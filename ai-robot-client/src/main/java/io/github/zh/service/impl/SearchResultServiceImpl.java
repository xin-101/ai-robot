package io.github.zh.service.impl;

import io.github.zh.model.SearchResult;
import io.github.zh.service.SearchResultService;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j

public class SearchResultServiceImpl implements SearchResultService {
    @Resource
    private OkHttpClient okHttpClient;

    @Resource(name = "httpRequestThreadPoolExecutor")
    private ThreadPoolTaskExecutor httpRequestThreadPoolExecutor;

    @Resource(name = "httpRequestThreadPoolExecutor")
    private ThreadPoolTaskExecutor resultProcessThreadPoolExecutor;




    @Override
    public CompletableFuture<List<SearchResult>> getSearchResults(List<SearchResult> searchResults,
                                                                  long timeout,
                                                                  TimeUnit unit) {
        return getHtmlContentConcurrently(searchResults, timeout, unit);
    }


    /**
     * 同步请求--通过提供的url获取html的内容
     */

    public String syncGetHtmlContent(String url) {
        if (StringUtils.isBlank(url)) return "";

        // 构建http的get请求
        Request req = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0")
                .header("Accept", "text/html")
                .build();

        try (Response response = okHttpClient.newCall(req).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return "";
            }
            return response.body().string();
        } catch (Exception e) {
            log.error("Error occurred while making HTTP request", e);
            return "";
        }

    }

    /**
     * 异步请求--通过提供的url获取html的内容
     */
    public CompletableFuture<SearchResult> getHtmlContentAsync(SearchResult searchResult,
                                                               long timeout,
                                                               TimeUnit unit) {

        // 异步线程进行处理
        return CompletableFuture.supplyAsync(() -> {
            // 获取html内容
            String html = syncGetHtmlContent(searchResult.getUrl());


            return searchResult
                    .builder()
                    .url(searchResult.getUrl())
                    .content(html)
                    .score(searchResult.getScore())
                    .build();
        }, httpRequestThreadPoolExecutor)
                .completeOnTimeout(getHtmlContentFallback(searchResult), timeout, unit)
                .exceptionally(ex -> {
                    log.error("Error occurred while making HTTP request", ex);
                    // 降级的一个方法 比如返回一个空的字符串
                    return getHtmlContentFallback(searchResult);
                });
    }

    /**
     * 降级方法
     */
    public SearchResult getHtmlContentFallback(SearchResult searchResult) {
        return searchResult
                .builder()
                .url(searchResult.getUrl())
                .content("")
                .score(searchResult.getScore())
                .build();
    }

    /**
     * 通过并发获取多个html内容
     */
    public CompletableFuture<List<SearchResult>> getHtmlContentConcurrently(List<SearchResult> searchResults,
                                                                            long timeout,
                                                                            TimeUnit  unit){
        // 1. 为我们的所有搜索结果创建CompletableFuture
        List<CompletableFuture<SearchResult>> futures = searchResults.stream()
                .map(searchResult -> getHtmlContentAsync(searchResult, timeout, unit))
                .toList();
        // 2. 使用CompletableFuture.allOf()方法等待所有任务完成
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        // 3. 使用thenApply()方法将结果映射为List<SearchResult>
        return voidCompletableFuture.thenApplyAsync(v -> futures.stream()
                .map(m->{
                    SearchResult result=m.join();

                    String content = result.getContent(); // 获取html内容

                    if (StringUtils.isNotBlank(content)){
                        // 获取html内容
                        content= Jsoup.parse(content).text();

                        result.setContent(content);
                    }
                    return result;
                })
                .collect(Collectors.toList()),resultProcessThreadPoolExecutor );
    }

}

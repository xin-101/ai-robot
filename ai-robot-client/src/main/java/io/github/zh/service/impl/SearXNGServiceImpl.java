package io.github.zh.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.zh.config.OkHttpConfig;
import io.github.zh.model.SearchResult;
import io.github.zh.service.SearXNGService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearXNGServiceImpl implements SearXNGService {
    private final OkHttpConfig okHttpConfig;
    private final ChatClient client;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    @Value("${searxng.url}")
    private String url;
    @Value("${searxng.count}")
    private int count;


    @Override
    public List<SearchResult> search(String query) {


        // 1.构建请求
        HttpUrl httpUrl = HttpUrl.parse(url)
                .newBuilder()
                .addQueryParameter("q", query)
                .addQueryParameter("format", "json")
                .addQueryParameter("engines", "bing,baidu,quark")
                .build();

        // 2.创建GET请求
        Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .build();


        // 3.发送http请求
        try (Response response = okHttpClient.newCall(request).execute()) {
            // 3.1 获取响应
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                log.info("====== searXNG 搜索结果：{}", responseBody);

                // 3.2 解析响应
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                JsonNode results = jsonNode.get("results");

                // 定义一个Record类型
                record SearchResultNode(Double score, JsonNode results) {
                }

                // 3.3 处理搜索出的结果
                List<SearchResultNode> searchResultNodeList = StreamSupport.stream(results.spliterator(), false)
                        .map(node -> {
                            double score = node.path("score").asDouble(0.0);
                            return new SearchResultNode(score, node);
                        })
                        .sorted(Comparator.comparing(SearchResultNode::score).reversed())
                        .limit(count)
                        .toList();

                // 转换成SearchResult对象
                List<SearchResult> searchResults = searchResultNodeList.stream()
                        .map(node -> {
                            JsonNode results1 = node.results();
                            String resultUrl = results1.path("url").asText("");
                            String content = results1.path("content").asText("");


                            return SearchResult.builder()
                                    .content(content)
                                    .url(resultUrl)
                                    .score(node.score())
                                    .build();


                        })
                        .collect(Collectors.toList());
                return searchResults;
            }


        } catch (IOException e) {
            log.error("Error occurred while HTTP request", e);
        }


        return List.of();
    }


}

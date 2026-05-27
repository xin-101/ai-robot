package io.github.zh.service;

import io.github.zh.model.SearchResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface SearchResultService {

    /**
     * 通过线程池技术并发获取最大量的搜索结果
     */
    CompletableFuture<List<SearchResult>> getSearchResults(List<SearchResult> searchResults,
                                                           long timeout,
                                                           TimeUnit  unit);


}

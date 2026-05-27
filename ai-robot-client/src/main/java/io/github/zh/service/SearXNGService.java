package io.github.zh.service;

import io.github.zh.model.SearchResult;

import java.util.List;

public interface SearXNGService {

    /**
     * 调用SearXNG服务api 进行搜索获取结果
     */
    List<SearchResult> search(String query);

}

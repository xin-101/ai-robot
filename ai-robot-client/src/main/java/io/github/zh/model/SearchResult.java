package io.github.zh.model;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResult {

    // 页面内容
    private String content;

    // 页面访问链接
    private String url;

    // 评分
    private Double score;
}

package io.github.zh.controller;


import io.github.zh.advisor.NetworkSearchAdvisor;
import io.github.zh.model.SearchResult;
import io.github.zh.service.SearXNGService;
import io.github.zh.service.impl.SearchResultServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/network")
public class SearXNGController {
    @Resource
    private SearXNGService searXNGService;
    @Resource
    private SearchResultServiceImpl searchResultService;
    @Resource
    private ChatClient chatClient;


    // http://localhost:8888/network/search?query=
    @GetMapping("/search")
    public List<SearchResult> search(String query) {
        return searXNGService.search(query);
    }

    @GetMapping("/searchToAi")
    public List<SearchResult> searchToAi(String query) {
        CompletableFuture<List<SearchResult>> searchResults = searchResultService.getSearchResults(searXNGService.search( query), 10, TimeUnit.SECONDS);
        return searchResults.join();
    }

    /**
     * 流式对话   http://localhost:8888/network/chat?msg=
     */
    @RequestMapping(value = "/chat",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(String msg, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        return chatClient.prompt()
                .user(msg)
                .advisors(new NetworkSearchAdvisor(searXNGService, searchResultService))
                .stream().content();
    }
}

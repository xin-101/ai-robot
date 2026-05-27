package io.github.zh.advisor;

import io.github.zh.model.SearchResult;
import io.github.zh.service.SearXNGService;
import io.github.zh.service.SearchResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import reactor.core.publisher.Flux;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NetworkSearchAdvisor implements StreamAdvisor {

    private final SearXNGService searXNGService;
    private final SearchResultService searchResultService;
    // 提示词
    private static final PromptTemplate PROMPT = new PromptTemplate("""
            ## 用户问题
            {question}
                        
            ## 上下文
            {context}
                        
            请根据上下文内容来回答用户：
                       
            ## 回答要求
            1.综合上下文内容，提取与用户提出问题的相关核心信息
            2.如果查询到，回复具体的内容
            3.请关注匹配度较高的搜索结果
            4.当无法查询到内容的时候，输出"没有找到匹配的答案"
                       
            """);


    public NetworkSearchAdvisor(SearXNGService searXNGService, SearchResultService searchResultService) {
        this.searXNGService = searXNGService;
        this.searchResultService = searchResultService;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {

        // 1. 获取用户问题
        Prompt prompt = chatClientRequest.prompt();
        UserMessage userMessage = prompt.getUserMessage();

        // 2.获取搜索结果
        List<SearchResult> searchResults = searXNGService.search(userMessage.getText());

        // 3.获取搜索结果内容
        CompletableFuture<List<SearchResult>> resultFuture = searchResultService.getSearchResults(searchResults, 10, TimeUnit.SECONDS);

        List<SearchResult> resultList = resultFuture.join();

        // 4.构造上下文的信息
        StringBuilder sb = new StringBuilder();
        for (SearchResult result : resultList) {
            sb.append(
                    String.format("""
                            ### 来源相关度：%s,
                            ### 来源链接：%s,
                            ### 页面文本：%s
                            """, result.getScore(), result.getUrl(), result.getContent())
            );
        }
        // 5.填充我们自己的提示词
        Prompt newPrompt = PROMPT.create(
                Map.of("question", userMessage.getText(),
                        "context", sb.toString())
        );
        ChatClientRequest clientRequest=ChatClientRequest.builder()
                .prompt(newPrompt)
                .build();

        return streamAdvisorChain.nextStream(clientRequest);
    }

    @Override
    // 当前类名
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    // 越小的优先级越高
    public int getOrder() {
        return 1;
    }
}

package io.github.zh.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/vector")
public class VectorStoreController {

    @Resource
    private VectorStore vectorStore;
    @Resource
    private ChatClient chatClient;

    /**
     * 查询向量数据库
     */
    @RequestMapping("/query")
    public List<Document> query(String query) {
        // 查询
        List<Document> res = vectorStore.similaritySearch(SearchRequest
                .builder()
                // 查询的关键词
                .query(query)
                // 只查询相似度高的
                .topK(2)
                .build());

        return res;
    }



    /**
     * 流式对话
     */
    @RequestMapping(value = "/rag",produces = "text/event-stream")
    public Flux<String> rag(String query, HttpServletResponse response) {
        
        response.setCharacterEncoding("utf-8");

        return chatClient.prompt()
                .system("你是一个技术领导，你会从内部知识库中查询资料，回答用户问题，如果没找到资料的话，回答：‘我不会’")
                .user(query)
                .advisors(new QuestionAnswerAdvisor(vectorStore)) // 添加向量数据库
                .stream().content();

    }
}

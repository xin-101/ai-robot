package io.github.zh.controller;

import io.github.zh.model.AIResponse;
import io.github.zh.service.ChatService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/test")
public class Test {

    @Resource
    private ChatService chatService;

    @GetMapping("/hello")
    public String hello(String msg) {
        return chatService.ChatTest(msg);
    }

    @GetMapping("/Flux")
    public Flux<String> flux(String msg, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        return chatService.ChatFluxTest(msg);
    }

    @GetMapping("/chatFlux")
    public Flux<String> chatFlux(String msg, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        return chatService.chatFlux(msg);
    }

    @GetMapping("/chatFluxMemory")
    public Flux<String> chatFlux(String msg,
                                 String chatId,
                                 HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        return chatService.chatFlux(msg, chatId);
    }

    @GetMapping("/chatMemory")
    public Flux<String> chatMemory(String msg,
                                   HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        return chatService.chatMemory(msg);
    }

    @GetMapping(value = "/chatFluxSSE", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatFluxSSE(String msg, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        return chatService.chatFlux(msg);
    }

    @GetMapping(value = "/chatFluxSSEAIResponse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AIResponse> chatFluxSSEAIResponse(String msg, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        return chatService.chatFlux(msg)
                .map(s -> AIResponse.builder().content(s).build());
    }


}

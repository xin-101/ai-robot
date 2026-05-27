package io.github.zh.service.impl;

import io.github.zh.service.ChatService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatServiceImpl implements ChatService {
    @Override
    public Flux<String> ChatFluxText(String question) {
        return null;
    }

    @Override
    public Flux<String> chatFluxTest(String msg) {
        return null;
    }

    @Resource
    private OpenAiChatModel openAiChatModel;

    @Resource
    private ChatClient chatClient;

    @Override
    public String ChatTest(String question) {

        return openAiChatModel.call(question);
    }

    @Override
    public Flux<String> ChatFluxTest(String question) {
        return openAiChatModel.stream(question);
    }
    @Override
    public Flux<String> chatFlux(String msg) {
        return chatClient.prompt(msg).stream().content();
    }

    @Override
    public Flux<String> chatFlux(String msg, String chatId) {
        return null;
    }
    @Override
    public Flux<String> chatMemory(String msg) {
        return chatClient
                .prompt(msg)
                .stream()
                .content();
    }

    @Override
    public Flux<String> chatFlux(Prompt prompt) {
        return chatClient
                .prompt(prompt)
                .stream()
                .content();
    }
}

package io.github.zh.service;

import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

public interface ChatService {

    public String ChatTest(String question);

    Flux<String> ChatFluxTest(String msg);

    Flux<String> ChatFluxText(String question);

    Flux<String> chatFluxTest(String msg);

    Flux<String> chatFlux(String msg);

    Flux<String> chatFlux(String msg, String chatId);

    Flux<String> chatMemory(String msg);

    Flux<String> chatFlux(Prompt prompt);
}

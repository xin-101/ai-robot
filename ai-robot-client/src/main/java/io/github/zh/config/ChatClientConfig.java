package io.github.zh.config;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Resource
    private ChatMemory chatMemory;

    String systemPrompt =
            """
                    你是一个智能AI助手，你的名字是【小艾同学】，
                    你可以帮助我回答我的问题，解决我提的内容。
                    """;

    @Bean
    public ChatClient client(OpenAiChatModel openAiChatModel, ToolCallbackProvider toolCallbackProvider) {
        return ChatClient
                .builder(openAiChatModel)
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}

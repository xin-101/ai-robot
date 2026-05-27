package io.github.zh.config;


import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.cassandra.CassandraChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

    @Resource
    private CassandraChatMemoryRepository chatMemoryRepository;

    //记忆存储功能
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory
                .builder()
                // 最大消息窗口是50
                .maxMessages(50)
                // 存储消息
                .chatMemoryRepository(chatMemoryRepository)
                .build();
    }
}

package io.github.zh.config;


import io.github.zh.tools.DateTool;
import io.github.zh.tools.EmailTool;
import io.github.zh.tools.WeatherTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolCallBackRegisterConfig {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(DateTool dateTool,
                                                     EmailTool emailTool,
                                                     WeatherTool weatherTool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(dateTool,emailTool,weatherTool)
                .build();
    }

}

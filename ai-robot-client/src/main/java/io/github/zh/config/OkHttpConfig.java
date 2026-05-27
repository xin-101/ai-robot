package io.github.zh.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {


    @Bean
    public OkHttpClient okHttpClient(
            @Value("${okhttp.connection-timeout}") int connectionTimeout,
            @Value("${okhttp.read-timeout}") int readTimeout,
            @Value("${okhttp.write-timeout}") int writeTimeout,
            @Value("${okhttp.max-idle-connections}") int maxIdleConnections,
            @Value("${okhttp.keep-alive-duration}") long keepAliveDuration
    )

    {
        return new OkHttpClient.Builder()
                .connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MILLISECONDS))
                .build();
    }
}

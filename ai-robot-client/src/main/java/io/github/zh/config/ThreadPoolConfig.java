package io.github.zh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    /**
     * HTTP请求线程池
     */
@Bean("httpRequestThreadPoolExecutor")
    public ThreadPoolTaskExecutor httpRequestThreadPoll(){
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(20); // 核心线程数
    executor.setMaxPoolSize(100); // 最大线程数
    executor.setQueueCapacity(200); // 队列容量
    executor.setKeepAliveSeconds(120); // 线程空闲时间
    executor.setThreadNamePrefix("http-request-thread-"); // 线程前缀名
    executor.initialize(); // 初始化

    return executor;
}

    /**
     * 结果处理线程池
     */

    @Bean("resultProcessThreadPoolExecutor")
    public ThreadPoolTaskExecutor resultProcessThreadPoolExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors()); // 核心线程数(cpu核心数)
        executor.setMaxPoolSize(2*Runtime.getRuntime().availableProcessors()); // 最大线程数
        executor.setQueueCapacity(200); // 队列容量
        executor.setKeepAliveSeconds(120); // 线程空闲时间
        executor.setThreadNamePrefix("result-process-thread-"); // 线程前缀名
        executor.initialize(); // 初始化

        return executor;
    }

}

package com.critiquehub.util.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${app.async.core-pool-size}")
    private int corePoolSize;

    @Value("${app.async.max-pool-size}")
    private int maxPoolSize;

    @Value("${app.async.queue-capacity}")
    private int queueCapacity;

    @Value("${app.async.thread-name-prefix}")
    private String threadNamePrefix;

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);

        // Стратегия на случай переполнения очереди (например, при твоих 1 млн запросов в JMeter)
        // CallerRunsPolicy заставит главный поток выполнить задачу, если пул перегружен, вместо падения с ошибкой
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}

package com.devpilotfml.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestClient;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
// When index api is called the response will be generated instantly offloading all work to these threads
public class AppConfig {
    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean(name = "indexingExecutor")
    Executor indexingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Setup
        // Main 2 worker threads
        executor.setCorePoolSize(2);
        // Can extend upto 4
        executor.setMaxPoolSize(4);
        // Max pending tasks
        executor.setQueueCapacity(50);
        // Name of threads
        executor.setThreadNamePrefix("index-");
        // Build with these configs
        executor.initialize();

        return executor;
    }
}

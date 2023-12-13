package com.example.demo.application.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @see <a href="https://jeong-pro.tistory.com/253">ThreadPoolTaskExecutor 설정 고민해보기</a>
 * @see <a href="https://engineering.zalando.com/posts/2019/04/how-to-set-an-ideal-thread-pool-size.html">How to set an ideal thread pool size</a>
 */
@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(25);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.setQueueCapacity(500);
        taskExecutor.setKeepAliveSeconds(30);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(5);
        taskExecutor.setThreadNamePrefix("async-thread-");
        return taskExecutor;
    }

}

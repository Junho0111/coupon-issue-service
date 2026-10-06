package com.apiece.coupon.infrastructure.messaging;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncIssuanceConfig {

    public static final String ISSUANCE_TASK_EXECUTOR = "issuanceTaskExecutor";

    @Bean(name = ISSUANCE_TASK_EXECUTOR)
    public Executor issuanceTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(10_000);
        executor.setThreadNamePrefix("issuance-async-"); // 스레드 로깅할때 나타내는것
        executor.setWaitForTasksToCompleteOnShutdown(true); // 갑자기 애플리케이션이 종료가 될때 스레드 풀에서 작업중인것은 완료를 하고 셧다운이 되도록
        executor.setAwaitTerminationSeconds(30); // 종료하기 전에 30초 정도 여유를 둠
        executor.initialize();
        return executor;
    }
}

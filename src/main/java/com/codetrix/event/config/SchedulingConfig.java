package com.codetrix.event.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("event-scheduler-");
        scheduler.setErrorHandler(t -> {
            // Log but don't crash the scheduler
            org.slf4j.LoggerFactory.getLogger(SchedulingConfig.class)
                    .error("Scheduler error: {}", t.getMessage(), t);
        });
        scheduler.initialize();
        return scheduler;
    }
}

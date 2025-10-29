package com.example.timecapsule_backend.config.scheduler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean(name = "taskScheduler")
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5); // 동시에 해결할 스케줄러 스레드 수, 필요에 맞게 조절
        taskScheduler.setThreadNamePrefix("capsule_task_scheduler-");
        taskScheduler.setAwaitTerminationSeconds(30);
        taskScheduler.setRemoveOnCancelPolicy(true);
        taskScheduler.initialize();
        return taskScheduler;
    }
}

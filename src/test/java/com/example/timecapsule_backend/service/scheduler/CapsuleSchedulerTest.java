package com.example.timecapsule_backend.service.scheduler;

import com.example.timecapsule_backend.config.scheduler.CapsuleSchedulerProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CapsuleScheduler 테스트")
class CapsuleSchedulerTest {

    @Mock
    private CapsuleSchedulerProperties capsuleSchedulerProperties;

    @Mock
    private CapsuleSchedulerService capsuleSchedulerService;

    @InjectMocks
    private CapsuleScheduler capsuleScheduler;

    @Test
    @DisplayName("스케줄 트리거 실행 시 서비스 호출")
    void scheduledTrigger_callsService() {
        // when
        capsuleScheduler.scheduledTrigger();

        // then
        verify(capsuleSchedulerService).runSchedulingCycle();
    }

    @Test
    @DisplayName("스케줄 트리거 여러 번 호출 시 모든 호출이 서비스로 전달")
    void scheduledTrigger_multipleCalls() {
        // when
        capsuleScheduler.scheduledTrigger();
        capsuleScheduler.scheduledTrigger();
        capsuleScheduler.scheduledTrigger();

        // then
        verify(capsuleSchedulerService, times(3)).runSchedulingCycle();
    }
}
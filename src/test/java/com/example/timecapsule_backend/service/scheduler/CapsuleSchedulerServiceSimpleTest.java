package com.example.timecapsule_backend.service.scheduler;

import com.example.timecapsule_backend.config.scheduler.CapsuleSchedulerProperties;
import com.example.timecapsule_backend.domain.capsule.Capsule;
import com.example.timecapsule_backend.domain.capsule.CapsuleRepository;
import com.example.timecapsule_backend.domain.capsule.CapsuleStatus;
import com.example.timecapsule_backend.service.delivery.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CapsuleSchedulerService 간단 테스트")
class CapsuleSchedulerServiceSimpleTest {

    @Mock
    private CapsuleSchedulerProperties schedulerProperties;

    @Mock
    private CapsuleRepository capsuleRepository;

    @Mock
    private DeliveryService deliveryService;

    @Mock
    private Capsule mockCapsule;

    private CapsuleSchedulerService capsuleSchedulerService;

    @BeforeEach
    void setUp() {
        capsuleSchedulerService = new CapsuleSchedulerService(schedulerProperties, capsuleRepository, deliveryService);
    }

    @Test
    @DisplayName("발송 대상 캡슐이 없을 때 정상 처리")
    void runSchedulingCycle_noDueCapsules() {
        // given
        when(capsuleRepository.findAndLockDueForDispatch(any(CapsuleStatus.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // when
        capsuleSchedulerService.runSchedulingCycle();

        // then
        verify(capsuleRepository).findAndLockDueForDispatch(eq(CapsuleStatus.SCHEDULED), any(LocalDateTime.class));
        verify(deliveryService, never()).dispatch(anyLong());
    }

    @Test
    @DisplayName("발송 대상 캡슐이 있을 때 성공적으로 발송")
    void runSchedulingCycle_successfulDelivery() throws Exception {
        // given
        when(mockCapsule.getId()).thenReturn(1L);
        when(capsuleRepository.findAndLockDueForDispatch(any(CapsuleStatus.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(mockCapsule));

        // when
        capsuleSchedulerService.runSchedulingCycle();

        // then
        verify(capsuleRepository).findAndLockDueForDispatch(eq(CapsuleStatus.SCHEDULED), any(LocalDateTime.class));
        verify(deliveryService).dispatch(1L);
        verify(capsuleRepository, never()).save(any());
    }

    @Test
    @DisplayName("발송 실패 시 재시도 로직 동작")
    void runSchedulingCycle_deliveryFailure() throws Exception {
        // given
        when(mockCapsule.getId()).thenReturn(1L);
        when(mockCapsule.getRetryCount()).thenReturn(0);
        when(schedulerProperties.getMaxRetries()).thenReturn(3);
        when(schedulerProperties.getBaseBackoffSeconds()).thenReturn(30L);
        
        when(capsuleRepository.findAndLockDueForDispatch(any(CapsuleStatus.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(mockCapsule));
        
        doThrow(new RuntimeException("발송 실패")).when(deliveryService).dispatch(1L);

        // when
        capsuleSchedulerService.runSchedulingCycle();

        // then
        verify(deliveryService).dispatch(1L);
        verify(mockCapsule).markFailedAttempt(30L, 3);
        verify(capsuleRepository).save(mockCapsule);
    }
}
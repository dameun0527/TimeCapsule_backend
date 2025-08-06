package com.example.timecapsule_backend.service.scheduler;

import com.example.timecapsule_backend.config.scheduler.CapsuleSchedulerProperties;
import com.example.timecapsule_backend.domain.capsule.Capsule;
import com.example.timecapsule_backend.domain.capsule.CapsuleRepository;
import com.example.timecapsule_backend.domain.capsule.CapsuleStatus;
import com.example.timecapsule_backend.domain.user.User;
import com.example.timecapsule_backend.service.delivery.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
@DisplayName("CapsuleSchedulerService 테스트")
class CapsuleSchedulerServiceTest {

    @MockBean
    private CapsuleSchedulerProperties schedulerProperties;

    @MockBean
    private CapsuleRepository capsuleRepository;

    @MockBean
    private DeliveryService deliveryService;

    private CapsuleSchedulerService capsuleSchedulerService;

    private User testUser;
    private Capsule testCapsule1;
    private Capsule testCapsule2;

    @BeforeEach
    void setUp() {
        capsuleSchedulerService = new CapsuleSchedulerService(schedulerProperties, capsuleRepository, deliveryService);
        
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testUser")
                .build();

        testCapsule1 = Capsule.builder()
                .id(1L)
                .user(testUser)
                .status(CapsuleStatus.SCHEDULED)
                .scheduledAt(LocalDateTime.now().minusMinutes(10))
                .retryCount(0)
                .build();

        testCapsule2 = Capsule.builder()
                .id(2L)
                .user(testUser)
                .status(CapsuleStatus.SCHEDULED)
                .scheduledAt(LocalDateTime.now().minusMinutes(5))
                .retryCount(0)
                .build();

        when(schedulerProperties.getMaxRetries()).thenReturn(3);
        when(schedulerProperties.getBaseBackoffSeconds()).thenReturn(30L);
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
        verify(capsuleRepository, never()).save(any(Capsule.class));
    }

    @Test
    @DisplayName("발송 대상 캡슐이 있을 때 성공적으로 발송")
    void runSchedulingCycle_successfulDelivery() throws Exception {
        // given
        List<Capsule> dueCapsules = Arrays.asList(testCapsule1, testCapsule2);
        when(capsuleRepository.findAndLockDueForDispatch(any(CapsuleStatus.class), any(LocalDateTime.class)))
                .thenReturn(dueCapsules);

        // when
        capsuleSchedulerService.runSchedulingCycle();

        // then
        verify(capsuleRepository).findAndLockDueForDispatch(eq(CapsuleStatus.SCHEDULED), any(LocalDateTime.class));
        verify(deliveryService).dispatch(1L);
        verify(deliveryService).dispatch(2L);
        verify(capsuleRepository, never()).save(any(Capsule.class));
    }

    @Test
    @DisplayName("발송 실패 시 재시도 로직 동작")
    void runSchedulingCycle_deliveryFailure() throws Exception {
        // given
        List<Capsule> dueCapsules = Arrays.asList(testCapsule1);
        when(capsuleRepository.findAndLockDueForDispatch(any(CapsuleStatus.class), any(LocalDateTime.class)))
                .thenReturn(dueCapsules);
        
        doThrow(new RuntimeException("발송 실패")).when(deliveryService).dispatch(1L);

        // when
        capsuleSchedulerService.runSchedulingCycle();

        // then
        verify(capsuleRepository).findAndLockDueForDispatch(eq(CapsuleStatus.SCHEDULED), any(LocalDateTime.class));
        verify(deliveryService).dispatch(1L);
        verify(capsuleRepository).save(testCapsule1);
        
        // 실패 처리 확인 (markFailedAttempt 호출됨)
        verify(schedulerProperties).getBaseBackoffSeconds();
        verify(schedulerProperties).getMaxRetries();
    }

    @Test
    @DisplayName("일부 캡슐 발송 성공, 일부 실패 시 혼합 처리")
    void runSchedulingCycle_mixedResults() throws Exception {
        // given
        List<Capsule> dueCapsules = Arrays.asList(testCapsule1, testCapsule2);
        when(capsuleRepository.findAndLockDueForDispatch(any(CapsuleStatus.class), any(LocalDateTime.class)))
                .thenReturn(dueCapsules);
        
        // 첫 번째 캡슐은 성공, 두 번째 캡슐은 실패
        doNothing().when(deliveryService).dispatch(1L);
        doThrow(new RuntimeException("네트워크 오류")).when(deliveryService).dispatch(2L);

        // when
        capsuleSchedulerService.runSchedulingCycle();

        // then
        verify(deliveryService).dispatch(1L);
        verify(deliveryService).dispatch(2L);
        
        // 실패한 캡슐만 저장됨
        verify(capsuleRepository).save(testCapsule2);
        verify(capsuleRepository, never()).save(testCapsule1);
    }

    @Test
    @DisplayName("스케줄링 사이클 실행 시 현재 시간으로 조회")
    void runSchedulingCycle_usesCurrentTime() {
        // given
        when(capsuleRepository.findAndLockDueForDispatch(any(CapsuleStatus.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // when
        capsuleSchedulerService.runSchedulingCycle();

        // then
        verify(capsuleRepository).findAndLockDueForDispatch(
                eq(CapsuleStatus.SCHEDULED), 
                argThat(time -> time.isBefore(LocalDateTime.now().plusSeconds(1)) && 
                               time.isAfter(LocalDateTime.now().minusSeconds(1)))
        );
    }
}
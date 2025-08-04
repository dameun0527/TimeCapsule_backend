package com.example.timecapsule_backend.domain.capsule;

import com.example.timecapsule_backend.domain.user.Role;
import com.example.timecapsule_backend.domain.user.User;
import com.example.timecapsule_backend.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class CapsuleRepositoryTest {

    @Autowired
    private CapsuleRepository capsuleRepository;

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("JWT_SECRET_KEY", () -> "dummy");
    }

    @Test
    void findByStatusAndScheduledAtBefore_과거_스케줄_캡슐_조회() {
        // 1. User 하나 만들고 저장
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("010-1234-5678")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        // 2. Capsule cap = Capsule.of(user);
        Capsule cap = Capsule.of(user);

        // 3. cap.schedule(LocalDateTime.now().minusMinutes(5)); // 과거로 해서 대상
        cap.schedule(LocalDateTime.now().minusMinutes(5));

        // 4. capsuleRepository.save(cap);
        capsuleRepository.save(cap);

        // 5. capsuleRepository.findByStatusAndScheduledAtBefore(CapsuleStatus.SCHEDULED, LocalDateTime.now()) 호출해서 방금 만든 캡슐이 나오는지 확인
        List<Capsule> results = capsuleRepository.findByStatusAndScheduledAtBefore(
                CapsuleStatus.SCHEDULED, 
                LocalDateTime.now()
        );

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(cap.getId());
        assertThat(results.get(0).getStatus()).isEqualTo(CapsuleStatus.SCHEDULED);
        assertThat(results.get(0).getScheduledAt()).isBefore(LocalDateTime.now());
    }
}
package com.example.timecapsule_backend.domain.capsule;

import com.example.timecapsule_backend.domain.base.BaseEntity;
import com.example.timecapsule_backend.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "capsules")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Capsule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CapsuleStatus status;

    // 발송 예약 시각
    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    // 발송 완료 시각
    @Column(name = "executed_at")
    private LocalDateTime executedAt;
}

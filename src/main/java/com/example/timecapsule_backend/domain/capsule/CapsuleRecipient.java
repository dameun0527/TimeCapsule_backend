package com.example.timecapsule_backend.domain.capsule;

import com.example.timecapsule_backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "capsule_recipients",
uniqueConstraints = @UniqueConstraint(columnNames = {"capsule_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CapsuleRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "capsule_id", nullable = false)
    private Capsule capsule;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(name = "invited_at", nullable = false)
    private LocalDateTime invitedAt;

    // 발송 상태 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 20)
    @Builder.Default
    private RecipientDeliveryStatus deliveryStatus = RecipientDeliveryStatus.PENDING;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private int retryCount = 0;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    protected CapsuleRecipient(Capsule capsule, User user) {
        this.capsule = capsule;
        this.user = user;
        this.deliveryStatus = RecipientDeliveryStatus.PENDING;
        this.retryCount = 0;
    }

    public static CapsuleRecipient of(Capsule capsule, User user) {
        return new CapsuleRecipient(capsule, user);
    }

    // 발송 성공 처리
    public void markDelivered() {
        this.deliveryStatus = RecipientDeliveryStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
        this.failureReason = null;
    }

    // 발송 실패 처리 (재시도 가능)
    public void markFailedAttempt(String reason, int maxRetries) {
        this.retryCount++;
        this.failureReason = reason;

        if (this.retryCount >= maxRetries) {
            // 최대 재시도 횟수 초과 시 최종 실패
            this.deliveryStatus = RecipientDeliveryStatus.FAILED;
        } else {
            // 재시도 가능하면 PENDING 유지
            this.deliveryStatus = RecipientDeliveryStatus.PENDING;
        }
    }

    // 발송 실패 여부 확인
    public boolean isDelivered() {
        return this.deliveryStatus == RecipientDeliveryStatus.DELIVERED;
    }

    // 재시도 필요 여부 확인
    public boolean needsRetry() {
        return this.deliveryStatus == RecipientDeliveryStatus.PENDING && this.retryCount > 0;
    }

    // 최종 실패 여부 확인
    public boolean isFinallyFailed() {
        return this.deliveryStatus == RecipientDeliveryStatus.FAILED;
    }
}

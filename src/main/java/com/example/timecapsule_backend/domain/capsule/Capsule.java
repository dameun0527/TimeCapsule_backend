package com.example.timecapsule_backend.domain.capsule;

import com.example.timecapsule_backend.domain.base.BaseEntity;
import com.example.timecapsule_backend.domain.user.User;
import com.example.timecapsule_backend.ex.BusinessException;
import com.example.timecapsule_backend.ex.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "capsules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Capsule extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CapsuleStatus status;


    // 발송 예약 시각
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;


    // 실제 발송 시각
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;


    // 메시지 내용
    @OneToOne(mappedBy = "capsule", cascade = CascadeType.ALL, orphanRemoval = true)
    private CapsuleContent content;


    // 테마
    @OneToOne(mappedBy = "capsule", cascade = CascadeType.ALL, orphanRemoval = true)
    private CapsuleTheme theme;


    // 그룹 캡슐 수신자
    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CapsuleRecipient> recipients = new HashSet<>();


    // 첨부 파일
    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Attachment> attachments = new ArrayList<>();

    public static Capsule of(User user) {
        return Capsule.builder()
                .user(user)
                .status(CapsuleStatus.PENDING)
                .build();
    }

    // 재시도 횟수
    @Column(nullable = false)
    @Builder.Default
    private int retryCount = 0;

    private LocalDateTime nextAttemptAt;

    @Version
    private Long version;

    public void addContent(CapsuleContent content) {
        this.content = content;
        content.setCapsule(this);
    }

    public void addTheme(CapsuleTheme theme) {
        this.theme = theme;
        theme.setCapsule(this);
    }

    public void addRecipient(CapsuleRecipient recipient) {
        recipients.add(recipient);
        recipient.setCapsule(this);
    }

    public void clearRecipients() {
        recipients.forEach(recipient -> recipient.setCapsule(null));
        recipients.clear();
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
        attachment.setCapsule(this);
    }

    public void clearAttachments() {
        attachments.forEach(attachment -> attachment.setCapsule(null));
        attachments.clear();
    }

    public void schedule(LocalDateTime when) {
        if (!(status == CapsuleStatus.PENDING)) {
            throw new BusinessException(ErrorCode.SCHEDULE_CREATION_NOT_ALLOWED);
        }
        this.scheduledAt = when;
        this.status = CapsuleStatus.SCHEDULED;
    }

    public void updateSchedule(LocalDateTime newScheduledAt) {
        if(status != CapsuleStatus.SCHEDULED) {
            throw new BusinessException(ErrorCode.SCHEDULE_UPDATE_NOT_ALLOWED);
        }
        this.scheduledAt = newScheduledAt;
    }

    public void markDelivered() {
        if (status != CapsuleStatus.SCHEDULED) {
            throw new BusinessException(ErrorCode.DELIVER_NOT_ALLOWED);
        }
        this.deliveredAt = LocalDateTime.now();
        this.status = CapsuleStatus.DELIVERED;
    }

    public void markFailed() {
        if (status != CapsuleStatus.SCHEDULED) {
            throw new BusinessException(ErrorCode.DELIVER_NOT_ALLOWED);
        }
        this.status = CapsuleStatus.FAILED;
    }

    public void markFailedAttempt(long backoffSeconds, int maxRetries) {
        if (status != CapsuleStatus.SCHEDULED) {
            throw new BusinessException(ErrorCode.DELIVER_NOT_ALLOWED);
        }
        this.retryCount += 1;
        if (this.retryCount < maxRetries) {
            this.nextAttemptAt = LocalDateTime.now().plusSeconds(backoffSeconds);
        } else {
            this.status = CapsuleStatus.FAILED;
        }
    }

    public void cancel() {
        if (!(status == CapsuleStatus.PENDING || status == CapsuleStatus.SCHEDULED)) {
            throw new BusinessException(ErrorCode.CANCEL_NOT_ALLOWED);
        }
        this.status = CapsuleStatus.CANCELLED;
    }

    // 수신자 발송 상태를 기반으로 캡슐 상태 업데이트
    public void updateStatusBasedOnRecipients(long baseBackoffSeconds) {
        if (recipients.isEmpty()) {
            return;
        }

        long deliveredCount = recipients.stream()
                .filter(CapsuleRecipient::isDelivered)
                .count();
        long failedCount = recipients.stream()
                .filter(CapsuleRecipient::isFinallyFailed)
                .count();
        long totalCount = recipients.size();

        if (deliveredCount == totalCount) {
            // 모든 수신자 성공
            this.status = CapsuleStatus.DELIVERED;
            this.deliveredAt = LocalDateTime.now();
            this.nextAttemptAt = null;
        } else if (failedCount == totalCount) {
            // 모든 수신자 최종 실패
            this.status = CapsuleStatus.FAILED;
            this.nextAttemptAt = null;
        } else if (deliveredCount > 0 && failedCount > 0) {
            // 일부 성공, 일부 최종 실패
            this.status = CapsuleStatus.PARTIALLY_DELIVERED;
            this.deliveredAt = LocalDateTime.now();
            this.nextAttemptAt = null;
        } else {
            // 일부 성공, 나머지는 재시도 대기 중 -> SCHEDULED 유지
            this.status = CapsuleStatus.SCHEDULED;

            // 재시도가 필요한 수신자의 최대 retryCount 기준으로 backoff 계산
            int maxRetryCount = recipients.stream()
                    .filter(r -> r.getDeliveryStatus() == RecipientDeliveryStatus.PENDING)
                    .mapToInt(CapsuleRecipient::getRetryCount)
                    .max()
                    .orElse(0);

            long backoffSeconds = baseBackoffSeconds * (maxRetryCount + 1);
            this.nextAttemptAt = LocalDateTime.now().plusSeconds(backoffSeconds);
        }
    }

    // 재시도가 필요한 수신자가 있는지 확인
    public boolean hasRecipientsNeedingRetry() {
        return recipients.stream()
                .anyMatch(r -> r.getDeliveryStatus() == RecipientDeliveryStatus.PENDING && r.getRetryCount() > 0);
    }

    // 아직 발송되지 않은 수신자 목록
    public List<CapsuleRecipient> getPendingRecipients() {
        return recipients.stream()
                .filter(r -> r.getDeliveryStatus() == RecipientDeliveryStatus.PENDING)
                .toList();
    }
}

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

    public void cancel() {
        if (!(status == CapsuleStatus.PENDING || status == CapsuleStatus.SCHEDULED)) {
            throw new BusinessException(ErrorCode.CANCEL_NOT_ALLOWED);
        }
        this.status = CapsuleStatus.CANCELLED;
    }
}

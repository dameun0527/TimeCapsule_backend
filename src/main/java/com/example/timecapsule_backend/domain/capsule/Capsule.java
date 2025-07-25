package com.example.timecapsule_backend.domain.capsule;

import com.example.timecapsule_backend.domain.base.BaseEntity;
import com.example.timecapsule_backend.domain.user.User;
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
    @Column(name = "scheduled_at", nullable = false)
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
    private Set<CapsuleRecipient> recipients = new HashSet<>();


    // 첨부 파일
    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();
}

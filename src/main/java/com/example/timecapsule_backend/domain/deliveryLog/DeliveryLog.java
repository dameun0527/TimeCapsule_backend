package com.example.timecapsule_backend.domain.deliveryLog;

import com.example.timecapsule_backend.domain.base.BaseEntity;
import com.example.timecapsule_backend.domain.capsule.Capsule;
import com.example.timecapsule_backend.domain.capsule.CapsuleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_logs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "capsule_id", nullable = false)
    private Capsule capsule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CapsuleStatus capsuleStatus;

    // 발송 시도 시각
    @Column(name = "attempted_at", nullable = false)
    private LocalDateTime attemptedAt;
}

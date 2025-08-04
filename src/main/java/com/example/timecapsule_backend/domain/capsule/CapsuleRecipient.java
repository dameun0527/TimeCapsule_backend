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

    protected CapsuleRecipient(Capsule capsule, User user) {
        this.capsule = capsule;
        this.user = user;
    }

    public static CapsuleRecipient of(Capsule capsule, User user) {
        return new CapsuleRecipient(capsule, user);
    }
}

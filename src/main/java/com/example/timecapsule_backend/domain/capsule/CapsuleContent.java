package com.example.timecapsule_backend.domain.capsule;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "capsule_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CapsuleContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capsule_id", nullable = false)
    private Capsule capsule;


    @Column(nullable = false, length = 100)
    private String title;


    @Lob
    @Column(nullable = false)
    private String mainMessage;

}

package com.example.timecapsule_backend.domain.capsule;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "capsule_themes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CapsuleTheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capsule_id", nullable = false)
    private Capsule capsule;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme_type", nullable = false, length = 30)
    private ThemeType themeType;

    @Column(name = "theme_metadata", columnDefinition = "JSON")
    private String themeMetadata;

}

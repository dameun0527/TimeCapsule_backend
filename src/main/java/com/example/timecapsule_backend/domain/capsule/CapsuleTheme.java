package com.example.timecapsule_backend.domain.capsule;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "capsule_themes")
@Getter
@Setter
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

    @Column(name = "theme_metadata")
    private String themeMetadata;

    public static CapsuleTheme of(Capsule capsule, ThemeType themeType, String themeMetadata) {
        CapsuleTheme theme = CapsuleTheme.builder()
                .themeType(themeType)
                .themeMetadata(themeMetadata)
                .build();
        theme.capsule = capsule;
        return theme;
    }

    public void update(ThemeType newType, String newMetadata) {
        if (themeType != null) this.themeType = newType;
        if (themeMetadata != null) this.themeMetadata = newMetadata;
    }
}

package com.example.timecapsule_backend.domain.capsule;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "capsule_contents")
@Getter
@Setter
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


    @Column(length = 50)
    private String alias;


    @Lob
    @Column(nullable = false)
    private String mainMessage;


    public static CapsuleContent of(Capsule capsule, String title, String alias, String mainMessage) {
        CapsuleContent content = CapsuleContent.builder()
                .title(title)
                .alias(alias)
                .mainMessage(mainMessage)
                .build();
        content.capsule = capsule;
        return content;
    }

    public void update(String title, String alis, String mainMessage) {
        if (title != null) this.title = title;
        if (alias != null) this.alias = alis;
        if (mainMessage != null) this.mainMessage = mainMessage;
    }
}

package com.example.timecapsule_backend.domain.capsule;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachments")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capsule_id", nullable = false)
    private Capsule capsule;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String storedUrl;

    public static Attachment of(Capsule capsule, String originalFilename, String storedUrl) {
        Attachment attachment = Attachment.builder()
                .originalFilename(originalFilename)
                .storedUrl(storedUrl)
                .build();
        attachment.capsule = capsule;
        return attachment;
    }
}

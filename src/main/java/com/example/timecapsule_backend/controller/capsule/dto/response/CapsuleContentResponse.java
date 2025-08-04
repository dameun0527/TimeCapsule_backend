package com.example.timecapsule_backend.controller.capsule.dto.response;

import com.example.timecapsule_backend.domain.capsule.CapsuleContent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CapsuleContentResponse {

    private final Long id;
    private final String title;
    private final String alias;
    private final String mainMessage;


    public static CapsuleContentResponse from(CapsuleContent capsuleContent) {
        return CapsuleContentResponse.builder()
                .id(capsuleContent.getId())
                .title(capsuleContent.getTitle())
                .alias(capsuleContent.getAlias())
                .mainMessage(capsuleContent.getMainMessage())
                .build();
    }
}

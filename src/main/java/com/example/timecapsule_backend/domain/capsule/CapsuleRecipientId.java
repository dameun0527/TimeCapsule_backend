package com.example.timecapsule_backend.domain.capsule;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class CapsuleRecipientId implements Serializable {
    private Long capsule;
    private Long user;
}

package com.example.timecapsule_backend.domain.capsule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapsuleRecipientRepository extends JpaRepository<CapsuleRecipient, Long> {
}

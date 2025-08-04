package com.example.timecapsule_backend.domain.capsule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CapsuleRepository extends JpaRepository<Capsule, Long> {

    List<Capsule> findByUserId(Long userId);
    Page<Capsule> findByUserId(Long userId, Pageable pageable);
    List<Capsule> findByStatusAndScheduledAtBefore(CapsuleStatus status, LocalDateTime now);
}

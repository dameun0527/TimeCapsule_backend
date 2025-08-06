package com.example.timecapsule_backend.domain.capsule;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CapsuleRepository extends JpaRepository<Capsule, Long> {

    List<Capsule> findByUserId(Long userId);
    Page<Capsule> findByUserId(Long userId, Pageable pageable);
    List<Capsule> findByStatusAndScheduledAtBefore(CapsuleStatus status, LocalDateTime now);

    // 스케줄러용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select c from Capsule c
            where c.status = :status
            and c.scheduledAt <= :now
            and (c.nextAttemptAt is null or c.nextAttemptAt <= :now)
            """)
    List<Capsule> findAndLockDueForDispatch(
            @Param("status") CapsuleStatus status,
            @Param("now") LocalDateTime now);
}

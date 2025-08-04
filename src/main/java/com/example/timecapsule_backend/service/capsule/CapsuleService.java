package com.example.timecapsule_backend.service.capsule;

import com.example.timecapsule_backend.controller.capsule.dto.request.CapsuleCreateRequest;
import com.example.timecapsule_backend.controller.capsule.dto.request.CapsuleUpdateRequest;
import com.example.timecapsule_backend.controller.capsule.dto.response.CapsuleResponse;
import com.example.timecapsule_backend.controller.capsule.dto.response.CapsuleSummaryResponse;
import com.example.timecapsule_backend.controller.delivery.dto.DeliveryLogResponse;
import com.example.timecapsule_backend.domain.capsule.Capsule;
import com.example.timecapsule_backend.domain.capsule.CapsuleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


public interface CapsuleService {

    CapsuleResponse createCapsule(Long userId, CapsuleCreateRequest request);

    CapsuleResponse getCapsule(Long userId, Long capsuleId);

    Page<CapsuleSummaryResponse> listCapsules(Long userId, Pageable pageable);

    CapsuleResponse updateCapsule(Long userId, Long capsuleId, CapsuleUpdateRequest request);

    void deleteCapsule(Long userId, Long capsuleId);

    List<Long> findDueCapsuleIds(LocalDateTime now);

    void changeStatus(Long capsuleId, CapsuleStatus status);

    List<DeliveryLogResponse> getDeliveryLogs(Long userId, Long capsuleId);
}
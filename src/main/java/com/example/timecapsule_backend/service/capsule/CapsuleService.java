package com.example.timecapsule_backend.service.capsule;

import com.example.timecapsule_backend.controller.capsule.dto.request.CapsuleCreateRequest;
import com.example.timecapsule_backend.controller.capsule.dto.request.CapsuleUpdateRequest;
import com.example.timecapsule_backend.controller.capsule.dto.response.CapsuleResponse;
import com.example.timecapsule_backend.controller.capsule.dto.response.CapsuleSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CapsuleService {

    CapsuleResponse createCapsule(Long userId, CapsuleCreateRequest request);

    CapsuleResponse getCapsule(Long userId, Long capsuleId);

    Page<CapsuleSummaryResponse> listCapsules(Long userId, Pageable pageable);

    CapsuleResponse updateCapsule(Long userId, Long capsuleId, CapsuleUpdateRequest request);

    void deleteCapsule(Long userId, Long capsuleId);
}
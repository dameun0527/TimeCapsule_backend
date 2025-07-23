package com.example.timecapsule_backend.service;

import com.example.timecapsule_backend.controller.capsule.dto.CapsuleCreateRequest;
import com.example.timecapsule_backend.controller.capsule.dto.CapsuleResponse;
import com.example.timecapsule_backend.controller.delivery.dto.DeliveryLogResponse;
import com.example.timecapsule_backend.domain.capsule.Capsule;
import com.example.timecapsule_backend.domain.capsule.CapsuleStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CapsuleService {

    /**
     * 타임캡슐 생성
     * @param request 생성 요청 정보
     * @param userId 사용자 ID
     * @return 생성된 캡슐 정보
     */
    @Transactional
    public CapsuleResponse createCapsule(CapsuleCreateRequest request, Long userId) {
        //  사용자 확인
        //  캡슐 생성
        //  첨부파일 처리
        //  스케줄링 등록
        //  응답 반환
        return null;
    }

    /**
     * 내 캡슐 목록 조회 (페이징)
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 캡슐 목록
     */
    public Page<CapsuleResponse> getMyCapsules(Long userId, Pageable pageable) {
        //  사용자별 캡슐 조회
        //  페이징 처리
        //  응답 변환
        return null;
    }

    /**
     * 캡슐 상세 조회
     * @param capsuleId 캡슐 ID
     * @param userId 사용자 ID
     * @return 캡슐 상세 정보
     */
    public CapsuleResponse getCapsuleDetail(Long capsuleId, Long userId) {
        //  캡슐 조회
        //  권한 확인
        //  상태 확인 (발송 전/후)
        //  응답 반환
        return null;
    }

    /**
     * 캡슐 수정 (발송 전만 가능)
     * @param capsuleId 캡슐 ID
     * @param request 수정 요청 정보
     * @param userId 사용자 ID
     * @return 수정된 캡슐 정보
     */
    @Transactional
    public CapsuleResponse updateCapsule(Long capsuleId, CapsuleCreateRequest request, Long userId) {
        //  캡슐 조회 및 권한 확인
        //  상태 확인 (RESERVED만 수정 가능)
        //  캡슐 정보 업데이트
        //  첨부파일 처리
        //  응답 반환
        return null;
    }

    /**
     * 캡슐 삭제 (발송 전만 가능)
     * @param capsuleId 캡슐 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteCapsule(Long capsuleId, Long userId) {
        //  캡슐 조회 및 권한 확인
        //  상태 확인
        //  첨부파일 삭제
        //  캡슐 삭제 또는 상태 변경
        //  스케줄링 취소
    }

    /**
     * 발송 대상 캡슐 조회 (스케줄러용)
     * @return 발송 대상 캡슐 목록
     */
    public List<Capsule> findCapsulesForDelivery() {
        // 현재 시간 기준 발송 대상 캡슐 조회
        return null;
    }

    /**
     * 캡슐 상태 변경
     * @param capsuleId 캡슐 ID
     * @param status 변경할 상태
     */
    @Transactional
    public void updateCapsuleStatus(Long capsuleId, CapsuleStatus status) {
        //  캡슐 조회
        //  상태 변경
        //  발송 시간 기록 (DELIVERED인 경우)
    }

    /**
     * 캡슐 발송 이력 조회
     * @param capsuleId 캡슐 ID
     * @param userId 사용자 ID
     * @return 발송 이력 목록
     */
    public List<DeliveryLogResponse> getDeliveryLogs(Long capsuleId, Long userId) {
        //  캡슐 조회 및 권한 확인
        //  발송 이력 조회
        //  응답 변환
        return null;
    }
}
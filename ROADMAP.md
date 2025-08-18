# 📋 TimeCapsule 프로젝트 개발 로드맵

## 🎯 현재 상태 분석

### ✅ 완료된 기능
- **인증/인가 시스템** (JWT, Security)
- **사용자 관리** (회원가입, 마이페이지)
- **타임캡슐 CRUD** (생성, 조회, 수정, 삭제)
- **데이터베이스 설계** (엔티티, 연관관계)
- **API 문서화** (Swagger)
- **Docker 인프라** (MySQL)
- **📦 DeliveryService 완전 구현** (발송 로직, 상태 관리, 배치 처리)
- **🚚 DeliveryController 구현** (발송 관리 API)
- **📊 DeliveryLogResponse DTO** (발송 이력 응답)
- **🗂️ 아키텍처 리팩토링** (CapsuleService ↔ DeliveryService 책임 분리)
- **⏰ 스케줄러 시스템 완전 구현** (CapsuleScheduler, CapsuleSchedulerService, 자동 발송)
- **📧 이메일 발송 시스템 완전 구현** (MailConfig, EmailService, 테마별 템플릿, 수동/자동 발송)

### ❌ 미구현 핵심 기능
- **WebSocket 실시간 알림**
- **파일 업로드/다운로드** (타임캡슐 첨부파일)
- **모니터링 시스템** (Redis, Prometheus, Grafana)

---

## 🚀 단계별 개발 계획

### 1단계: 핵심 기능 완성 (🔥 우선순위 높음)

#### ~~1.1 스케줄러 시스템 구현~~ ✅ 완료
- CapsuleScheduler `@Scheduled` 트리거 구현
- CapsuleSchedulerService 비즈니스 로직
- CapsuleSchedulerProperties 설정 관리
- DeliveryService 연동
- 재시도 로직 및 실패 처리
- 단위 테스트 작성

#### ~~1.2 DeliveryService 구현~~ ✅ 완료
- 캡슐 상태 변경 (SCHEDULED → DELIVERED/FAILED)
- 에러 처리 및 재시도 로직
- 배치 발송 처리
- 발송 이력 관리
- DeliveryController API 엔드포인트

#### 1.3 이메일 알림 시스템 ⭐ 다음 구현 대상
- JavaMailSender 구현
- SMTP 설정 (Gmail, AWS SES 등)
- 이메일 템플릿 작성
- 발송 성공/실패 처리
- DeliveryService와 연동

#### 1.4 WebSocket 실시간 알림 ⭐ 다음 구현 대상
- SimpMessagingTemplate 구현
- 클라이언트 연결 관리
- 실시간 알림 전송
- 구독/해제 처리

---

### 2단계: 사용자 경험 개선 (🟡 우선순위 중간)

#### 2.1 파일 업로드/다운로드 ⭐ 우선 구현 대상
- 타임캡슐 첨부파일 업로드 API
- 파일 저장소 구현 (로컬/AWS S3)
- 파일 검증 및 보안
- 이메일 첨부파일 기능 연동

#### ~~2.2 DeliveryController 구현~~ ✅ 완료
- 발송 대상 캡슐 조회 API (`GET /api/delivery/due`)
- 수동 발송 API (`POST /api/delivery/dispatch/{capsuleId}`)
- 배치 발송 API (`POST /api/delivery/batch`)
- 상태 변경 API (`PATCH /api/delivery/status/{capsuleId}`)
- 발송 이력 조회 API (`GET /api/delivery/logs/{capsuleId}`)

#### 2.3 설정 파일 정리
- `application.yml` 주석 해제
- 환경별 설정 분리

---

### 3단계: 운영 최적화 (🟢 우선순위 낮음)

#### 3.1 모니터링 시스템
- Prometheus 메트릭 구현
- Grafana 대시보드
- Actuator 엔드포인트

#### 3.2 테스트 코드 확장
- 단위 테스트
- 통합 테스트
- E2E 테스트

#### 3.3 성능 최적화
- 캐싱 전략 (Redis)
- DB 인덱스 최적화
- 비동기 처리

---

## 🎯 다음 단계 추천

### 즉시 구현해야 할 항목
1. **파일 업로드/다운로드** - 타임캡슐 첨부파일 ⭐ 최우선
2. **WebSocketService 구현** - 실시간 알림
3. **모니터링 시스템** - 운영 안정성

### 구현 순서
1. FileUploadService (타임캡슐 첨부파일)
2. WebSocketService (실시간 알림)
3. MonitoringService (메트릭, 대시보드)



### ⚡ 현재 진행 상황
- ✅ DeliveryService 완전 구현
- ✅ DeliveryController 완전 구현
- ✅ CapsuleScheduler 완전 구현
- ✅ EmailService 완전 구현
- 🔄 다음 단계: FileUploadService (타임캡슐 첨부파일)

---

## 📝 세부 구현 체크리스트

### 1단계
- [x] CapsuleScheduler.processExpiredCapsules() 구현
- [x] SchedulerService 의존성 주입 및 로직 구현
- [x] DeliveryService 핵심 발송 로직 구현
- [x] JavaMailSender 설정 및 이메일 발송
- [ ] WebSocket 구현

### 2단계
- [ ] 파일 업로드 API ⭐
- [ ] 파일 저장소 구현
- [x] DeliveryController API 구현
- [x] application.yml 설정 정리 (환경별 분리)

### 3단계
- [ ] Prometheus 메트릭
- [ ] Grafana 대시보드
- [ ] 테스트 코드 작성
- [ ] 성능 최적화

---

## 🔧 기술 스택

### 백엔드
- Spring Boot 3.5.3
- Spring Security (JWT)
- Spring Data JPA
- MySQL 8.0
- Redis

### 모니터링 & 운영
- Docker & Docker Compose
- Prometheus
- Grafana
- Swagger/OpenAPI

### 알림 시스템
- JavaMailSender (SMTP)
- WebSocket (STOMP)

---

## 📅 최근 완료 사항

### 2025-08-04
#### 🚀 DeliveryService & Controller 완전 구현
- DeliveryService / 구현체 완성
- 핵심 발송 로직
- DeliveryController 5개 API
- DeliveryLogResponse DTO
- 아키텍처 리팩토링
- DeliveryLogRepository 메서드 추가
- 컴파일 오류 해결

**진행률**
- 전체: ~85%
- 1단계: ~90% (이메일 시스템 완료)
- 2단계: ~60% (설정 분리 완료)

---

### 2025-08-06
#### ⏰ CapsuleScheduler 완전 구현
- CapsuleScheduler @Scheduled 트리거
- CapsuleSchedulerService 비즈니스 로직
- CapsuleSchedulerProperties 설정 관리
- 재시도 로직 및 백오프 전략
- 단위 테스트 3개 클래스
- DeliveryService 연동

---

---

### 2025-08-18
#### 📧 EmailService 완전 구현
- MailConfig: Gmail SMTP 설정 및 JavaMailSender 빈
- EmailService/EmailServiceImpl: 텍스트/HTML/타임캡슐 이메일 발송
- EmailController: 수동 이메일 전송 API
- Thymeleaf 템플릿: 기본, 크리스마스, 생일 테마
- 환경별 설정 분리: dev, prod, secret 프로파일
- DeliveryService 연동: 타임캡슐 자동 발송

**Last Updated:** 2025-08-18

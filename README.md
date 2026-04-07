# TimeCapsule Backend

미래의 나 또는 특정인에게 메시지를 예약 발송하는 타임캡슐 서비스의 백엔드입니다.
단순한 이메일 발송 구현을 넘어, **4가지 비동기 처리 전략의 트레이드오프를 직접 비교**하고
**재시도 및 부분 실패 처리**를 포함한 안정적인 발송 파이프라인 설계에 집중했습니다.

---

## 기술 스택

| 분류 | 기술 | 선택 이유 |
|------|------|-----------|
| Framework | Spring Boot 3.x / Java 17 | LTS, Virtual Thread 대비 |
| ORM | Spring Data JPA + MySQL 8 | Pessimistic Lock으로 스케줄러 중복 실행 방지 |
| 비동기 | Spring @Async, CompletableFuture | 전략별 성능 트레이드오프 비교 목적 |
| 메시지 큐 | Redis List | 경량 큐, 트랜잭션과 이메일 발송 완전 분리 |
| 인증 | JWT (JJWT) | Stateless 구조, 세션 서버 의존 제거 |
| 메일 | JavaMailSender + Thymeleaf | HTML 템플릿 기반 테마별 이메일 |
| 모니터링 | Micrometer + Prometheus + Grafana | 전략별 TPS 측정, AOP 기반 실행 시간 추적 |
| 문서화 | SpringDoc (Swagger) | API 명세 자동화 |

---

## 핵심 설계 의사결정

### 1. 이메일 발송 전략 4가지

단일 구현 대신 4가지 전략을 Facade + Strategy 패턴으로 구성하여 **성능과 안정성의 트레이드오프를 실측**했습니다.

| 전략 | 구현 방식 | 응답 방식 | 장점 | 단점 |
|------|----------|-----------|------|------|
| `SYNC` | `JavaMailSender` 직접 호출 | 블로킹 | 즉시 성공/실패 확인, 흐름 단순 | HTTP 응답 지연, 트랜잭션 내 외부 I/O |
| `ASYNC` | Spring `@Async` + ThreadPool | 논블로킹 | 응답 빠름, 구현 단순 | 예외 전파 불가, self-invocation 주의 |
| `CF` | `CompletableFuture.runAsync()` | 논블로킹 | 타임아웃·체이닝·조합 가능 | 예외 처리 복잡, 콜백 지옥 위험 |
| `REDIS_QUEUE` | Redis List + `@Scheduled` Worker | 비동기 큐 | 트랜잭션 완전 분리, 유실 추적 가능 | 폴링 지연(1초), DLQ 미구현 |

### JMeter 부하 테스트 결과 (emailCount=10, 5 loops)

> 응답 시간 기준: 서버가 요청을 처리하고 응답을 반환하는 시간 (실제 메일 전송 완료 시간과 다를 수 있음)

| 전략 | 10 스레드 (저부하) | 100 스레드 (중부하) | 200 스레드 (고부하) |
|------|-----------------|------------------|------------------|
| SYNC | 16ms | 16ms | 63ms |
| ASYNC | 2ms | 1ms | **131ms** ⚠️ |
| CF | 1ms | 1ms | 22ms |
| REDIS_QUEUE | 3ms | 3ms | **3ms** ✅ |

**인사이트:**
- 저/중부하에서는 ASYNC/CF가 가장 빠름 (큐 제출 후 즉시 반환)
- 고부하(200 스레드)에서 ASYNC는 스레드 풀 포화로 SYNC보다 느려짐 (131ms)
- REDIS_QUEUE는 부하와 무관하게 일정한 응답 시간 유지 — 고부하 환경에서 가장 안정적

**기본 전략을 `SYNC`로 선택한 이유:**
- 발송 주체가 스케줄러(사용자 HTTP 요청 아님) → 블로킹해도 UX 영향 없음
- 발송 성공/실패를 즉시 확인해야 재시도 로직이 동작 가능
- ASYNC/CF는 예외가 스케줄러 스레드로 전파되지 않아 실패 감지 불가
- yml 설정(`email.delivery.default-strategy`)으로 무중단 전환 가능

```yaml
# application.yml
email:
  delivery:
    default-strategy: SYNC  # SYNC | ASYNC | CF | REDIS_QUEUE
```

---

### 2. 재시도 전략 (Exponential Backoff)

수신자별로 실패 상태를 독립 관리하여 **부분 성공(PARTIALLY_DELIVERED)** 을 처리합니다.

```
캡슐 발송 흐름:

스케줄러 (@Scheduled, fixedDelay=60s)
  └─ 비관적 락으로 SCHEDULED 캡슐 조회 (중복 실행 방지)
       └─ 수신자별 이메일 발송 시도
            ├─ 성공 → RecipientDeliveryStatus.DELIVERED
            └─ 실패 → retryCount++
                  ├─ retryCount < maxRetries(3) → PENDING 유지, 다음 주기에 재시도
                  └─ retryCount >= maxRetries  → FAILED 확정

캡슐 최종 상태 결정:
  모두 성공    → DELIVERED
  모두 실패    → FAILED
  일부 성공    → PARTIALLY_DELIVERED
  재시도 대기  → SCHEDULED 유지, nextAttemptAt = now + (baseBackoff × (retryCount + 1))
```

**Backoff 공식:** `대기 시간 = baseBackoffSeconds(30s) × (retryCount + 1)`

| retryCount | 대기 시간 |
|-----------|---------|
| 0회 실패 후 | 30초 |
| 1회 실패 후 | 60초 |
| 2회 실패 후 | 90초 |
| 3회 실패 후 | FAILED 확정 |

---

### 3. 구조적 설계 패턴

**Facade 패턴** — `EmailServiceFacade`가 4가지 전략을 단일 인터페이스로 추상화

```java
// 호출부는 전략 구현을 모름
emailServiceFacade.sendByDefaultStrategy(emailRequest);
```

**Strategy 패턴** — `EmailMode` enum + switch 표현식으로 전략을 함수형 값으로 전달

```java
private Consumer<EmailPayload> pick(EmailMode mode) {
    return switch (mode) {
        case SYNC        -> syncEmailService::send;
        case ASYNC       -> asyncEmailService::send;
        case CF          -> completableFutureEmailService::send;
        case REDIS_QUEUE -> redisQueueEmailService::send;
    };
}
```

**AOP 기반 모니터링** — `LoggingAspect`로 서비스 계층 전체 실행 시간을 횡단 관심사로 분리

**Rich Domain Model** — `Capsule`, `CapsuleRecipient`에 상태 전이 로직 내장

```java
recipient.markFailedAttempt(e.getMessage(), schedulerConfig.getMaxRetries());
capsule.updateStatusBasedOnRecipients(schedulerConfig.getBaseBackoffSeconds());
```

---

## 시스템 구성

```
[Client]
   │ JWT 인증
   ▼
[SecurityFilterChain]
   JwtExceptionFilter → JwtAuthorizationFilter → JwtAuthenticationFilter
   │
   ▼
[Controller Layer]
   CapsuleController / EmailController / DeliveryController
   │
   ▼
[Service Layer]
   EmailServiceFacade ──────────────────────────────────────────────┐
   ├─ SyncEmailService                                              │
   ├─ AsyncEmailService (@Async + ThreadPoolTaskExecutor)           │
   ├─ CompletableFutureEmailService (orTimeout 30s)                 │
   └─ RedisQueueEmailService ──► Redis List ──► RedisEmailWorker    │
                                                                    │
   DeliveryServiceImpl ◄───────────────────────────────────────────┘
   └─ 수신자별 발송 + 재시도 상태 관리
   
   CapsuleScheduler (@Scheduled, fixedDelay=60s)
   └─ CapsuleSchedulerService
        └─ findAndLockDueForDispatch (비관적 락)
             └─ DeliveryService.dispatch()

[Infrastructure]
   MySQL (캡슐/수신자/발송 이력)
   Redis (이메일 큐: email:queue)
   Prometheus + Grafana (메트릭)
```

---

## 패키지 구조

```
src/main/java/com/example/timecapsule_backend/
├── config/
│   ├── email/          # 이메일 전략 설정 (AsyncEmailConfig, EmailDeliveryConfig)
│   ├── jwt/            # JWT 필터 체인
│   ├── monitoring/     # AOP 로깅, Micrometer 메트릭
│   ├── redis/          # RedisTemplate 설정
│   ├── scheduler/      # 스케줄러 설정값
│   └── security/       # SecurityFilterChain, CORS
├── controller/         # REST API (캡슐/이메일/발송/사용자)
├── service/
│   ├── email/          # 4가지 발송 전략 + Facade
│   ├── delivery/       # 발송 오케스트레이션 + 재시도
│   └── scheduler/      # 스케줄 트리거
├── domain/
│   ├── capsule/        # Capsule, CapsuleRecipient (Rich Domain Model)
│   └── deliveryLog/    # 발송 이력
├── ex/                 # 예외 계층 (BusinessException, ErrorCode)
└── handler/            # GlobalExceptionHandler
```

---

## 데이터베이스 스키마

| 테이블 | 설명 |
|--------|------|
| `users` | 사용자 정보 |
| `capsules` | 캡슐 메타 정보 (`@Version` Optimistic Lock 포함) |
| `capsule_contents` | 제목, 본문 |
| `capsule_themes` | 테마 타입 |
| `capsule_recipients` | 수신자별 발송 상태 (`retry_count`, `delivery_status`) |
| `delivery_logs` | 캡슐 단위 발송 이력 |

---

## 실행 방법

### 사전 요구사항
- Java 17+
- Docker & Docker Compose

### 로컬 실행

```bash
# 1. 저장소 클론
git clone <repository-url>
cd TimeCapsule_backend

# 2. 시크릿 설정 (DB, SMTP, JWT)
cp src/main/resources/application-secret.yml.example src/main/resources/application-secret.yml
# 파일 열어서 실제 값 입력

# 3. 인프라 실행
docker-compose up -d mysql redis prometheus grafana

# 4. 애플리케이션 실행
./gradlew bootRun
```

### 주요 URL

| 서비스 | URL |
|--------|-----|
| API 서버 | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Grafana | http://localhost:3000 (admin / admin123) |
| Prometheus | http://localhost:9090 |

---

## API 목록

### 인증
| Method | Path | 설명 |
|--------|------|------|
| POST | `/api/users` | 회원가입 |
| POST | `/api/auth/login` | 로그인 (JWT 발급) |
| GET | `/api/users/me` | 내 정보 조회 |
| PATCH | `/api/users/me` | 내 정보 수정 |

### 타임캡슐
| Method | Path | 설명 |
|--------|------|------|
| POST | `/api/capsules` | 캡슐 생성 |
| GET | `/api/capsules` | 내 캡슐 목록 |
| GET | `/api/capsules/{id}` | 캡슐 상세 |
| PUT | `/api/capsules/{id}` | 캡슐 수정 |
| DELETE | `/api/capsules/{id}` | 캡슐 삭제 |

### 발송 관리
| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/delivery/due` | 발송 대상 캡슐 조회 |
| POST | `/api/delivery/dispatch/{capsuleId}` | 수동 발송 |
| POST | `/api/delivery/batch` | 배치 발송 |
| PATCH | `/api/delivery/status/{capsuleId}` | 상태 변경 |
| GET | `/api/delivery/logs/{capsuleId}` | 발송 이력 조회 |

### 이메일
| Method | Path | 설명 |
|--------|------|------|
| POST | `/api/email/send/sync` | 동기 단건 발송 |
| POST | `/api/email/send/async` | 비동기 단건 발송 |
| POST | `/api/email/send/cf` | CompletableFuture 단건 발송 |
| POST | `/api/email/send/redis-queue` | Redis Queue 단건 발송 |
| POST | `/api/email/performance-test/sync` | 동기 전략 성능 테스트 |
| POST | `/api/email/performance-test/async` | 비동기 전략 성능 테스트 |
| POST | `/api/email/performance-test/cf` | CF 전략 성능 테스트 |
| POST | `/api/email/performance-test/redis-queue` | Redis Queue 전략 성능 테스트 |

### 모니터링
| Method | Path | 설명 |
|--------|------|------|
| GET | `/actuator/health` | 헬스체크 |
| GET | `/actuator/prometheus` | Prometheus 메트릭 |

---

## 알려진 한계 및 개선 계획

### 현재 한계

| 항목 | 내용 |
|------|------|
| 테스트 코드 부재 | 도메인 로직(재시도 상태 전이, backoff 계산) 단위 테스트 미작성 |
| Redis DLQ 미구현 | Worker에서 역직렬화/발송 실패 시 메시지 유실 가능 (`TODO` 주석으로 인지) |
| 성능 테스트 측정 편향 | ASYNC/CF/REDIS_QUEUE는 큐 제출 시간만 측정 → 실제 전송 완료 시간과 다름 |
| CORS 설정 | 개발 편의상 `allowedOriginPattern("*")` 사용 중 — 운영 시 도메인 화이트리스트 필요 |
| 성능 테스트 API 미인증 | `/api/email/performance-test/**`가 인증 없이 공개됨 |
| 분산 환경 스케줄러 | 현재 DB 비관적 락으로 중복 방지 — 다중 인스턴스 환경에서 ShedLock 도입 검토 |

### 개선 계획

- [ ] 핵심 도메인 로직 단위 테스트 작성
- [ ] Redis DLQ(`email:queue:dead`) 구현 및 재처리 스케줄러
- [ ] 성능 테스트에 콜백 기반 실제 전송 완료 시간 측정 추가
- [ ] 파일 첨부 기능 (AWS S3 연동)
- [ ] WebSocket 실시간 알림 (발송 완료 시 사용자 푸시)

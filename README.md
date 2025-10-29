# TimeCapsule Backend

타임캡슐 애플리케이션을 위한 Spring Boot 백엔드 프로젝트입니다.

## 🚀 기술 스택

- **Java 17** - 최신 LTS 버전
- **Spring Boot 3.x** - 웹 프레임워크
- **Spring Data JPA** - ORM 및 데이터 접근
- **MySQL 8.0** - 메인 데이터베이스
- **Redis** - 캐시 및 메시지 큐
- **WebSocket** - 실시간 알림
- **Spring Scheduler** - 백그라운드 작업 스케줄링
- **Micrometer** - 모니터링 메트릭
- **JWT** - 인증 및 인가
- **Docker** - 컨테이너화

## 📋 주요 기능

### 1. 사용자 관리 ✅
- 회원 가입 및 로그인
- JWT 기반 인증 (7일 만료)
- 사용자 정보 관리 (마이페이지)

### 2. 타임캡슐 관리 ✅
- 캡슐 생성, 조회, 수정, 삭제
- 발송 시간 예약 (scheduled_at)
- 테마 선택 (크리스마스, 생일케이크 등)
- 수신자 관리 (다중 수신자 지원)
- 캡슐 상태 관리 (PENDING, SCHEDULED, DELIVERED, FAILED)

### 3. 스케줄링 시스템 ✅
- 자동 캡슐 발송 (@Scheduled 기반)
- 실패 시 재시도 로직 (백오프 전략)
- 발송 이력 관리 (DeliveryLog)

### 4. 이메일 알림 시스템 ✅
- **4가지 전송 전략** 지원:
  - SYNC - 동기 방식 (안전하지만 느림)
  - ASYNC - Spring @Async (기본 전략, 균형잡힌 성능)
  - CompletableFuture - 고급 비동기 제어
  - Redis Queue - 메시지 큐 기반 (완전한 트랜잭션 분리)
- Thymeleaf 템플릿 엔진 (HTML 이메일)
- 테마별 이메일 템플릿
- Gmail SMTP 연동

### 5. 모니터링 🚧
- AOP 기반 로깅 (실행 시간 측정)
- Docker Compose로 Prometheus, Grafana 통합
- 헬스체크 엔드포인트 (Actuator)

## 🛠️ 개발 환경 설정

### 필요 사항
- Java 17+
- Docker & Docker Compose
- MySQL 8.0
- Redis 7+

### 로컬 개발 환경 구성

1. **저장소 클론**
```bash
git clone <repository-url>
cd TimeCapsule_backend
```

2. **환경변수 설정**
```bash
# .env.example 파일을 복사하여 .env 파일 생성
cp .env.example .env

# .env 파일을 열어서 실제 값으로 수정
# MYSQL_ROOT_PASSWORD, MAIL_PASSWORD 등
```

3. **Docker 환경 실행**
```bash
# 전체 스택 실행 (.env 파일이 자동으로 로드됨)
docker-compose up -d

# 애플리케이션만 로컬에서 실행 (개발 시)
docker-compose up -d mysql redis prometheus grafana
```

4. **애플리케이션 실행**
```bash
# Gradle을 사용하여 실행
./gradlew bootRun

# 또는 IDE에서 TimeCapsuleBackendApplication.main() 실행
```

### 환경 변수 설정

개발 환경에서는 `application.yml`에 설정된 기본값을 사용하거나, 환경 변수로 오버라이드할 수 있습니다:

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/timecapsule
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=password
export SPRING_DATA_REDIS_HOST=localhost
export JWT_SECRET=your-jwt-secret-key
```

## 🔧 API 엔드포인트

### 인증 관련
- `POST /api/users` - 회원 가입
- `POST /api/auth/login` - 로그인
- `GET /api/users/me` - 내 정보 조회
- `PATCH /api/users/me` - 내 정보 수정

### 캡슐 관련
- `POST /api/capsules` - 캡슐 생성
- `GET /api/capsules` - 내 캡슐 목록 조회 (페이징)
- `GET /api/capsules/{id}` - 캡슐 상세 조회
- `PUT /api/capsules/{id}` - 캡슐 수정
- `DELETE /api/capsules/{id}` - 캡슐 삭제

### 배송 관련
- `GET /api/delivery/due` - 발송 대상 캡슐 조회
- `POST /api/delivery/dispatch/{capsuleId}` - 수동 발송
- `POST /api/delivery/batch` - 배치 발송
- `GET /api/delivery/logs` - 발송 이력 조회

### 이메일 관련
- `POST /api/email/send/sync` - 동기 이메일 발송
- `POST /api/email/send/async` - 비동기 이메일 발송
- `POST /api/email/send/cf` - CompletableFuture 이메일 발송
- `POST /api/email/send/redis-queue` - Redis Queue 이메일 발송
- `POST /api/email/performance-test/{strategy}` - 성능 테스트 API

### 모니터링
- `GET /actuator/health` - 헬스체크
- `GET /actuator/prometheus` - Prometheus 메트릭

## 📊 모니터링 대시보드

### Grafana 대시보드
- URL: http://localhost:3000
- 계정: admin / admin123

### Prometheus
- URL: http://localhost:9090

### Swagger UI
- URL: http://localhost:8080/swagger-ui.html

## 🏗️ 아키텍처

### 디렉토리 구조
```
src/main/java/com/example/timecapsule_backend/
├── config/                 # 설정 클래스
│   ├── jwt/                # JWT 관련 설정
│   ├── security/           # 보안 설정
│   ├── websocket/          # WebSocket 설정
│   ├── redis/              # Redis 설정
│   └── monitoring/         # 모니터링 설정
├── controller/             # REST 컨트롤러
├── service/                # 비즈니스 로직
├── domain/                 # 도메인 엔티티 및 리포지토리
├── ex/                     # 예외 처리
├── util/                   # 유틸리티 클래스
└── handler/                # 글로벌 핸들러
```

### 데이터베이스 스키마
- `users` - 사용자 정보
- `capsules` - 타임캡슐 메인 정보
- `capsule_contents` - 타임캡슐 내용 (제목, 본문)
- `capsule_themes` - 타임캡슐 테마 설정
- `capsule_recipients` - 수신자 목록
- `attachments` - 첨부파일 (미구현)
- `delivery_logs` - 발송 이력

## 🔄 배포

### Docker를 사용한 배포
```bash
# 프로덕션 환경 배포
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# 스케일 아웃
docker-compose up -d --scale timecapsule-backend=3
```

### 환경별 설정
- `application.yml` - 기본 설정
- `application-dev.yml` - 개발 환경
- `application-prod.yml` - 프로덕션 환경

## 🧪 테스트

```bash
# 단위 테스트 실행
./gradlew test

# 통합 테스트 실행
./gradlew integrationTest

# 테스트 커버리지 확인
./gradlew jacocoTestReport
```

## 📝 개발 가이드

### 코드 스타일
- Java 17 기준 최신 문법 사용
- Lombok을 활용한 보일러플레이트 코드 최소화
- Spring Boot 3.x 의존성 주입 패턴 사용

### 브랜치 전략
- `main` - 프로덕션 브랜치
- `develop` - 개발 브랜치
- `feature/*` - 기능 개발 브랜치

### 커밋 메시지 규칙
- `feat: 새로운 기능 추가`
- `fix: 버그 수정`
- `docs: 문서 수정`
- `refactor: 코드 리팩토링`
- `test: 테스트 추가`

## 🤝 기여하기

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 라이센스

이 프로젝트는 MIT 라이센스를 따릅니다.

## 🆘 문제 해결

### 자주 발생하는 문제들

1. **MySQL 연결 오류**
   - Docker 컨테이너가 실행 중인지 확인
   - 포트 충돌 확인 (3306)

2. **Redis 연결 오류**
   - Redis 컨테이너 상태 확인
   - 포트 충돌 확인 (6379)

3. **JWT 토큰 오류**
   - JWT 시크릿 키 설정 확인
   - 토큰 만료 시간 확인

4. **스케줄러 실행 오류**
   - Redis 분산 락 설정 확인
   - 스케줄러 활성화 상태 확인

### 로그 확인
```bash
# 애플리케이션 로그
docker-compose logs -f timecapsule-backend

# 데이터베이스 로그
docker-compose logs -f mysql

# Redis 로그
docker-compose logs -f redis
```

## 📞 연락처

프로젝트 관련 문의: [이메일 주소]
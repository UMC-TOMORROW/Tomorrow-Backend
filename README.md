# 🛠️ Tomorrow Backend

DDD 기반으로 설계된 Tomorrow 서비스의 백엔드 레포지토리입니다.

## 프로젝트 구조
```
tomorrow
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.umc.tomorrow
│   │   │       ├── domain
│   │   │       │   └── member
│   │   │       │       ├── controller
│   │   │       │       ├── entity
│   │   │       │       ├── repository
│   │   │       │       └── service
│   │   │       │           ├── command
│   │   │       │           └── query
│   │   │       ├── global
│   │   │       │   └── common
│   │   │       │       ├── base
│   │   │       │       └── exception
│   │   │       │           └── code
│   │   │       └── TomorrowApplication.java
│   │   └── resources
│   │       ├── application.yml
│   │       ├── static
│   │       └── templates
│   └── test
│       └── java
│           └── com.umc.tomorrow
│               └── TomorrowApplicationTests.java
├── .gitignore
├── build.gradle
├── gradlew
├── gradlew.bat
└── README.md
```

## 코드 컨벤션

### 1. 패키지 구조

- **도메인형 패키지 구조**: 기능이 아닌 도메인(비즈니스 영역)별로 패키지를 구성합니다.
  - `domain`: 각 도메인별 비즈니스 로직이 위치합니다.
  - `global`: 프로젝트 전반에서 사용되는 공통 요소들이 위치합니다.

### 2. 도메인 패키지 내부 구조

각 도메인 패키지는 다음과 같은 내부 구조를 가집니다:
- `controller`: 해당 도메인의 API 엔드포인트를 관리합니다.
- `entity`: JPA 엔티티 클래스들이 위치합니다.
- `repository`: 데이터 접근 계층 인터페이스가 위치합니다.
- `service`: 비즈니스 로직이 구현되는 서비스 클래스들이 위치합니다.
  - `command`: 데이터 변경(CUD)을 담당하는 서비스를 구현합니다.
  - `query`: 데이터 조회(R)를 담당하는 서비스를 구현합니다.
  - 
### 3. 네이밍 컨벤션

- **클래스명**: 파스칼 케이스(PascalCase)를 사용합니다. 예: `MemberService`
- **메소드명, 변수명**: 카멜 케이스(camelCase)를 사용합니다. 예: `getUserById`
- **상수**: 대문자 스네이크 케이스(SNAKE_CASE)를 사용합니다. 예: `MAX_USER_COUNT`
- **패키지명**: 소문자만 사용합니다. 예: `com.umc.foody.domain.member`

## 4. 예외 처리

- 글로벌 예외 처리를 사용하여 일관된 응답 형식을 유지합니다.
- 커스텀 예외 클래스를 사용하여 비즈니스 로직에 관련된 예외를 명확히 구분합니다.

### 5. Lombok 사용

- 반복적인 코드를 줄이기 위해 Lombok을 적극적으로 활용합니다.
- 주로 사용하는 어노테이션: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`



## 🌱 Git 브랜치 전략

- **main**: 배포 가능한 상태만 유지
- **develop**: 기능 개발 브랜치가 merge되는 통합 브랜치
- **feature/**: 기능 단위 작업
- **fix/**: 버그 수정
- **hotfix/**: 운영 중 긴급 수정
- **refactor/**: 리팩토링용 브랜치

---

## 🧾 브랜치 네이밍 규칙

- 접두어/이슈번호-작업내용 형태로 작성합니다.
- 하위 작업은 소문자 알파벳으로 나누어 관리할 수 있습니다.

### 예시


feature/#24-image-create
feature/#31-user-login-a
fix/#78-token-expire
refactor/#10-entity-rename

---

## 🧩 PR(Pull Request) 규칙

- **기능 단위 또는 세부 작업 단위로 PR 작성**
- 하나의 이슈에 대해 여러 개의 세부 작업 PR을 쪼개서 관리
- PR 본문: 작업 목적, 주요 변경점, 테스트 여부 등 명확히 기술



---

## ✅ 커밋 컨벤션 (선택 시)

```md
feat: 기능 추가
fix: 버그 수정
docs: 문서 수정
refactor: 리팩토링
test: 테스트 코드 작성
chore: 빌드 설정, 패키지 추가 등

## 주요 디렉토리 설명

### domain

비즈니스 로직을 담당하는 도메인 계층입니다. 각 도메인은 독립적인 기능 단위로 구성됩니다.

#### member

사용자 관련 기능을 구현하는 도메인입니다.
- `controller`: 사용자 API 엔드포인트
- `entity`: 사용자 관련 엔티티 클래스
- `repository`: 사용자 데이터 접근 인터페이스
- `service/command`: 사용자 정보 생성/수정/삭제 서비스
- `service/query`: 사용자 정보 조회 서비스

### global

프로젝트 전반에서 사용되는 공통 모듈입니다.

#### common

- `base`: 공통으로 사용되는 기본 클래스들
  - `BaseEntity`: 생성일자, 수정일자, 삭제일자를 관리하는 JPA 엔티티 기본 클래스
  - `BaseResponse`: API 응답의 일관성을 위한 응답 래퍼 클래스
- `exception`: 예외 처리 관련 클래스들
  - `code`: 에러 코드와 메시지를 정의하는 인터페이스와 구현체
  - `ExceptionAdvice`: 전역 예외 처리기
  - `RestApiException`: 사용자 정의 예외 클래스

## 기술 스택

- Java 17
- Spring Boot 3.5.3
- Spring Data JPA
- Spring Security
- MySQL
- Gradle
- Lombok




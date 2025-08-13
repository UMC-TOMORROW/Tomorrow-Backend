# 지원 결과 메일 발송 기능 구현

## 개요
사용자가 지원한 직무에 대한 합격/불합격 결과를 자동으로 메일로 발송하는 기능을 구현했습니다.

## 구현된 기능

### 1. 메일 타입 추가
- `JOB_ACCEPTED`: 합격 안내 메일
- `JOB_REJECTED`: 불합격 안내 메일

### 2. 메일 제목
- **제목**: `[내일] 전형 결과 안내드립니다.`

### 3. 메일 내용
- **합격 메일**: "축하합니다! 이번 전형에서 합격하셨습니다."
- **불합격 메일**: "아쉽지만 이번 전형에서는 함께하지 못하게 되었습니다."
- **지원 정보**: 지원공고명, 지원회사명 포함

### 4. HTML 템플릿
- 녹색 테마의 모던한 디자인
- 반응형 레이아웃
- "내일" 로고 포함

## 파일 구조

```
src/main/java/com/umc/tomorrow/domain/email/
├── enums/
│   ├── EmailContentType.java          # 메일 내용 생성 로직
│   ├── EmailType.java                 # 메일 타입 정의
│   └── EmailContentProvider.java      # 메일 내용 제공 인터페이스
├── service/
│   └── EmailService.java              # 메일 발송 서비스
└── dto/
    └── request/
        └── EmailRequestDTO.java       # 메일 요청 DTO

src/main/java/com/umc/tomorrow/domain/application/
└── service/command/
    └── ApplicationCommandService.java  # 지원 상태 변경 시 메일 발송

src/main/resources/static/email-templates/
└── application-result.html            # HTML 템플릿 파일
```

## 사용 방법

### 1. 지원 상태 변경 시 자동 메일 발송
```java
// ApplicationCommandService.updateApplicationStatus() 메서드에서
// 지원 상태가 ACCEPTED 또는 REJECTED로 변경되면 자동으로 메일 발송
EmailType emailType = status == ApplicationStatus.ACCEPTED ? 
    EmailType.JOB_ACCEPTED : EmailType.JOB_REJECTED;

EmailRequestDTO emailRequestDTO = EmailRequestDTO.builder()
    .jobId(job.getId())
    .type(emailType)
    .build();

emailService.sendEmail(application.getUser().getId(), emailRequestDTO);
```

### 2. 수동으로 메일 발송
```java
EmailRequestDTO emailRequestDTO = EmailRequestDTO.builder()
    .type(EmailType.JOB_ACCEPTED)  // 또는 JOB_REJECTED
    .jobId(jobId)
    .build();

emailService.sendEmail(userId, emailRequestDTO);
```

## 메일 템플릿 특징

### 디자인
- **색상**: 녹색 테마 (#4CAF50)
- **폰트**: Arial, sans-serif
- **레이아웃**: 반응형, 최대 너비 600px
- **로고**: 흰색 배경의 "내일" 텍스트

### 내용 구성
1. **헤더**: "전형 결과 안내" 제목과 로고
2. **인사말**: 사용자 이름 포함
3. **결과 메시지**: 합격/불합격 안내
4. **마무리**: 격려 또는 축하 메시지
5. **지원 정보**: 공고명과 회사명

## 테스트

### 단위 테스트
- `EmailServiceTest`: 메일 발송 기능 테스트
- `JobBookmarkCommandServiceImplTest`: 북마크 기능 테스트
- `JobBookmarkQueryServiceImplTest`: 북마크 조회 기능 테스트

### 테스트 실행
```bash
./gradlew test
```

## 주의사항

1. **HTML 메일**: `helper.setText(content, true)`로 HTML 메일 설정
2. **인코딩**: UTF-8 인코딩 사용
3. **예외 처리**: 메일 발송 실패 시 적절한 예외 처리
4. **트랜잭션**: 지원 상태 변경과 메일 발송이 하나의 트랜잭션으로 처리

## 향후 개선 사항

1. **템플릿 엔진**: Thymeleaf 등을 사용한 동적 템플릿 처리
2. **메일 큐**: 비동기 메일 발송을 위한 큐 시스템
3. **템플릿 관리**: 관리자 페이지에서 메일 템플릿 수정 기능
4. **다국어 지원**: 한국어 외 다른 언어 지원
5. **메일 발송 이력**: 발송된 메일의 이력 관리

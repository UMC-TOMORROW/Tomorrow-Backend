# 🛠️ Tomorrow Backend

DDD 기반으로 설계된 Tomorrow 서비스의 백엔드 레포지토리입니다.

---


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
- PR 제목: `[#이슈번호] 작업내용`
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



# 🌱 내일 - 당신의 더 나은 '내일'을 위해 '내 일' 찾기

> 🧑‍🦳 건강 상태에 딱 맞는 맞춤형 일자리 추천 서비스
> **내일**은 중장년층의 건강 상태와 생활 패턴을 고려한 건강 맞춤 온보딩 기능을 통해무리가 가지 않는 직무를 추천하고, 정서적·신체적 부담을 줄여 지속적인 사회 참여와 경제 활동을 돕는 서비스입니다.
이를 통해 삶의 만족도와 자존감을 높이고, 건강한 노후를 준비할 수 있습니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/69f8ff8b-bf45-4e6c-8679-1095b2bad4a6" width="500" alt="스크린샷"/>
</p>


### ✨ 주요 기능
---

#### 💪 건강 맞춤 온보딩

* 5가지의 온보딩을 통해 개인의 건강 상태와 생활 패턴을 반영하여 무리가 없는 직무를 추천하는 맞춤형 초기 진단 기능
* 중장년층도 쉽게 사용할 수 있는 직관적 UI/UX로 접근성과 편의성을 강화

#### 📋 맞춤형 일자리 추천 & 후기 공유
* 온보딩 결과와 건강 조건을 기반으로 한 최적의 일자리 매칭 시스템
* 실제 근무자의 생생한 후기 공유를 통해 직무 이해도와 선택의 정확도를 높임

#### 💬 커리어 정보 제공 & 실시간 소통
* 다양한 업종·직무별 커리어 정보와 취업 준비 자료를 한 곳에서 제공
* 실제 커리어 경험자와의 실시간 채팅을 통해 현장감 있는 조언과 정보를 실시간으로 획득

##  👥 팀원 소개(백엔드)

<table>
  <tr>
    <td align="center" width="33%">
       <img width="300" height="400" alt="Image" src="https://github.com/user-attachments/assets/245fc11a-a37b-4962-9b91-613b2d93d6cd"/><br/>
      <strong>이승주</strong><br/>
      Team Lead<br/>
      <a href="https://github.com/Leeseung-joo">📧 GitHub</a><br/>
      <sub>커리어톡 및 채팅<br/>내일 추천 기능<br/>nginx 및 도메인 설정, S3</sub>
    </td>
    <td align="center" width="33%">
      <img width="300" height="400" alt="Image" src="https://github.com/user-attachments/assets/a5eed6d6-3891-4ccf-99de-b0a98525406f"/><br/>
      <strong>정여진</strong><br/>
      Backend Developer<br/>
      <a href="https://github.com/jnyn0314">📧 GitHub</a><br/>
      <sub>마이페이지<br/>소셜로그인 및 메일 발송<br/>인프라 구축,erd 설계</sub>
    </td>
    <td align="center" width="33%">
      <img width="300" height="400" alt="Image" src="" /><br/>
      <strong>한지혜</strong><br/>
      Backend Developer<br/>
      <a href="https://github.com/hanfihei">📧 GitHub</a><br/>
      <sub>일자리 등록 및 카카오 지도<br/>소셜로그인<br/>CI/CD,프로젝트 설계</sub>
    </td>
  </tr>
</table>
















## ⚒ 기술 스택

### Backend  
![Java](https://img.shields.io/badge/Java-007396?style=flat&logo=openjdk&logoColor=white) 
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white) 
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat&logo=spring-security&logoColor=white) 
![JPA](https://img.shields.io/badge/JPA-59666C?style=flat&logo=hibernate&logoColor=white)

### DB  
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white) 
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white)

### Infra  
![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat&logo=amazon-aws&logoColor=white) 
![Amazon S3](https://img.shields.io/badge/Amazon%20S3-569A31?style=flat&logo=amazon-s3&logoColor=white) 
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white) 
![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat&logo=nginx&logoColor=white) 
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=flat&logo=github-actions&logoColor=white)

### API  
![Kakao API](https://img.shields.io/badge/Kakao%20API-FFCD00?style=flat&logo=kakao&logoColor=black) 
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=black) 
![JavaMail](https://img.shields.io/badge/JavaMail-007396?style=flat&logo=java&logoColor=white)

### ETC  
![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white) 
![Notion](https://img.shields.io/badge/Notion-000000?style=flat&logo=notion&logoColor=white) 
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ-000000?style=flat&logo=intellijidea&logoColor=white) 
![Slack](https://img.shields.io/badge/Slack-4A154B?style=flat&logo=slack&logoColor=white) 
![Discord](https://img.shields.io/badge/Discord-5865F2?style=flat&logo=discord&logoColor=white) 
![ERDCloud](https://img.shields.io/badge/ERDCloud-00C389?style=flat&logo=data&logoColor=white)


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





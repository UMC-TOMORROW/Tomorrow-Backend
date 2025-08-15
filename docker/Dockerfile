# Base 이미지: Java 17 (Debian slim)
FROM openjdk:17-jdk-slim

# 시스템 폰트 설치 (한글 포함)
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      fontconfig fonts-dejavu-core fonts-noto-cjk \
 && fc-cache -f \
 && rm -rf /var/lib/apt/lists/*

# JVM 옵션: 헤드리스 + 타임존(서울) + UTF-8 인코딩
ENV JAVA_TOOL_OPTIONS="-Djava.awt.headless=true -Duser.timezone=Asia/Seoul -Dfile.encoding=UTF-8"

# 앱 작업 디렉토리
WORKDIR /app

# GitHub Actions에서 복사한 app.jar을 컨테이너에 복사
COPY build/libs/*.jar app.jar

# 컨테이너가 사용하는 포트
EXPOSE 8080

# 컨테이너 실행 시 실행할 명령
ENTRYPOINT ["java", "-jar", "app.jar"]

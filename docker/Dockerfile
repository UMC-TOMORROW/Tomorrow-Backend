# Base 이미지: Java 17
FROM openjdk:17-jdk-slim

# GitHub Actions에서 복사한 app.jar을 컨테이너에 복사
COPY build/libs/*.jar app.jar

# 컨테이너가 사용하는 포트
EXPOSE 8080

# 컨테이너 실행 시 실행할 명령
ENTRYPOINT ["java", "-jar", "app.jar"]

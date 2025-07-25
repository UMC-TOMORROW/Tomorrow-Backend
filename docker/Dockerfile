# Dockerfile
FROM openjdk:17-jdk-slim

# JAR file name (빌드 결과물)
ARG JAR_FILE=build/libs/*.jar

# JAR 파일을 app.jar로 복사
COPY ${JAR_FILE} app.jar

# 실행 포트 명시
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]

# 1단계: 빌드 스테이지
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# pom.xml과 소스 복사
COPY pom.xml .
COPY src ./src

# Maven 패키징 (테스트는 생략 가능)
RUN mvn clean package -DskipTests

# 2단계: 실행 스테이지
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# 빌드된 jar 복사
COPY --from=builder /app/target/*.jar app.jar

# 포트 설정 (Spring Boot 기본 8080)
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]

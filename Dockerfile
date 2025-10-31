# 1단계: 빌드 스테이지
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# pom.xml 먼저 복사 → 의존성 캐싱
COPY pom.xml ./

# generated-sources 디렉토리 생성
RUN mkdir -p target/generated-sources/java

# 소스 복사
COPY src ./src

# Maven 빌드 (QueryDSL Q 클래스 생성 포함)
RUN mvn clean package -DskipTests

# 2단계: 런타임 스테이지
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# 빌드된 jar 복사
COPY --from=builder /app/target/*.jar app.jar

# 포트 설정
EXPOSE 8080

# JVM 옵션 (시간대)
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Seoul"

# 실행
ENTRYPOINT ["java","-jar","app.jar"]

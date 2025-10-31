# 1단계: 빌드 스테이지
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# pom.xml 먼저 복사 (의존성 캐싱 최적화)
COPY pom.xml ./

# 의존성 다운로드 (레이어 캐싱)
RUN mvn dependency:go-offline -B

# 소스 코드 복사
COPY src ./src

# Maven 빌드 (QueryDSL Q 클래스 생성 포함)
# compile 단계에서 Q 클래스 생성 → package로 JAR 생성
RUN mvn clean compile && \
    echo "✅ Compile completed (Q classes generated)" && \
    find target/generated-sources/annotations -name "Q*.java" | head -5 || echo "⚠️ No Q classes" && \
    mvn package -DskipTests && \
    echo "✅ Package completed" && \
    ls -lh target/*.jar

# 2단계: 런타임 스테이지
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/target/*.jar app.jar

# 포트 설정
EXPOSE 8080

# JVM 옵션 설정
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Seoul"

# 헬스체크 (선택사항)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
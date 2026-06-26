# --- Stage 1: Build ---
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B -q

COPY src ./src
RUN mvn clean package -DskipTests -B -q

# --- Stage 2: Runtime ---
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

RUN groupadd --system appgroup && \
    useradd --system --gid appgroup --no-create-home appuser

COPY --from=builder /app/target/dataAnalist-0.0.1-SNAPSHOT.jar app.jar

RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]

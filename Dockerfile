# Stage 1: Build the application with Gradle
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Install bash and dos2unix for handling wrapper script
RUN apk add --no-cache bash dos2unix

# Copy gradle wrapper and configuration files
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Convert line endings and make gradlew executable
RUN dos2unix ./gradlew && chmod +x ./gradlew

# Copy source code and build project (skip tests to speed up container build)
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Lightweight runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create upload directory for medical records fallback
RUN mkdir -p /app/uploads/medical-records

# Copy built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
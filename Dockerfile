# Multi-stage build: Build stage with Maven + Java
FROM maven:3.8.6-openjdk-17-slim AS build

WORKDIR /app

# Copy Maven configuration
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Production stage: Lightweight Java runtime
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/spring-boot-ecommerce-1.0.0.jar app.jar

# Expose the port that Render will use
EXPOSE 10000

# Set environment variables
ENV PORT=10000
ENV SPRING_PROFILES_ACTIVE=production

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT}/health || exit 1

# Run the application
CMD ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]

# =========================================
# Stage 1: Build the Spring Boot Application
# =========================================
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy only pom.xml first for dependency caching
COPY pom.xml .

# Pre-fetch dependencies (improves rebuild speed)
RUN mvn dependency:go-offline -B

# Copy the full source
COPY src ./src

# Build the JAR (skip tests for faster CI builds)
RUN mvn clean package -DskipTests

# =========================================
# Stage 2: Runtime Image
# =========================================
FROM eclipse-temurin:17-jre-jammy

# Install yt-dlp (for YouTube extraction)
RUN apt-get update && \
    apt-get install -y yt-dlp && \
    rm -rf /var/lib/apt/lists/*

# Create app directory
WORKDIR /app

# Copy the packaged JAR from build stage
COPY --from=build /app/target/youtube-services*.jar youtube-services.jar

# Expose application port
EXPOSE 8080

# Healthcheck (optional but recommended)
HEALTHCHECK CMD curl --fail http://localhost:8080/search/cronJob || exit 1

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "youtube-services.jar"]

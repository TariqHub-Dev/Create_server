# Stage 1: Build the application using Gradle
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy gradle wrapper and configuration files first for better caching
COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle.properties* ./

# Copy the source code for all modules
COPY client client
COPY core core
COPY server server

# Build the fat JAR
RUN chmod +x gradlew && ./gradlew :server:buildFatJar --no-daemon

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the build artifact from the build stage
COPY --from=build /app/server/build/libs/*-all.jar app.jar

# Support dynamic port assignment for cloud environments
EXPOSE ${PORT:-8080}

CMD ["java", "-jar", "app.jar"]

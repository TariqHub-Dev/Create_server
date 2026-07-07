# Stage 1: Build stage using a Gradle image to avoid downloading Gradle during build
FROM gradle:jdk21 AS build
WORKDIR /app

# Copy configuration files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

# Copy source code
COPY client client
COPY core core
COPY server server

# Build the Fat JAR
# We use 'gradle' directly instead of './gradlew' to use the pre-installed version
RUN gradle :server:buildFatJar --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built JAR
COPY --from=build /app/server/build/libs/*-all.jar app.jar

ENV PORT=8080
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

# Stage 1: Build stage
FROM gradle:jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :server:buildFatJar --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/server/build/libs/*-all.jar app.jar

ENV PORT=8080
EXPOSE 8080

# Memaksa Java menggunakan IPv4 untuk menghindari "Network is unreachable" di lingkungan cloud
CMD ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "app.jar"]

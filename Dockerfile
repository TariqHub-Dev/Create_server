FROM eclipse-temurin:21-jdk-slim AS build
WORKDIR /app

COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle.properties* ./

COPY client client
COPY core core
COPY server server

RUN chmod +x gradlew && ./gradlew :server:buildFatJar --no-daemon

FROM eclipse-temurin:21-jre-slim
WORKDIR /app
COPY --from=build /app/server/build/libs/*-all.jar app.jar
EXPOSE ${PORT:-8080}
CMD ["java", "-jar", "app.jar"]
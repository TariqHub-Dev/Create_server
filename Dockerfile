FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY core core
COPY server server

RUN chmod +x gradlew && ./gradlew :server:buildFatJar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/server/build/libs/*-all.jar app.jar

CMD ["java", "-jar", "app.jar"]

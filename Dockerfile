FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew :server:buildFatJar --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/server/build/libs/*-all.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

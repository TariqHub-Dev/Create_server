Dockerfile

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY server/build/libs/*-all.jar app.jar

EXPOSE 8080

CMD ["java","-jat","app.jar"]
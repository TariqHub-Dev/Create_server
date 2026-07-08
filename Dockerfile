# Stage 1: Build the application
FROM gradle:8-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :server:buildFatJar --no-daemon

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/server/build/libs/*-all.jar app.jar

ENV PORT=8080
EXPOSE 8080

# Flag -Djava.net.preferIPv4Stack=true sangat penting di lingkungan cloud
# untuk mencegah error "UnknownHostException" atau "Network is unreachable"
CMD ["java", "-Djava.net.preferIPv4Stack=true", "-Djava.net.preferIPv6Addresses=false", "-Dnetworkaddress.cache.ttl=60", "-jar", "app.jar"]

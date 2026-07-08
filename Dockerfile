# Stage 1: Build stage
FROM gradle:8-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :server:buildFatJar --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/server/build/libs/*-all.jar app.jar

ENV PORT=8080
EXPOSE 8080

# Hapus preferIPv4Stack agar Java bisa menghubungi host IPv6 Supabase
CMD ["java", "-Dnetworkaddress.cache.ttl=60", "-jar", "app.jar"]

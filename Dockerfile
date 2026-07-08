# Stage 1: Build stage
FROM gradle:jdk21 AS build
WORKDIR /app
COPY . .
# Menggunakan gradle bawaan image untuk menghindari timeout download gradle wrapper
RUN gradle :server:buildFatJar --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/server/build/libs/*-all.jar app.jar

ENV PORT=8080
EXPOSE 8080

# Flag penting untuk koneksi Cloud:
# 1. preferIPv4Stack: Menghindari error "Network is unreachable" pada IPv6
# 2. preferIPv6Addresses: Memastikan prioritas IPv4
# 3. networkaddress.cache.ttl: Agar DNS tidak dicache terlalu lama (antisipasi pergantian IP Supabase)
CMD ["java", \
     "-Djava.net.preferIPv4Stack=true", \
     "-Djava.net.preferIPv6Addresses=false", \
     "-Dnetworkaddress.cache.ttl=60", \
     "-jar", "app.jar"]

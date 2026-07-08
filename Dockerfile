# Stage 1: Build the application
# Menggunakan image gradle resmi agar tidak perlu download gradle wrapper (menghindari timeout)
FROM gradle:8-jdk21 AS build
WORKDIR /app

# Copy seluruh file project
COPY . .

# Jalankan build fat JAR
RUN gradle :server:buildFatJar --no-daemon

# Stage 2: Run the application
# Image eclipse-temurin:21-jre tersedia secara resmi (tanpa tag -slim)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy JAR hasil build dari stage 1
COPY --from=build /app/server/build/libs/*-all.jar app.jar

# Railway menggunakan environment variable PORT
ENV PORT=8080
EXPOSE 8080

# Flag -Djava.net.preferIPv4Stack=true sangat penting di lingkungan cloud
# untuk mencegah error "UnknownHostException" atau "Network is unreachable"
CMD ["java", "-Djava.net.preferIPv4Stack=true", "-Djava.net.preferIPv6Addresses=false", "-Dnetworkaddress.cache.ttl=60", "-jar", "app.jar"]

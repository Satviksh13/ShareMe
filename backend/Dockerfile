# Stage 1: Build the app
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests

# Stage 2: Run the app
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Render provides PORT, default to 8080 locally
ENV PORT=8080

EXPOSE 8080

CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
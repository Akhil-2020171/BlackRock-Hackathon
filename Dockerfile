# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/selfinvestment-0.0.1.jar app.jar

# Expose port 5477
EXPOSE 5477

# Set environment variable for Spring Boot port
ENV SERVER_PORT=5477

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=5477"]

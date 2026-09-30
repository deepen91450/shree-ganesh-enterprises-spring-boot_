# Use Java 21 (change version to match your project)
FROM eclipse-temurin:21-jre-alpine

# Set working directory inside container
WORKDIR /app

# Copy the JAR from target folder
COPY target/*.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 2330

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
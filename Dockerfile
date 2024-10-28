# Use the official OpenJDK image as a base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the jar file from the target directory
COPY target/trade-0.0.1-SNAPSHOT.jar myapp.jar

# Expose the application port
EXPOSE 8080

# Command to run the jar
ENTRYPOINT ["java", "-jar", "myapp.jar"]

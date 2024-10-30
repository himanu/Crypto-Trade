FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Use the official OpenJDK image as a base image
FROM openjdk:17-jdk-slim


# Copy the jar file from the target directory
COPY --from=build /target/trade-0.0.1-SNAPSHOT.jar myapp.jar

# Expose the application port
EXPOSE 8080

# Command to run the jar
ENTRYPOINT ["java", "-jar", "myapp.jar"]

FROM openjdk:21

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged JAR file into the container at the defined working directory
COPY target/database-service*.jar database-service.jar

# Expose the port on which your Spring Boot application runs
EXPOSE 8080

# Command to run your Spring Boot application
CMD ["java", "-jar", "database-service.jar"]
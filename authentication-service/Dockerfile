FROM openjdk:21

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged JAR file into the container at the defined working directory
COPY target/authentication-service-*.jar authentication-service.jar

# Expose the port on which your Spring Boot application runs
EXPOSE 8080

# Command to run your Spring Boot application
#ENTRYPOINT ["./wait-for-it.sh", "mysqldb:3306", "--", "java", "-jar", "authentication-service.jar"]
CMD ["java", "-jar", "authentication-service.jar"]
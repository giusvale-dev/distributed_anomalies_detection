# Read Me First
Here are insert the instruction for developers.
To use this project you need to run the following commands:

**ONLY DEVELOPMENT ENVIRONMENT**
```bash
mvn clean install compile test -Dspring.config.location=file:./src/main/resources/application-dev.properties
```

For production environment use the docker-compose file

If you need to add this configuration in your code, you can use this configuration (visual studio code)

```javascript
        {
            "type": "java",
            "name": "DatabaseServiceApplication",
            "request": "launch",
            "mainClass": "it.uniroma1.databaseservice.DatabaseServiceApplication",
            "projectName": "database-service",
            "args": [
                "--spring.config.location=file:./database-service/src/main/resources/application-dev.properties"
            ]
        }
```



# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.3/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.3/maven-plugin/reference/html/#build-image)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.2.3/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Spring for RabbitMQ](https://docs.spring.io/spring-boot/docs/3.2.3/reference/htmlsingle/index.html#messaging.amqp)

### Guides
The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Messaging with RabbitMQ](https://spring.io/guides/gs/messaging-rabbitmq/)


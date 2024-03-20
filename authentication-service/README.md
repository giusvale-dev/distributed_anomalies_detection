## How to compile

```bash
cd distributed_anomalies_detection/authentication-service
mvn clean install compile
```

## IDE Configuration (Visual studio code)

In the development environment is reccomended use the application-dev properties file. To do that in your **launch.json** file you can add this configuration:
```json
        {
            "type": "java",
            "name": "AuthenticationServerApplication",
            "request": "launch",
            "mainClass": "it.uniroma1.authenticationserver.AuthenticationServerApplication",
            "projectName": "authentication-service",
            "args": [
                "--spring.config.location=file:./authentication-service/src/main/resources/application-dev.properties"
            ]
        }
```

compile with IDE and debug!

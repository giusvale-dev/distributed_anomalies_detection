# Userservice

## Prerequisites

- Docker must be installed on your machine. You can download and install Docker from [the official Docker website](https://www.docker.com/get-started).

## Running the RabbitMQ Container

To run the RabbitMQ container, follow these steps:

1. Pull the RabbitMQ Docker image from Docker Hub by running the following command:

   ```bash
   docker pull rabbitmq:3-management
   ```
2. Once the image is downloaded, you can start the RabbitMQ container by running the following command:
```bash
docker run --rm -d \
  --name rabbitmq \
  -p 15672:15672 \
  -p 5672:5672 \
  rabbitmq:3-management
  ```
3. To access the RabbitMQ management UI, open your web browser and navigate to http://localhost:15672. You can log in with the default credentials (username: guest, password: guest).

4. To stop the RabbitMQ container, you can use the following command:
```bash
docker stop rabbitmq
```
## How to use in development

Going under application.properties file, uncomment the rows under #Configuration in Docker for test then 

```bash
docker run --rm -d \
    --name mysqldb \
    -e MYSQL_ROOT_PASSWORD=user_admin01$ \
    -e MYSQL_DATABASE=users \
    -e MYSQL_USER=user_admin \
    -e MYSQL_PASSWORD=user_admin01$ \
    -v $(pwd)/mysql-init:/docker-entrypoint-initdb.d \
    -p 3306:3306 \
    mysql:latest
```

compile and run!
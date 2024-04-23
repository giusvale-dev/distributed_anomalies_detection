# Distributed Anomalies Detection

# Dependencies
Please install in your system the following required dependecies:
```bash
# Insert here the commands
```


# Docker images container installation
Installation of production environment:
```bash
./install-services.sh
docker-compose up -d
```

# Install the watchdog
The Watchdog is not a docker container but is an agent that interact with the docker containers.
In the target run these commands:

```bash
cd watchdog
./deploy.sh
```


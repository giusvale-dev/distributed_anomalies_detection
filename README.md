# Distributed Anomalies Detection

# Dependencies
Please install in your system the following required dependecies:
```bash
1. run sudo apt install git 
2. clone repository with: git clone https://github.com/giusvale-dev/distributed_anomalies_detection.git
3. install maven with: sudo apt install maven 
4. verify Java version, it must be 21
5. install node and nvm with: install nvm, with wget -qO- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | 	 bash
6. install node v16.14.0 with: nvm install v16.14.0
7. install angular16 and cli with: npm install -g @angular/cli@16
8. run cd distributed_anomalies_detection
9. run cd user-manager-ui and run command npm install
10.run cd.. and after that ./install_services.py
11.install docker 
12.install docker-compose 
13.run sudo docker-compose build
14.run sudo docker.compose up -d
15.run cd watchdog and after that ./deploy.sh to run watchdog agent on your machine
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


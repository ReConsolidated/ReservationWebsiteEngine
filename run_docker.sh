#!/bin/bash
set -e
git reset --hard
git pull
mvn package
cp target/ZPIBackend-0.0.1-SNAPSHOT.jar src/main/docker
cd src/main/docker || exit
sudo docker-compose stop
sudo docker-compose build
sudo docker-compose up

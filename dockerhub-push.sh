#!/bin/sh
cd docker-distribution
sudo docker build --build-arg PLUGIN_VERSION="$TRAVIS_TAG" -t "alenkacz/marathon-deployer:$TRAVIS_TAG" .
sudo docker login -u "$DOCKER_USERNAME" -p "$DOCKER_PASSWORD"
sudo docker push "alenkacz/marathon-deployer:$TRAVIS_TAG"

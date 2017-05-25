#!/bin/sh
cd docker-distribution
docker build --build-arg PLUGIN_VERSION="$TRAVIS_TAG" -t "alenkacz/marathon-deployer:$TRAVIS_TAG"
docker login -u "$DOCKER_USERNAME" -p "$DOCKER_PASSWORD"
docker push "alenkacz/marathon-deployer:$TRAVIS_TAG"

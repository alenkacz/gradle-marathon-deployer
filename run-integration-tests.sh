#!/bin/bash

docker build --tag=build-image .
docker network create --driver bridge my-network
docker run --network my-network -v $(pwd):/build -v $HOME/.m2:/root/.m2 -v $HOME/.gradle:/root/.gradle -v /var/run/docker.sock:/var/run/docker.sock build-image test --info
docker network rm my-network
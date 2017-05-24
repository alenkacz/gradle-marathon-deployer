#!/bin/sh
curl -H "Content-Type: application/json" --data '{"docker_tag": "$TRAVIS_TAG"}' -X POST https://registry.hub.docker.com/u/alenkacz/marathon-deployer/trigger/$DOCKER_HUB_TOKEN/

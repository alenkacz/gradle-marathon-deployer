#!/bin/sh
curl -H "Content-Type: application/json" --data  '{"source_type": "Tag", "source_name": "$TRAVIS_TAG"}' -X POST https://registry.hub.docker.com/u/alenkacz/marathon-deployer/trigger/$DOCKER_HUB_TOKEN/

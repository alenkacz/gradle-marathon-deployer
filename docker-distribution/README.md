Docker image [alenkacz/marathon-deployer](https://hub.docker.com/r/alenkacz/marathon-deployer/) is available so you can actually use this plugin to deploy any application from any environment where Docker is available:
```bash
docker run \
    -e MARATHON_URL=http://path-to-your-marathon-instance.com \
    -e DOCKER_IMAGE_NAME=yourorg/app:1.0.0 \
    -v /path/to/your/marathon.json:/marathon.json
    alenkacz/marathon-deployer
```
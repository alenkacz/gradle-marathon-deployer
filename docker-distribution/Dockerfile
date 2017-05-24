FROM gradle:3.5-jre8-alpine
MAINTAINER varkockova.a@gmail.com

USER root
COPY build.gradle /
WORKDIR /
# SOURCE_BRANCH environment variable should be set while building image in DockerHub
# to cache all the required stuff so it's not downloaded when container starts
RUN gradle -PpluginVersion=$SOURCE_BRANCH tasks
ENTRYPOINT gradle -PpluginVersion=$SOURCE_BRANCH deployToMarathon
CMD []

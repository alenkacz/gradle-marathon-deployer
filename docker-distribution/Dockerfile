FROM gradle:3.5-jre8-alpine
MAINTAINER varkockova.a@gmail.com

USER root
COPY build.gradle /
WORKDIR /
ARG PLUGIN_VERSION
# ARGs are not persisted so we must store value of ARG to ENV
ENV PLUGIN_VERSION=$PLUGIN_VERSION
# to cache all the required stuff so it's not downloaded when container starts
RUN gradle -PpluginVersion=$PLUGIN_VERSION tasks
ENTRYPOINT gradle -PpluginVersion=$PLUGIN_VERSION deployToMarathon
CMD []

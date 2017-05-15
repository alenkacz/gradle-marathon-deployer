# gradle-marathon-deployer

[![Build Status](https://travis-ci.org/alenkacz/gradle-marathon-deployer.svg?branch=master)](https://travis-ci.org/alenkacz/gradle-marathon-deployer) [ ![Download](https://api.bintray.com/packages/alenkacz/maven/gradle-marathon-deployer/images/download.svg) ](https://bintray.com/alenkacz/maven/gradle-marathon-deployer/_latestVersion)

Gradle plugin that can deploy your application to Marathon (https://mesosphere.github.io/marathon/).

Apart from deploying your application to Marathon it also verifies, that the deployment is actually finished by querying the Marathon event stream.

If you want to deploy your application using canary deployments, *deployCanaryToMarathon* task takes the marathon.json and deploys it under the same name, just with "-canary" as a suffix. Thanks to this, you can have both old and new version deployed in parallel.

Usage
====================
```groovy
	buildscript {
		repositories {
			jcenter()
		}
		dependencies {
			classpath 'cz.alenkacz.gradle:gradle-marathon-deployer:1.2.3'
		}
	}

	apply plugin: 'marathon-deploy'

    marathon {
	    url = 'http://path-to-your-marathon-instance.com'
	    dockerImageName = 'yourorg/app:1.0.0'
    }
```

Also [alenkacz/marathon-deployer](https://hub.docker.com/r/alenkacz/marathon-deployer/) Docker image is available so you can actually use this plugin to deploy any application from any environment where Docker is available:
```bash
docker run \
    -e MARATHON_URL=http://path-to-your-marathon-instance.com \
    -e DOCKER_IMAGE_NAME=yourorg/app:1.0.0 \
    -v /path/to/your/marathon.json:/marathon.json
    alenkacz/marathon-deployer
```

Properties
==========
| Gradle property | Docker environment variable | Default | Description |
| --------------- | --------------------------- | ------- | ----------- |
| `url`           | `MARATHON_URL`              | N/A     | url to your Marathon instance |
| `dockerImageName` | `DOCKER_IMAGE_NAME`              | N/A     | full image name that will be provided to Marathon, use this only if you want to override the image name provided in your json (e.g. you want to deploy a testing version as a part of your CI/CD pipeline) |
| `verificationTimeout` | `MARATHON_DEPLOY_TIMEOUT_SECONDS` | 90 seconds | timeout when querying Marathon to verify that there are no pending deployments left |
| `deploymentRequestTimeout` | `MARATHON_DEPLOY_REQUEST_TIMEOUT_SECONDS` | 5 seconds | timeout for the initial deployment request |
| `jvmOverhead` | `JVM_OVERHEAD` | 200 MB | number of MBs that the container needs on top of JVM app memory |
| `forceDeployment` | `FORCE_DEPLOYMENT` | N/A     | url to your Marathon instance |
| `pathToJsonFile` | N/A | `deploy/marathon.json` | Gradle project relative path to your json file |

Tasks
=====
Tasks are added under the publishing group in yous gradle project.

- *deployToMarathon* - deploys your application to Marathon
- *deployCanaryToMarathon* - canary deployment on top of Marathon. Creates new application id (just the original application id with postfix "-canary") and pushes that to Marathon in one instance. Read more about [canary deployment](http://martinfowler.com/bliki/CanaryRelease.html)
- *printMarathonJson* - prints out the marathon json that would be used when deploying to Marathon. Good for debugging...

Extended Marathon json
======================
To support some common use cases when deploying applications to Marathon, this plugin also supports richer version of Marathon json.

JVM memory
----------
Running JVM apps in containers can be sometimes painful because of its extensive memory demand. To make it easier for JVM developers, marathon json can be enriched by property *jvmMem* that will contain number of megabytes your JVM app needs. Memory of the whole container will then be altered to reflect the constant overhead of the JVM itself so that your container does not go out of memory.

If you want to start using this feature, just add a jvmMem property on the top level of your marathon json as in the following example:

	{
        "id": "/product/service/myApp",
        "jvmMem": 256
        ... other marathon json properties
    }

For this to work, when starting your JVM app, it must pass JAVA_OPTS environment variable to the JVM. JAVA_OPTS is handled automatically for you if you use [distribution plugin](https://docs.gradle.org/current/userguide/distribution_plugin.html).

CPU profile
-----------
If you don't want to specify cpus option in marathon.json, you can get it computed for you. Simply omit cpus field in marathon.json. The cpus number gets calculated from required memory and Mesos resources ratio (number of CPUS / total RAM size). You can change cpus count policy by adding field cpuProfile with one of these values (low, normal, high). The default value is normal. Low option means that the computed value is multiplied by 0.3. High option means that the computed value is multiplied by 3.

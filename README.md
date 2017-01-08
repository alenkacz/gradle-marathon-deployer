# gradle-marathon-deployer

[![Build Status](https://travis-ci.org/alenkacz/gradle-marathon-deployer.svg)](https://travis-ci.org/alenkacz/gradle-marathon-deployer) [ ![Download](https://api.bintray.com/packages/alenkacz/maven/gradle-marathon-deployer/images/download.svg) ](https://bintray.com/alenkacz/maven/gradle-marathon-deployer/_latestVersion)

Gradle plugin that can deploy your application to Marathon (https://mesosphere.github.io/marathon/).

Apart from deploying your application to Marathon it also verifies, that the deployment is actually finished by querying the Marathon event stream.

If you want to deploy your application using canary deployments, *deployCanaryToMarathon* task takes the marathon.json and deploys it under the same name, just with "-canary" as a suffix. Thanks to this, you can have both old and new version deployed in parallel.

Usage
====================

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
    	url = "http://path-to-your-marathon-instance.com"
    }

Properties
====================
- *url* - url to your marathon application
- *dockerImageName* (OPTIONAL) - full image name that will be provided to marathon, use this only if you want to override the image name provided in your json (e.g. you want to deploy a testing version as a part of your CI/CD pipeline)
- *pathToJsonFile* (OPTIONAL) - project relative path to your json file, default is *deploy/marathon.json*
- *verificationTimeout* (OPTIONAL) - timeout when querying Marathon to verify that there are no pending deployments left, default is 90 seconds
- *deploymentRequestTimeout* (OPTIONAL) - timeout for the initial deployment request, default is 5 seconds
- *jvmOverhead* (OPTIONAL) - number of MBs that the container needs on top of JVM app memory (default is 200)

Tasks
====================
Tasks are added under the publishing group in yous gradle project.

- *deployToMarathon* - deploys your application to Marathon
- *deployCanaryToMarathon* - canary deployment on top of Marathon. Creates new application id (just the original application id with postfix "-canary") and pushes that to Marathon in one instance. Read more about [canary deployment](http://martinfowler.com/bliki/CanaryRelease.html)

JVM support
====================
Running JVM apps in containers can be sometimes painful because of its extensive memory demand. To make it easier for JVM developers, marathon json can be enriched by property *jvmMem* that will contain number of megabytes your JVM app needs. Memory of the whole container will then be altered to reflect the constant overhead of the JVM itself so that your container does not go out of memory.

If you want to start using this feature, just add a jvmMem property on the top level of your marathon json as in the following example:

	{
        "id": "/product/service/myApp",
        "jvmMem": 256
        ... other marathon json properties
    }

For this to work, when starting your JVM app, it must pass JAVA_OPTS environment variable to the JVM. JAVA_OPTS is handled automatically for you if you use [distribution plugin](https://docs.gradle.org/current/userguide/distribution_plugin.html).

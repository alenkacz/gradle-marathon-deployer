# gradle-marathon-deployer

[![Build Status](https://travis-ci.org/alenkacz/gradle-marathon-deployer.svg)](https://travis-ci.org/alenkacz/gradle-marathon-deployer) [ ![Download](https://api.bintray.com/packages/alenkacz/maven/gradle-marathon-deployer/images/download.svg) ](https://bintray.com/alenkacz/maven/gradle-marathon-deployer/_latestVersion)

Gradle plugin that can deploy your application to Marathon (https://mesosphere.github.io/marathon/).

Usage
====================

	buildscript {
		repositories {
			jcenter()
		}
		dependencies {
			classpath 'cz.alenkacz.gradle:gradle-marathon-deployer:1.0.16'
		}
	}

	apply plugin: 'cz.alenkacz.gradle.marathon.deploy'
    
    marathon {
      url = "http://path-to-your-marathon-instance.com"
  }

Properties
====================
- *url* - url to your marathon application
- *dockerImageName* (OPTIONAL) - full image name that will be provided to marathon, use this only if you want to override the image name provided in your json (e.g. you want to deploy a testing version as a part of your CI/CD pipeline)
- *pathToJson* (OPTIONAL) - project relative path to your json file, default is *deploy/marathon.json*

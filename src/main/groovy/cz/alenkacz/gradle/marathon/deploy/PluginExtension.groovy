package cz.alenkacz.gradle.marathon.deploy

import groovy.time.TimeDuration

class PluginExtension {
    String url
    String dockerImageName
    String pathToJsonFile
    TimeDuration verificationTimeout
    TimeDuration deploymentRequestTimeout
    Boolean forceDeployment
    /**
     * Memory overhead of the jvm application running in container
     * This will be used to alter mem property when jvmMem is used
     */
    Integer jvmOverhead

    PluginExtension() {
        pathToJsonFile = "deploy/marathon.json"
        verificationTimeout = new TimeDuration(0, 0, 90, 0)
        deploymentRequestTimeout = new TimeDuration(0, 0, 5, 0)
        jvmOverhead = 200
    }

    String getMarathonApiUrl() {
        return "${this.url}/v2"
    }
}

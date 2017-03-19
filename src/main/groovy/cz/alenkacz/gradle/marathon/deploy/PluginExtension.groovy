package cz.alenkacz.gradle.marathon.deploy

import groovy.time.TimeDuration

class PluginExtension {
    def String url
    def String marathonApiUrl
    def String dockerImageName
    def String pathToJsonFile
    def TimeDuration verificationTimeout
    def TimeDuration deploymentRequestTimeout
    def Boolean forceDeployment
    /**
     * Memory overhead of the jvm application running in container
     * This will be used to alter mem property when jvmMem is used
     */
    def Integer jvmOverhead

    public PluginExtension() {
        pathToJsonFile = "deploy/marathon.json"
        verificationTimeout = new TimeDuration(0, 0, 90, 0)
        deploymentRequestTimeout = new TimeDuration(0, 0, 5, 0)
        jvmOverhead = 200
    }

    public String getMarathonApiUrl() {
        return "${this.url}/v2"
    }
}

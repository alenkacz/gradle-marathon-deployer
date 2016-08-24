package cz.alenkacz.gradle.marathon.deploy

import groovy.time.TimeDuration

class PluginExtension {
    def String url
    def String dockerImageName
    def String pathToJsonFile
    def TimeDuration verificationTimeout
    def TimeDuration deploymentRequestTimeout

    public PluginExtension() {
        pathToJsonFile = "deploy/marathon.json"
        verificationTimeout = new TimeDuration(0, 0, 30, 0)
        deploymentRequestTimeout = new TimeDuration(0, 0, 5, 0)
    }
}

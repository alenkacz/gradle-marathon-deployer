package cz.alenkacz.gradle.marathon.deploy

class MarathonDeployPluginExtension {
    def String marathonUrl
    def String dockerImageName
    def pathToMarathonJsonFile

    public MarathonDeployPluginExtension() {
        pathToMarathonJsonFile = "deploy/marathon.json"
    }
}

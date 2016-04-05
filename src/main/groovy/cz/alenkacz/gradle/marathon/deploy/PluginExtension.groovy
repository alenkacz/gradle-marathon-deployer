package cz.alenkacz.gradle.marathon.deploy

class PluginExtension {
    def String marathonUrl
    def String dockerImageName
    def pathToMarathonJsonFile

    public PluginExtension() {
        pathToMarathonJsonFile = "deploy/marathon.json"
    }
}

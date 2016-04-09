package cz.alenkacz.gradle.marathon.deploy

class PluginExtension {
    def String url
    def String dockerImageName
    def pathToJsonFile

    public PluginExtension() {
        pathToJsonFile = "deploy/marathon.json"
    }
}

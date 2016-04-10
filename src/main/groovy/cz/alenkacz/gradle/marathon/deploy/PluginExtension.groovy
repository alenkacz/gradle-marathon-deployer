package cz.alenkacz.gradle.marathon.deploy

class PluginExtension {
    def String url
    def String dockerImageName
    def String pathToJsonFile

    public PluginExtension() {
        pathToJsonFile = "deploy/marathon.json"
    }
}

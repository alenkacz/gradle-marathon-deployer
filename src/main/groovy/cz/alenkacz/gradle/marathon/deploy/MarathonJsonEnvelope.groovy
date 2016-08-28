package cz.alenkacz.gradle.marathon.deploy

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class MarathonJsonEnvelope {
    private def Object parsedJson
    private PluginExtension pluginExtension

    public MarathonJsonEnvelope(PluginExtension pluginExtension) {
        this.pluginExtension = pluginExtension
        if (!pluginExtension.pathToJsonFile || !new File(pluginExtension.pathToJsonFile).exists()) {
            throw new Exception("Invalid path to marathon json ${pluginExtension.pathToJsonFile}")
        }

        def jsonSlurper = new JsonSlurper()
        parsedJson = jsonSlurper.parse(new File(pluginExtension.pathToJsonFile))
    }

    String getApplicationId() {
        return parsedJson.id
    }

    String getFinalJson() {
        if (pluginExtension.dockerImageName) {
            parsedJson.container.docker.image = pluginExtension.dockerImageName
        }
        return JsonOutput.toJson(parsedJson)
    }
}

package cz.alenkacz.gradle.marathon.deploy

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class MarathonJsonEnvelope {
    protected def Object parsedJson
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
        if (parsedJson.jvmMem != null) {
            parsedJson.mem = parsedJson.jvmMem + pluginExtension.jvmOverhead
            if (parsedJson.env == null) {
                def jsonBuilder = new JsonBuilder()
                jsonBuilder {
                    JAVA_OPTS: ""
                }
                parsedJson.env = jsonBuilder.content
            }
            def javaOptsBuilder = new StringBuilder("-Xmx${parsedJson.jvmMem}m")
            if (parsedJson.env.JAVA_OPTS != null && parsedJson.env.JAVA_OPTS != "") {
                javaOptsBuilder.append(" ")
                javaOptsBuilder.append(parsedJson.env.JAVA_OPTS)
            }
            parsedJson.env.JAVA_OPTS = javaOptsBuilder.toString()

        }
        return JsonOutput.toJson(parsedJson)
    }
}

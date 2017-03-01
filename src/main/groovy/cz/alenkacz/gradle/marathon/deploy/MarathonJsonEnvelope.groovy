package cz.alenkacz.gradle.marathon.deploy

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.logging.Logger

class MarathonJsonEnvelope {
    protected def Object parsedJson
    private PluginExtension pluginExtension
    private BigDecimal mesosResourcesRatio
    private Logger logger

    public MarathonJsonEnvelope(PluginExtension pluginExtension, BigDecimal mesosResourcesRatio = 1.0) {
        this.logger = logger
        this.pluginExtension = pluginExtension
        this.mesosResourcesRatio = mesosResourcesRatio
        if (mesosResourcesRatio <= 0) {
            throw new Exception("mesosResourcesRatio must be greater than zero")
        }
        if (!pluginExtension.pathToJsonFile || !new File(pluginExtension.pathToJsonFile).exists()) {
            throw new Exception("Invalid path to marathon json ${pluginExtension.pathToJsonFile}")
        }

        def jsonSlurper = new JsonSlurper()
        parsedJson = jsonSlurper.parse(new File(pluginExtension.pathToJsonFile))
    }

    String getApplicationId() {
        return parsedJson.id
    }

    String getFinalJson(Logger logger) {
        if (pluginExtension.dockerImageName) {
            logger.info("Rewriting container.docker.image property to ${pluginExtension.dockerImageName}")
            parsedJson.container.docker.image = pluginExtension.dockerImageName
        }
        if (parsedJson.jvmMem != null && parsedJson.mem == null) {
            parsedJson.mem = parsedJson.jvmMem + pluginExtension.jvmOverhead
            logger.info("jvmMem property found, setting memory to ${parsedJson.mem} and JAVA_OPTS -Xmx to ${parsedJson.jvmMem}")

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

        if (parsedJson.cpus == null && parsedJson.mem != null) {
            parsedJson.cpus = parsedJson.mem * mesosResourcesRatio

            if (parsedJson.cpuProfile != null && parsedJson.cpuProfile == "low") {
                parsedJson.cpus = parsedJson.cpus * 0.3
            }

            if (parsedJson.cpuProfile != null && parsedJson.cpuProfile == "high") {
                parsedJson.cpus = parsedJson.cpus * 3
            }

            if (parsedJson.labels == null) {
                parsedJson.labels = [:]
            }

            if (parsedJson.cpuProfile != null) {
                parsedJson.labels.cpu_profile = parsedJson.cpuProfile
            }
            else {
                parsedJson.labels.cpu_profile = "normal"
            }
        }

        return JsonOutput.prettyPrint(JsonOutput.toJson(parsedJson))
    }
}

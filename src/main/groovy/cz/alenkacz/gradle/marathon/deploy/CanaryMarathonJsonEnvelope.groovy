package cz.alenkacz.gradle.marathon.deploy

class CanaryMarathonJsonEnvelope extends MarathonJsonEnvelope {
    CanaryMarathonJsonEnvelope(PluginExtension pluginExtension) {
        super(pluginExtension)

        parsedJson.id = getApplicationId() + "-canary"
        parsedJson.instances = 1
    }
}

package cz.alenkacz.gradle.marathon.deploy

class CanaryMarathonJsonEnvelope extends MarathonJsonEnvelope {
    CanaryMarathonJsonEnvelope(PluginExtension pluginExtension, BigDecimal mesosResourcesRatio) {
        super(pluginExtension, mesosResourcesRatio)

        parsedJson.id = getApplicationId() + "-canary"
        parsedJson.instances = 1
    }
}

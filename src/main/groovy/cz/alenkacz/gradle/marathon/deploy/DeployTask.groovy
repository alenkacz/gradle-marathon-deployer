package cz.alenkacz.gradle.marathon.deploy

class DeployTask extends DeployTaskBase {
    DeployTask() {
        super({ PluginExtension pluginExtension, BigDecimal mesosResourceRatio -> new MarathonJsonEnvelope(pluginExtension, mesosResourceRatio) })

        group = 'publishing'
        description = 'Deploys your application to Marathon'
    }
}

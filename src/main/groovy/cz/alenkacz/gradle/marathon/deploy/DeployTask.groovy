package cz.alenkacz.gradle.marathon.deploy

class DeployTask extends DeployTaskBase {
    public DeployTask() {
        super({ PluginExtension pluginExtension -> new MarathonJsonEnvelope(pluginExtension) })

        group = 'publishing'
        description = 'Deploys your application to Marathon'
    }
}

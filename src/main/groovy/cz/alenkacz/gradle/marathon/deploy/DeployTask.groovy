package cz.alenkacz.gradle.marathon.deploy

class DeployTask extends DeployTaskBase {
    public DeployTask() {
        super({ PluginExtension marathonJsonPath -> new MarathonJsonEnvelope(marathonJsonPath) })

        group = 'publishing'
        description = 'Deploys your application to Marathon'
    }
}

package cz.alenkacz.gradle.marathon.deploy

class DeployTask extends DeployTaskBase {
    public DeployTask() {
        super({ PluginExtension marathonJsonPath -> new MarathonJsonEnvelope(marathonJsonPath, project.logger) })

        group = 'publishing'
        description = 'Deploys your application to Marathon'
    }
}

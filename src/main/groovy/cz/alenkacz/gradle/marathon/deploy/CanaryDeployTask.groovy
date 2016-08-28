package cz.alenkacz.gradle.marathon.deploy

class CanaryDeployTask extends DeployTaskBase {
    public CanaryDeployTask() {
        super({ PluginExtension marathonJsonPath -> new CanaryMarathonJsonEnvelope(marathonJsonPath) })

        group = 'publishing'
        description = 'Deploys canary of your application to Marathon'
    }
}

package cz.alenkacz.gradle.marathon.deploy

import org.gradle.api.Project
import org.gradle.api.Plugin

class MarathonDeployPlugin implements Plugin<Project> {
    void apply(Project target) {
        DeployTask deployTask = target.task('deployToMarathon', type: DeployTask)
        CanaryDeployTask canaryDeployTask = target.task('deployCanaryToMarathon', type: CanaryDeployTask)
        PrintTask printTask = target.task('printMarathonJson', type: PrintTask)
        PluginExtension extension = target.extensions.create('marathon', PluginExtension)

        deployTask.pluginExtension = extension
        canaryDeployTask.pluginExtension = extension
        printTask.pluginExtension = extension
    }
}

package cz.alenkacz.gradle.marathon.deploy

import org.gradle.api.Project
import org.gradle.api.Plugin

class MarathonDeployPlugin implements Plugin<Project> {
    void apply(Project target) {
        target.task('deployToMarathon', type: MarathonDeployTask)
        target.extensions.create('marathon', MarathonDeployPluginExtension)
    }
}

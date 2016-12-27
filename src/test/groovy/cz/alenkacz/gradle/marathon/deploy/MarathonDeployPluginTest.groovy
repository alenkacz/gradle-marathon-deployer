package cz.alenkacz.gradle.marathon.deploy

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class MarathonDeployPluginTest extends Specification {
    def "add deploy task to the project"() {
        when:
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply "cz.alenkacz.gradle.marathon.deploy"

        then:
        project.tasks.deployToMarathon instanceof DeployTask
    }

    def "support also short name of the plugin"() {
        when:
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply "marathon-deploy"

        then:
        project.tasks.deployToMarathon instanceof DeployTask
    }

    def "add canary deploy task to the project"() {
        when:
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply "cz.alenkacz.gradle.marathon.deploy"

        then:
        project.tasks.deployCanaryToMarathon instanceof CanaryDeployTask
    }

    def "add extension to the project"() {
        when:
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply "cz.alenkacz.gradle.marathon.deploy"

        then:
        project.extensions.findByName("marathon") instanceof PluginExtension
    }
}

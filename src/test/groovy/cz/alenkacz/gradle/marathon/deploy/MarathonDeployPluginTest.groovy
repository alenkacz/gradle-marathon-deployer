package cz.alenkacz.gradle.marathon.deploy

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class MarathonDeployPluginTest extends Specification {
    def "add task to the project"() {
        when:
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply "cz.alenkacz.gradle.marathon.deploy"

        then:
        project.tasks.deployToMarathon instanceof MarathonDeployTask
    }

    def "add extension to the project"() {
        when:
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply "cz.alenkacz.gradle.marathon.deploy"

        then:
        project.extensions.findByName("marathon") instanceof MarathonDeployPluginExtension
    }
}

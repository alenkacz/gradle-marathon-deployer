package cz.alenkacz.gradle.marathon.deploy

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class DeployTaskTest extends Specification  {
    def "fail because of missing marathon url"() {
        given:
        def project = ProjectBuilder.builder().build()
        project.plugins.apply 'cz.alenkacz.gradle.marathon.deploy'
        def extension = (PluginExtension) project.extensions.findByName('marathon')
        extension.setDockerImageName("imagename")

        when:
        project.tasks.deployToMarathon.deployToMarathon()

        then:
        Exception ex = thrown()
        ex.message.toLowerCase().contains("url")
    }

    def "fail because of incorrect path to json"() {
        given:
            def project = ProjectBuilder.builder().build()
            project.plugins.apply 'cz.alenkacz.gradle.marathon.deploy'
            def extension = (PluginExtension) project.extensions.findByName('marathon')
            extension.setPathToJsonFile("non/existing/path.json")
            extension.setDockerImageName("imagename")
            extension.setUrl("http://marathon.url")
        when:
            project.tasks.deployToMarathon.deployToMarathon()

        then:
            Exception ex = thrown()
            ex.message.toLowerCase().contains("non/existing/path.json")
    }
}

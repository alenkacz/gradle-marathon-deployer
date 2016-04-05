package cz.alenkacz.gradle.marathon.deploy

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class DeployTaskTest extends Specification  {
    def "fail because of incorrect path to json"() {
        given:
            def project = ProjectBuilder.builder().build()
            project.plugins.apply 'cz.alenkacz.gradle.marathon.deploy'
            ((PluginExtension) project.extensions.findByName('marathon'))
                .setPathToMarathonJsonFile("non/existing/path.json")
        when:
            project.tasks.deployToMarathon.deployToMarathon()

        then:
            Exception ex = thrown()
            ex.message.toLowerCase().contains("non/existing/path.json")
    }
}

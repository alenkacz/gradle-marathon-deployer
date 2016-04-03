package cz.alenkacz.gradle.marathon.deploy

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class MarathonDeployTaskTest extends Specification  {

    def "deploy to Marathon"() {
        given:
            def project = ProjectBuilder.builder().build()
            project.plugins.apply 'cz.alenkacz.gradle.marathon.deploy'
        when:
        project.tasks.deployToMarathon.deployToMarathon()

        then:
        noExceptionThrown()
    }
}

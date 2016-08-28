package cz.alenkacz.gradle.marathon.deploy

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class CanaryDeployTaskTest extends Specification {
    def "deploy canary of application to Marathon"() {
        given:
        def project = ProjectBuilder.builder().build()
        project.plugins.apply 'cz.alenkacz.gradle.marathon.deploy'
        def extension = (PluginExtension) project.extensions.findByName('marathon')
        def marathonUrl = MarathonMother.getMarathonUrl()
        extension.setUrl(marathonUrl)
        extension.setPathToJsonFile(MarathonJsonMother.validMarathonJsonPath())

        when:
        project.tasks.deployCanaryToMarathon.deployToMarathon()

        then:
        noExceptionThrown()
        MarathonMother.applicationIsDeployed("testcontainer-canary", marathonUrl)
    }
}

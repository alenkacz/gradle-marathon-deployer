package cz.alenkacz.gradle.marathon.deploy

import groovy.time.TimeDuration
import mesosphere.marathon.client.Marathon
import mesosphere.marathon.client.MarathonClient
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.IgnoreIf
import spock.lang.Specification

import java.time.Instant

class DeployTaskTest extends Specification {
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

    @IgnoreIf({ IntegrationTestHelper.shouldSkipIntegrationTests() })
    def "fail because of incorrect path to json"() {
        given:
            def project = ProjectBuilder.builder().build()
            project.plugins.apply 'cz.alenkacz.gradle.marathon.deploy'
            def extension = (PluginExtension) project.extensions.findByName('marathon')
            extension.setPathToJsonFile("non/existing/path.json")
            extension.setDockerImageName("imagename")
            extension.setUrl(MarathonMother.getMarathonUrl())
        when:
            project.tasks.deployToMarathon.deployToMarathon()

        then:
            Exception ex = thrown()
            ex.message.toLowerCase().contains("non/existing/path.json")
    }

    @IgnoreIf({ IntegrationTestHelper.shouldSkipIntegrationTests() })
    def "deploy application to Marathon"() {
        given:
        def project = ProjectBuilder.builder().build()
        project.plugins.apply 'cz.alenkacz.gradle.marathon.deploy'
        def extension = (PluginExtension) project.extensions.findByName('marathon')
        def marathonUrl = MarathonMother.getMarathonUrl()
        extension.setUrl(marathonUrl)
        extension.setPathToJsonFile(MarathonJsonMother.validMarathonJsonPath())

        when:
        project.tasks.deployToMarathon.deployToMarathon()

        then:
        noExceptionThrown()
        MarathonMother.applicationIsDeployed("testcontainer", marathonUrl)
    }

    @IgnoreIf({ IntegrationTestHelper.shouldSkipIntegrationTests() })
    def "deploy application to Marathon with force"() {
        given:
        def project = ProjectBuilder.builder().build()
        project.plugins.apply 'cz.alenkacz.gradle.marathon.deploy'
        def extension = (PluginExtension) project.extensions.findByName('marathon')
        def marathonUrl = MarathonMother.getMarathonUrl()
        extension.setUrl(marathonUrl)
        extension.setForceDeployment(true)
        extension.setPathToJsonFile(MarathonJsonMother.validMarathonJsonPath())

        when:
        project.tasks.deployToMarathon.deployToMarathon()

        then:
        noExceptionThrown()
        MarathonMother.applicationIsDeployed("testcontainer", marathonUrl)
    }

    @IgnoreIf({ IntegrationTestHelper.shouldSkipIntegrationTests() })
    def "fail when incorrect marathon url provided"() {
        given:
        def project = ProjectBuilder.builder().build()
        project.plugins.apply 'cz.alenkacz.gradle.marathon.deploy'
        def extension = (PluginExtension) project.extensions.findByName('marathon')
        extension.setUrl("http://nonexistingmarathonurl")
        extension.setPathToJsonFile(MarathonJsonMother.validMarathonJsonPath())

        when:
        project.tasks.deployToMarathon.deployToMarathon()

        then:
        Exception ex = thrown()
        ex in MarathonDeployerException
    }

    /*def "deploy application to Marathon with different jvm memory"() {
        given:
        def project = ProjectBuilder.builder().build()
        project.plugins.apply 'cz.alenkacz.gradle.marathon.deploy'
        def extension = (PluginExtension) project.extensions.findByName('marathon')
        def marathonUrl = MarathonMother.getMarathonUrl()
        extension.setUrl(marathonUrl)
        extension.setJvmOverhead(10)
        extension.setPathToJsonFile(MarathonJsonMother.jsonWithJvmMem(10))

        when:
        project.tasks.deployToMarathon.deployToMarathon()

        then:
        noExceptionThrown()
        MarathonMother.getApp("testcontainer-jvm", marathonUrl).app.mem == 20d
    }*/

    @IgnoreIf({ IntegrationTestHelper.shouldSkipIntegrationTests() })
    def "fail when container cannot be deployed and verification times out"() {
        given:
        def project = ProjectBuilder.builder().build()
        project.plugins.apply 'cz.alenkacz.gradle.marathon.deploy'
        def extension = (PluginExtension) project.extensions.findByName('marathon')
        extension.setUrl(MarathonMother.getMarathonUrl())
        extension.setPathToJsonFile(MarathonJsonMother.invalidMarathonJsonPath())
        extension.verificationTimeout = new TimeDuration(0, 0, 5, 0)

        when:
        project.tasks.deployToMarathon.deployToMarathon()

        then:
        Exception ex = thrown()
        ex in MarathonDeployerException
        ex.printStackTrace()
        ex.message.toLowerCase().contains("application deployment did not finish")
    }
}

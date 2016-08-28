package cz.alenkacz.gradle.marathon.deploy

import groovy.time.TimeDuration
import mesosphere.marathon.client.Marathon
import mesosphere.marathon.client.MarathonClient
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.time.Instant

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

    def "deploy application to Marathon"() {
        given:
        def project = ProjectBuilder.builder().build()
        project.plugins.apply 'cz.alenkacz.gradle.marathon.deploy'
        def extension = (PluginExtension) project.extensions.findByName('marathon')
        def marathonUrl = getMarathonUrl()
        extension.setUrl(marathonUrl)
        extension.setPathToJsonFile(validMarathonJsonPath())

        when:
        project.tasks.deployToMarathon.deployToMarathon()

        then:
        noExceptionThrown()
        applicationIsDeployed("testcontainer", marathonUrl)
    }

    def "fail when incorrect marathon url provided"() {
        given:
        def project = ProjectBuilder.builder().build()
        project.plugins.apply 'cz.alenkacz.gradle.marathon.deploy'
        def extension = (PluginExtension) project.extensions.findByName('marathon')
        extension.setUrl("http://nonexistingmarathonurl")
        extension.setPathToJsonFile(validMarathonJsonPath())

        when:
        project.tasks.deployToMarathon.deployToMarathon()

        then:
        Exception ex = thrown()
        ex in MarathonDeployerException
    }

    def "fail when container cannot be deployed and verification times out"() {
        given:
        def project = ProjectBuilder.builder().build()
        project.plugins.apply 'cz.alenkacz.gradle.marathon.deploy'
        def extension = (PluginExtension) project.extensions.findByName('marathon')
        extension.setUrl(getMarathonUrl())
        extension.setPathToJsonFile(invalidMarathonJsonPath())
        extension.verificationTimeout = new TimeDuration(0, 0, 5, 0)

        when:
        project.tasks.deployToMarathon.deployToMarathon()

        then:
        Exception ex = thrown()
        ex in MarathonDeployerException
        ex.printStackTrace()
        ex.message.toLowerCase().contains("application deployment did not finish")
    }

    private def String getMarathonUrl() {
        def marathonHost = System.getenv("MARATHON_HOST")
        def marathonPort = System.getenv("MARATHON_TCP_8080")
        return "http://$marathonHost:$marathonPort"
    }

    private def applicationIsDeployed(String name, String marathonUrl) {
        def marathon = MarathonClient.getInstance(marathonUrl)
        marathon.getApp(name) // throws exception "Not Found (http status: 404)" when not exists
    }

    private def validMarathonJsonPath() {
        def marathonFilePath = ""
        File.createTempFile("marathon",".json").with {
            deleteOnExit()

            write """{
  "id": "testcontainer",
  "cpus": 1,
  "mem": 128,
  "instances": 1,
  "cmd": "while [ true ] ; do echo 'Hello Marathon' ; sleep 5 ; done"
}"""
            marathonFilePath = absolutePath
        }
        return marathonFilePath
    }

    private def invalidMarathonJsonPath() {
        def marathonFilePath = ""
        File.createTempFile("marathon",".json").with {
            deleteOnExit()

            write """{
  "id": "testcontainerinvalid",
  "cpus": 1,
  "mem": 128,
  "instances": 1,
  "container": {
        "type": "DOCKER",
        "docker": {
            "image": "nonexisting/image"
        }
   }
}"""
            marathonFilePath = absolutePath
        }
        return marathonFilePath
    }
}

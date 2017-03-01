package cz.alenkacz.gradle.marathon.deploy

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class PrintTaskTest extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    def "print out the final marathon.json"() {
        given:
        buildFile << """
            plugins {
                id "cz.alenkacz.gradle.marathon.deploy"
            }

            marathon {
                dockerImageName = "imagename"
                pathToJsonFile = "${MarathonJsonMother.validDockerMarathonJsonPath()}"
                url = "${MarathonMother.getMarathonUrl()}"
            }
        """

        when:
        def actual = GradleRunner
                .create()
                .withProjectDir(testProjectDir.root)
                .withArguments("printMarathonJson")
                .withPluginClasspath()
                .build()

        then:
        actual.output.contains("""{
    "container": {
        "docker": {
            "image": "imagename"
        }
    },
    "cpus": 1,
    "id": "testcontainer",
    "instances": 1,
    "mem": 128
}""")
    }
}

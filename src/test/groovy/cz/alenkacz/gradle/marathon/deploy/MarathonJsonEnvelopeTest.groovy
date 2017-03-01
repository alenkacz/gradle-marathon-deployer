package cz.alenkacz.gradle.marathon.deploy

import groovy.json.JsonSlurper
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class MarathonJsonEnvelopeTest extends Specification {
    def "adjust memory setting based on jvmMem property"() {
        given:
        def marathonWithJvmMem = MarathonJsonMother.jsonWithJvmMem(128)
        def extension = new PluginExtension()
        extension.setPathToJsonFile(marathonWithJvmMem)
        extension.jvmOverhead = 200

        when:
        def target = new MarathonJsonEnvelope(extension)
        def actual = new JsonSlurper().parse(target.getFinalJson(new NoOpLogger()).toCharArray())

        then:
        actual.mem == 328
    }

    def "not adjust memory setting based on jvmMem property when mem is set too"() {
        given:
        def marathonWithJvmMem = MarathonJsonMother.jsonWithJvmMem(128, "", true)
        def extension = new PluginExtension()
        extension.setPathToJsonFile(marathonWithJvmMem)
        extension.jvmOverhead = 200

        when:
        def target = new MarathonJsonEnvelope(extension)
        def actual = new JsonSlurper().parse(target.getFinalJson(new NoOpLogger()).toCharArray())

        then:
        actual.mem == 10
    }

    def "add -Xmx property to JAVA_OPTS based on jvmMem property"() {
        given:
        def marathonWithJvmMem = MarathonJsonMother.jsonWithJvmMem(128)
        def extension = new PluginExtension()
        extension.setPathToJsonFile(marathonWithJvmMem)
        extension.jvmOverhead = 200

        when:
        def target = new MarathonJsonEnvelope(extension)
        def actual = new JsonSlurper().parse(target.getFinalJson(new NoOpLogger()).toCharArray())

        then:
        actual.env.JAVA_OPTS == "-Xmx128m"
    }

    def "prepend -Xmx property to other JAVA_OPTS based on jvmMem property"() {
        given:
        def marathonWithJvmMem = MarathonJsonMother.jsonWithJvmMem(128, "-Dabc=1")
        def extension = new PluginExtension()
        extension.setPathToJsonFile(marathonWithJvmMem)
        extension.jvmOverhead = 200

        when:
        def target = new MarathonJsonEnvelope(extension)
        def actual = new JsonSlurper().parse(target.getFinalJson(new NoOpLogger()).toCharArray())

        then:
        actual.env.JAVA_OPTS == "-Xmx128m -Dabc=1"
    }

    def "add cpus field based on cloud's cpu/memory ratio"() {
        given:
        def marathonWithoutCpus = MarathonJsonMother.validMarathonJsonWithoutCpusPath()
        def extension = new PluginExtension()
        extension.setPathToJsonFile(marathonWithoutCpus)
        def ratio = 0.1

        when:
        def target = new MarathonJsonEnvelope(extension, ratio)
        def actual = new JsonSlurper().parse(target.getFinalJson(new NoOpLogger()).toCharArray())

        then:
        actual.cpus == 1
    }
}

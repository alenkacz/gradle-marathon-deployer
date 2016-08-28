package cz.alenkacz.gradle.marathon.deploy

import spock.lang.Specification

class CanaryMarathonJsonEnvelopeTest extends Specification {
    def "rewrites application name with postfix -canary"() {
        def pluginExtension = new PluginExtension()
        pluginExtension.pathToJsonFile = MarathonJsonMother.validMarathonJsonPath()
        def actual = new CanaryMarathonJsonEnvelope(pluginExtension)

        actual.getApplicationId().endsWith("-canary")
    }

    def "rewrites number of instances to 1"() {
        def pluginExtension = new PluginExtension()
        pluginExtension.pathToJsonFile = MarathonJsonMother.validMarathonJsonPathWithMultipleInstances(8)
        def actual = new CanaryMarathonJsonEnvelope(pluginExtension)

        actual.getFinalJson().contains("instances\": 1,")
    }
}

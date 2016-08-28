package cz.alenkacz.gradle.marathon.deploy

import mesosphere.marathon.client.MarathonClient

class MarathonMother {
    static def String getMarathonUrl() {
        def marathonHost = System.getenv("MARATHON_HOST")
        def marathonPort = System.getenv("MARATHON_TCP_8080")
        return "http://$marathonHost:$marathonPort"
    }

    static def void applicationIsDeployed(String name, String marathonUrl) {
        def marathon = MarathonClient.getInstance(marathonUrl)
        marathon.getApp(name) // throws exception "Not Found (http status: 404)" when not exists
    }
}

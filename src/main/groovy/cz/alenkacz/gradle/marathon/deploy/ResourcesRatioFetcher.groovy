package cz.alenkacz.gradle.marathon.deploy

import groovy.json.JsonSlurper
import org.asynchttpclient.DefaultAsyncHttpClient

import java.nio.charset.StandardCharsets

class ResourcesRatioFetcher {

    def marathonApiUrl
    def AsyncHttpClient
    def jsonSlurper

    ResourcesRatioFetcher(String marathonApiUrl) {
        this.marathonApiUrl = marathonApiUrl
        this.asyncHttpClient = new DefaultAsyncHttpClient()
        this.jsonSlurper = new JsonSlurper()
    }

    public BigDecimal getMesosResourcesRatio() {

        println("Marathon API URL: ${this.marathonApiUrl}")

        String mesosUrl
        try {
            def marathon_result = asyncHttpClient.prepareGet("${this.marathonApiUrl}/info").execute().get()
            if (marathon_result.statusCode != 200) {
                throw new MarathonDeployerException("Marathon responded with code ${marathon_result.statusCode} when fetching info. Response: ${marathon_result.getResponseBody(StandardCharsets.UTF_8)}")
            }
            mesosUrl = jsonSlurper.parse(marathon_result.getResponseBodyAsBytes()).marathon_config.mesos_leader_ui_url
        } catch (Exception e) {
            throw new MarathonDeployerException("Error when requesting info from Marathon", e)
        }

        BigDecimal ratio
        try {
            def mesos_result = asyncHttpClient.prepareGet("${mesosUrl}state.json").execute().get()
            if (mesos_result.statusCode != 200) {
                throw new MarathonDeployerException("Mesos responded with code ${mesos_result.statusCode} when state.json. Response: ${mesos_result.getResponseBody(StandardCharsets.UTF_8)}")
            }
            def slaves = jsonSlurper.parse(mesos_result.getResponseBodyAsBytes()).slaves
            def cpus = slaves.collect { it.resources.cpus }.sum()
            def mem  = slaves.collect { it.resources.mem  }.sum()
            ratio = cpus / mem
        } catch (Exception e) {
            throw new MarathonDeployerException("Error when requesting state info from Mesos", e)
        }


        return ratio
    }

}


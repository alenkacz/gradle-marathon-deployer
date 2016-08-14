package cz.alenkacz.gradle.marathon.deploy

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.DefaultAsyncHttpClient
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class DeployTask extends DefaultTask {
    def PluginExtension pluginExtension
    def AsyncHttpClient asyncHttpClient
    def JsonSlurper jsonSlurper

    public DeployTask() {
        group = 'publishing'
        description = 'Deploys your application to Marathon'

        asyncHttpClient = new DefaultAsyncHttpClient()
        jsonSlurper = new JsonSlurper()
    }
    @TaskAction
    def deployToMarathon() {
        if (!pluginExtension.url) {
            throw new Exception("Missing required property marathon url")
        }

        if (!pluginExtension.pathToJsonFile || !new File(pluginExtension.pathToJsonFile).exists()) {
            throw new Exception("Invalid path to marathon json ${pluginExtension.pathToJsonFile}")
        }

        def marathonApiUrl = "${pluginExtension.url}/v2"
        def parsedJson = jsonSlurper.parse(new File(pluginExtension.pathToJsonFile))
        def String applicationId = parsedJson.id
        def marathonJson = getFinalMarathonJson(parsedJson)
        try {
            def result = asyncHttpClient.preparePut("${marathonApiUrl}/apps/${java.net.URLEncoder.encode(applicationId, StandardCharsets.UTF_8.toString())}").setBody(marathonJson).execute().get(pluginExtension.deploymentRequestTimeout.toMilliseconds(), TimeUnit.MILLISECONDS)
            if (result.statusCode != 200 && result.statusCode != 201) {
                throw new MarathonDeployerException("Marathon responded with code ${result.statusCode} when requesting deployment. Response: ${result.getResponseBody(StandardCharsets.UTF_8)}")
            }
        } catch (Exception e) {
            throw new MarathonDeployerException("Error when requesting to deploy application to Marathon", e)
        }

        def int notFinishedDeploymentsCount = verifyDeploymentFinished(marathonApiUrl, applicationId)
        if (notFinishedDeploymentsCount > 0) {
            throw new MarathonDeployerException("Unable to verify deployment. There are still $notFinishedDeploymentsCount deployments of this application not finished")
        } else {
            println("Deployment was successful")
        }
    }

    Object getFinalMarathonJson(Object parsedJson) {
        if (pluginExtension.dockerImageName) {
            parsedJson.container.docker.image = pluginExtension.dockerImageName
        }
        return JsonOutput.toJson(parsedJson)
    }

    private def int verifyDeploymentFinished(String marathonApiUrl, String applicationId) {
        def marathonDeploymentUrl = "$marathonApiUrl/deployments"
        def currentNumberOfDeployments = 666
        def startTime = new Date()
        while (currentNumberOfDeployments > 0 && !timedOut(pluginExtension.verificationTimeout, startTime, new Date())) {
            try {
                currentNumberOfDeployments = getNumberOfDeployments(marathonDeploymentUrl, applicationId)
            } catch (Exception e) {
                // intentionally nothing
                logger.warn("Unable to receive deployment information when validating deployment")
            }
            logger.debug("Number of deployments after verification: $currentNumberOfDeployments")
            sleep(500)
        }
        return currentNumberOfDeployments
    }

    boolean timedOut(TimeDuration limit, Date startTime, Date currentTime) {
        TimeCategory.minus(currentTime, startTime) > limit
    }

    private int getNumberOfDeployments(String marathonDeploymentUrl, String applicationId) {
        def deploymentCount = 0
        def result = asyncHttpClient.prepareGet(marathonDeploymentUrl).execute().get(3, TimeUnit.SECONDS)
        def parsedDeployments = jsonSlurper.parse(result.getResponseBodyAsBytes())
        parsedDeployments.each { it.affectedApps.each { app -> if (app == "/$applicationId") { deploymentCount++ } } }
        return deploymentCount
    }
}

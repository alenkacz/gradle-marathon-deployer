package cz.alenkacz.gradle.marathon.deploy

import groovy.json.JsonSlurper
import groovy.time.TimeDuration
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.DefaultAsyncHttpClient
import org.glassfish.jersey.media.sse.SseFeature
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class DeployTaskBase extends DefaultTask  {
    def PluginExtension pluginExtension
    private def AsyncHttpClient asyncHttpClient
    private def Client client
    private def JsonSlurper jsonSlurper
    private Closure<MarathonJsonEnvelope> marathonJsonFactory

    public DeployTaskBase(Closure<MarathonJsonEnvelope> marathonJsonFactory) {
        this.marathonJsonFactory = marathonJsonFactory
        asyncHttpClient = new DefaultAsyncHttpClient()
        jsonSlurper = new JsonSlurper()
        client = ClientBuilder.newBuilder()
                .register(SseFeature.class).build()
    }

    @TaskAction
    def deployToMarathon() {
        if (!pluginExtension.url) {
            throw new Exception("Missing required property marathon url")
        }

        def marathonApiUrl = "${pluginExtension.url}/v2"
        def marathonJsonEnvelope = marathonJsonFactory(pluginExtension)
        def String applicationId = marathonJsonEnvelope.getApplicationId()
        def String marathonJson = marathonJsonEnvelope.getFinalJson()
        def String deploymentId
        def eventStream = new FinishedDeploymentVerifier(client, asyncHttpClient, jsonSlurper, marathonApiUrl, logger)
        // we need to start capturing deployments before making actual deployment request
        // if we have started reading the stream after the request, there might be a race condition of the deployment finishing before us attaching
        eventStream.startCapturingFinishedDeployments()

        try {
            def result = asyncHttpClient.preparePut(prepareMarathonDeployUrl(marathonApiUrl, applicationId)).setBody(marathonJson).execute().get(pluginExtension.deploymentRequestTimeout.toMilliseconds(), TimeUnit.MILLISECONDS)
            if (result.statusCode != 200 && result.statusCode != 201) {
                throw new MarathonDeployerException("Marathon responded with code ${result.statusCode} when requesting deployment. Response: ${result.getResponseBody(StandardCharsets.UTF_8)}")
            }
            deploymentId = jsonSlurper.parse(result.getResponseBodyAsBytes()).deploymentId
            logger.info("Initiated deployment of id $deploymentId")
        } catch (Exception e) {
            throw new MarathonDeployerException("Error when requesting to deploy application to Marathon", e)
        }

        if (!eventStream.isDeploymentFinished(deploymentId, new TimeDuration(0, 0, 30, 0))) {
            throw new MarathonDeployerException("The application deployment did not finish in the defined timeout. Deployment_success event for the initiated deployment did not appear in the event stream.")
        } else {
            println("Deployment was successful")
        }
    }

    private String prepareMarathonDeployUrl(String marathonApiUrl, String applicationId) {
        "${marathonApiUrl}/apps/${URLEncoder.encode(applicationId, StandardCharsets.UTF_8.toString())}${pluginExtension.forceDeployment ? "?force=true" : ""}"
    }
}


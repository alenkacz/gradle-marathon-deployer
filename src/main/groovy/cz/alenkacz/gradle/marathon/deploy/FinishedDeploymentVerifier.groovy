package cz.alenkacz.gradle.marathon.deploy

import groovy.json.JsonException
import groovy.json.JsonSlurper
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import org.asynchttpclient.*
import org.glassfish.jersey.media.sse.EventSource
import org.glassfish.jersey.media.sse.InboundEvent
import org.gradle.api.logging.Logger
import org.glassfish.jersey.media.sse.EventListener

import javax.ws.rs.client.Client
import javax.ws.rs.client.WebTarget
import java.util.concurrent.ConcurrentHashMap

class FinishedDeploymentVerifier {
    private String marathonApiUrl
    private Logger logger
    private JsonSlurper jsonSlurper
    private ConcurrentHashMap<String, Boolean> finishedDeployments
    private ListenableFuture<String> eventStreamFuture
    private EventSource eventSource
    private Client client

    FinishedDeploymentVerifier(Client client, JsonSlurper jsonSlurper, String marathonApiUrl, Logger logger) {
        this.client = client
        this.jsonSlurper = jsonSlurper
        this.logger = logger
        this.marathonApiUrl = marathonApiUrl
        this.finishedDeployments = new ConcurrentHashMap<>()
    }

    void startCapturingFinishedDeployments() {
        WebTarget target = client.target("$marathonApiUrl/events")
        eventSource = EventSource.target(target).build()
        EventListener listener = new EventListener() {
            void onEvent(InboundEvent inboundEvent) {
                def receivedEvent = inboundEvent.readData()
                logger.debug("Found deployment_success event in the marathon event stream")
                try {
                    def foundDeploymentId = jsonSlurper.parse(receivedEvent.getBytes()).id
                    finishedDeployments.put(foundDeploymentId, true)
                    logger.debug("New finished deployment of id $foundDeploymentId")
                } catch (JsonException e) {
                    logger.warn("Unexpected format of event in Marathon event stream: $receivedEvent", e)
                }
            }
        }
        eventSource.register(listener, "deployment_success")
        eventSource.open()
    }

    boolean isDeploymentFinished(String deploymentId, TimeDuration timeout) {
        def startTime = new Date()
        def deploymentFinished = false
        while (!timedOut(timeout, startTime, new Date())) {
            if (finishedDeployments.containsKey(deploymentId)) {
                logger.info("Found finished deployment of id $deploymentId in event stream")
                deploymentFinished = true
                break
            }
            logger.debug("No deployment of that id found, will retry in 100 milliseconds")
            sleep(100)
        }
        close()
        return deploymentFinished
    }

    static boolean timedOut(TimeDuration limit, Date startTime, Date currentTime) {
        TimeCategory.minus(currentTime, startTime) > limit
    }

    void close() {
        try {
            eventStreamFuture.done()
            eventSource.close()
        } catch (Exception ignored) {
            // ignoring
        }
    }
}

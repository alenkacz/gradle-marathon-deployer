package cz.alenkacz.gradle.marathon.deploy

import groovy.json.JsonException
import groovy.json.JsonSlurper
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import org.asynchttpclient.*
import org.gradle.api.logging.Logger

import java.util.concurrent.ConcurrentHashMap

class FinishedDeploymentVerifier {
    private AsyncHttpClient asyncHttpClient
    private String marathonApiUrl
    private Logger logger
    private JsonSlurper jsonSlurper
    private ConcurrentHashMap<String, Boolean> finishedDeployments
    private ListenableFuture<String> eventStreamFuture

    public FinishedDeploymentVerifier(AsyncHttpClient asyncHttpClient, JsonSlurper jsonSlurper, String marathonApiUrl, Logger logger) {
        this.jsonSlurper = jsonSlurper
        this.logger = logger
        this.marathonApiUrl = marathonApiUrl
        this.asyncHttpClient = asyncHttpClient
        this.finishedDeployments = new ConcurrentHashMap<>()
    }

    public void startCapturingFinishedDeployments() {
        eventStreamFuture = asyncHttpClient.prepareGet("$marathonApiUrl/events").addHeader("Accept", "text/event-stream").execute(new AsyncHandler<String>() {

            @Override
            void onThrowable(Throwable t) {
                logger.error("Exception thrown when reading Marathon event stream, trying to make sure that deployment was finished", t)
            }

            StringBuilder currentEvent = new StringBuilder()

            // Example of event that we are interested in:
            // event: deployment_success
            // data: {"id":"bc7b0fbd-a9ad-4045-85bd-41fbad569abe","plan":{"id":"bc7b0fbd-a9ad-4045-85bd-41fbad569abe","original":{"id":"/","apps":[{"id":"/testcontainer-canary","cmd":"while [ true ] ; do echo 'Hello Marathon' ; sleep 5 ; done","args":null,"user":null,"env":{},"instances":1,"cpus":1,"mem":128,"disk":0,"executor":"","constraints":[],"uris":[],"fetch":[],"storeUrls":[],"ports":[10000],"portDefinitions":[{"port":10000,"protocol":"tcp","labels":{}}],"requirePorts":false,"backoffSeconds":1,"backoffFactor":1.15,"maxLaunchDelaySeconds":3600,"container":null,"healthChecks":[],"readinessChecks":[],"dependencies":[],"upgradeStrategy":{"minimumHealthCapacity":1,"maximumOverCapacity":1},"labels":{},"acceptedResourceRoles":null,"ipAddress":null,"version":"2016-09-04T17:48:40.394Z","residency":null,"versionInfo":{"lastScalingAt":"2016-09-04T17:48:40.394Z","lastConfigChangeAt":"2016-09-04T17:48:40.394Z"}}],"groups":[],"dependencies":[],"version":"2016-09-04T17:48:40.394Z"},"target":{"id":"/","apps":[{"id":"/testcontainer-canary","cmd":"while [ true ] ; do echo 'Hello Marathon' ; sleep 5 ; done","args":null,"user":null,"env":{},"instances":1,"cpus":1,"mem":128,"disk":0,"executor":"","constraints":[],"uris":[],"fetch":[],"storeUrls":[],"ports":[10000],"portDefinitions":[{"port":10000,"protocol":"tcp","labels":{}}],"requirePorts":false,"backoffSeconds":1,"backoffFactor":1.15,"maxLaunchDelaySeconds":3600,"container":null,"healthChecks":[],"readinessChecks":[],"dependencies":[],"upgradeStrategy":{"minimumHealthCapacity":1,"maximumOverCapacity":1},"labels":{},"acceptedResourceRoles":null,"ipAddress":null,"version":"2016-09-04T17:48:40.394Z","residency":null,"versionInfo":{"lastScalingAt":"2016-09-04T17:48:40.394Z","lastConfigChangeAt":"2016-09-04T17:48:40.394Z"}},{"id":"/testcontainer","cmd":"while [ true ] ; do echo 'Hello Marathon' ; sleep 5 ; done","args":null,"user":null,"env":{},"instances":1,"cpus":1,"mem":128,"disk":0,"executor":"","constraints":[],"uris":[],"fetch":[],"storeUrls":[],"ports":[10001],"portDefinitions":[{"port":10001,"protocol":"tcp","labels":{}}],"requirePorts":false,"backoffSeconds":1,"backoffFactor":1.15,"maxLaunchDelaySeconds":3600,"container":null,"healthChecks":[],"readinessChecks":[],"dependencies":[],"upgradeStrategy":{"minimumHealthCapacity":1,"maximumOverCapacity":1},"labels":{},"acceptedResourceRoles":null,"ipAddress":null,"version":"2016-09-04T17:48:46.846Z","residency":null,"versionInfo":{"lastScalingAt":"2016-09-04T17:48:46.846Z","lastConfigChangeAt":"2016-09-04T17:48:46.846Z"}}],"groups":[],"dependencies":[],"version":"2016-09-04T17:48:46.846Z"},"steps":[{"actions":[{"type":"StartApplication","app":"/testcontainer"}]},{"actions":[{"type":"ScaleApplication","app":"/testcontainer"}]}],"version":"2016-09-04T17:48:46.846Z"},"eventType":"deployment_success","timestamp":"2016-09-04T17:48:50.775Z"}
            @Override
            AsyncHandler.State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                def receivedEvent = new String(bodyPart.getBodyPartBytes(), "UTF-8")
                currentEvent.append(receivedEvent)

                if (!receivedEvent.contains("\r\n")) {
                    return AsyncHandler.State.CONTINUE
                } else {
                    if (receivedEvent.startsWith("event: deployment_success")) {
                        logger.debug("Found deployment_success event in the marathon event stream")
                        try {
                            def foundDeploymentId = jsonSlurper.parse(receivedEvent.split("data: ")[1].getBytes()).id
                            finishedDeployments.put(foundDeploymentId, true)
                            logger.debug("New finished deployment of id $foundDeploymentId")
                        } catch (JsonException e) {
                            logger.warn("Unexpected format of event in Marathon event stream: $receivedEvent", e)
                        }
                    }
                    currentEvent = new StringBuilder() // next message will be new event
                    logger.debug("Received new event from Marathon event stream, but it was not interesting one for deployment verification")
                    return AsyncHandler.State.CONTINUE
                }
            }

            @Override
            AsyncHandler.State onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
                def statusCode = responseStatus.getStatusCode()
                if (statusCode < 200 || statusCode >= 400) {
                    return AsyncHandler.State.ABORT
                }
                return AsyncHandler.State.CONTINUE
            }

            @Override
            AsyncHandler.State onHeadersReceived(HttpResponseHeaders headers) throws Exception {
                return AsyncHandler.State.CONTINUE
            }

            @Override
            String onCompleted() throws Exception {
                return ""
            }
        })
    }

    public boolean isDeploymentFinished(String deploymentId, TimeDuration timeout) {
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

    public void close() {
        try {
            eventStreamFuture.done()
        } catch (Exception ignored) {
            // ignoring
        }
    }
}

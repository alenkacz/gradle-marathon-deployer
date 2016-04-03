package cz.alenkacz.gradle.marathon.deploy

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MarathonDeployTask extends DefaultTask {
    @TaskAction
    def deployToMarathon() {
        doLast {
            project.exec { execSpec ->
                ArrayList<Object> dockerRunCmd = []
                dockerRunCmd.addAll(['docker', 'run', '-v', "${project.marathon.pathToMarathonJsonFile}:/marathon.json",
                                     '-e', "MARATHON_URL=${project.marathon.marathonUrl}",
                                     '-e', "DOCKER_IMAGE_NAME=${project.marathon.dockerImageName}"])

                dockerRunCmd << 'avastsoftware/marathon-deployer:latest'

                execSpec.commandLine 'docker', 'pull', 'avastsoftware/marathon-deployer:latest'

                execSpec.commandLine dockerRunCmd.toArray()
            }
        }
    }
}

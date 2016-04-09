package cz.alenkacz.gradle.marathon.deploy

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class DeployTask extends DefaultTask {
    def PluginExtension pluginExtension

    public DeployTask() {
        group = 'publishing'
        description = 'Deploys your application to Marathon'
    }
    @TaskAction
    def deployToMarathon() {
        project.exec { execSpec ->
            if (!pluginExtension.marathonUrl) {
                throw new Exception("Missing required property marathonUrl")
            }

            if (!pluginExtension.dockerImageName) {
                throw new Exception("Missing required property dockerImageName")
            }

            if (!pluginExtension.pathToMarathonJsonFile || !new File(project.marathon.pathToMarathonJsonFile).exists()) {
                throw new Exception("Invalid path to marathon json ${project.marathon.pathToMarathonJsonFile}")
            }

            ArrayList<Object> dockerRunCmd = []
            dockerRunCmd.addAll(['docker', 'run', '-v', "${project.marathon.pathToMarathonJsonFile}:/marathon.json",
                                 '-e', "MARATHON_URL=${project.marathon.marathonUrl}",
                                 '-e', "DOCKER_IMAGE_NAME=${project.marathon.dockerImageName}"])

            dockerRunCmd << 'avastsoftware/marathon-deployer:latest'

            execSpec.commandLine 'docker', 'pull', '-a', 'avastsoftware/marathon-deployer:latest'

            execSpec.commandLine dockerRunCmd.toArray()
        }
    }
}

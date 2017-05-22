package cz.alenkacz.gradle.marathon.deploy

class MarathonDeployerException extends Exception {
    MarathonDeployerException(String message) {
        super(message)
    }

    MarathonDeployerException(String message, Throwable cause) {
        super(message, cause)
    }
}

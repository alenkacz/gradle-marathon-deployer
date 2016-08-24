package cz.alenkacz.gradle.marathon.deploy

class MarathonDeployerException extends Exception {
    public MarathonDeployerException(String message) {
        super(message)
    }

    public MarathonDeployerException(String message, Throwable cause) {
        super(message, cause)
    }
}

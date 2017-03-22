package cz.alenkacz.gradle.marathon.deploy

class IntegrationTestHelper {
    public static boolean shouldSkipIntegrationTests() {
        !System.env['runIntegrationTests'].toString().toLowerCase().equals('true')
    }
}

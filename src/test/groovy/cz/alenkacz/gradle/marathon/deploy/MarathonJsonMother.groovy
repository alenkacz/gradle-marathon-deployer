package cz.alenkacz.gradle.marathon.deploy

class MarathonJsonMother {
    static def String validMarathonJsonPath() {
        def marathonFilePath = ""
        File.createTempFile("marathon",".json").with {
            deleteOnExit()

            write """{
  "id": "testcontainer",
  "cpus": 1,
  "mem": 128,
  "instances": 1,
  "cmd": "while [ true ] ; do echo 'Hello Marathon' ; sleep 5 ; done"
}"""
            marathonFilePath = absolutePath
        }
        return marathonFilePath
    }

    static def String validMarathonJsonPathWithMultipleInstances(Integer instancesCount) {
        def marathonFilePath = ""
        File.createTempFile("marathon",".json").with {
            deleteOnExit()

            write """{
  "id": "testcontainer",
  "cpus": 1,
  "mem": 128,
  "instances": ${instancesCount},
  "cmd": "while [ true ] ; do echo 'Hello Marathon' ; sleep 5 ; done"
}"""
            marathonFilePath = absolutePath
        }
        return marathonFilePath
    }

    static def String invalidMarathonJsonPath() {
        def marathonFilePath = ""
        File.createTempFile("marathon",".json").with {
            deleteOnExit()

            write """{
  "id": "testcontainerinvalid",
  "cpus": 1,
  "mem": 128,
  "instances": 1,
  "container": {
        "type": "DOCKER",
        "docker": {
            "image": "nonexisting/image"
        }
   }
}"""
            marathonFilePath = absolutePath
        }
        return marathonFilePath
    }
}

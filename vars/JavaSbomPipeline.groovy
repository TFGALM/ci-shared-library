import commonsteps.CloneRepositoryStage
import commonsteps.MavenBuildArtifactStage
import config.EnvironmentVariables
import model.CodeVariables
import service.MavenService
import service.ValidationService


def call(body) {
  
  config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()
  pipelineConfig = readYaml(text: libraryResource('config/dev.yml'))
  EnvironmentVariables environmentVariables = new EnvironmentVariables(this)
  CodeVariables codeVariables = environmentVariables.createEnvironmentVariables(pipelineConfig.repository)
  ValidationService validation = new ValidationService()
  
  pipeline {
    agent {
      kubernetes {
        yaml "${libraryResource 'config/java/builder21.yaml'}"
      }
    }
    options {
      buildDiscarder(logRotator(numToKeepStr: '50'))
      disableConcurrentBuilds(abortPrevious: false)
      timeout(time: 20, unit: 'MINUTES')
    }
    stages {
      stage("Clone repo") {
        environment {
          GIT_CREDS = credentials("${pipelineConfig.git.credentials.id}")
        }
        steps {
          script {
            container('base') {
              sh 'git config --global http.https://forgejo.alopezpa.homelab/.sslcainfo /mnt/certificate/ca-alopezpa-homelab-raiz.crt'
              echo codeVariables.toString()
              new CloneRepositoryStage(this).execute(codeVariables)
            }
          }
        }
      }
      stage("Build artifact") {
        environment {
          NEXUS_CREDS = credentials("${pipelineConfig.nexus.credentials.id}")
        }
        steps {
          script {
            container('java-maven') {
              new MavenBuildArtifactStage(this).execute(codeVariables.getNextVersion(), config)
            }
          }
        }
      }
      stage("Publish artifact") {
        environment {
          NEXUS_CREDS = credentials("${pipelineConfig.nexus.credentials.id}")
        }
        when {
          expression {
            return validation.isMergeWithMergedStatus(codeVariables.getActionType(),
                codeVariables.getMergeStatus())
          }
        }
        steps {
          script {
            container('java-maven') {
              MavenService mvn = new MavenService(this)
              mvn.setNewVersion(codeVariables.getNextVersion())
              mvn.deployArtifacts()
            }
          }
        }
      }
    }
  }
}

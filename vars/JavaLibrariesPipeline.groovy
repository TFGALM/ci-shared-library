import commonsteps.CloneRepositoryStage
import commonsteps.MavenBuildArtifactStage
import commonsteps.SonarScannerAnalysisStage
import commonsteps.BuildNotifier
import config.EnvironmentVariables
import service.MavenService
import model.CodeVariables
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
      stage("Sonar Scanner Analysis") {
        environment {
          SONAR_CREDS = credentials("${pipelineConfig.sonar.credentials.id}")
        }
        when {
          expression {
            return (validation.isMainBranch(codeVariables.getActionType(),codeVariables.getBranch())
                || validation.isHotfixOrFeatureOrBugfixBranch(codeVariables)
                || validation.isMergeWithMergedStatus(codeVariables.getActionType(), codeVariables.getMergeStatus()))
          }
        }
        steps {
          script {
            container('sonar-scanner') {
              try {
                new SonarScannerAnalysisStage(this).execute(codeVariables.getRepoName(), codeVariables.getBranch())
                codeVariables.setCurrentBuildResult('SUCCESS')
                new BuildNotifier(this).notifyBuildStatus(codeVariables)
              } catch (Exception e) {
                codeVariables.setCurrentBuildResult('FAILURE')
                new BuildNotifier(this).notifyBuildStatus(codeVariables)
              }
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

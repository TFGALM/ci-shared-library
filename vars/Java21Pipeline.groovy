import commonsteps.CloneRepositoryStage
import commonsteps.MavenBuildArtifactStage
import commonsteps.MavenPublishArtifactStage
import commonsteps.SonarScannerAnalysisStage
import commonsteps.BuildNotifier
import service.CraneService
import service.KanikoService
import config.EnvironmentVariables
import model.CodeVariables
import service.GitService
import service.TrivyService
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
      stage("Build container image") {
        environment {
          NEXUS_CREDS = credentials("${pipelineConfig.nexus.credentials.id}")
        }
        when {
          expression {
            return (validation.isMergedHotfixBranchBugfixTagOrFeatureTag(codeVariables)
                || validation.isHotFixBranch(codeVariables.getActionType(), codeVariables.getBranch(),
                codeVariables.getPreviousCommitSha()))
          }
        }
        steps {
          script {
            container('kaniko') {
              KanikoService kaniko = new KanikoService(this)
              codeVariables.setPipelineType("java")
              codeVariables.setDefaultDockerImage("quarkus21native")
              codeVariables.setDefaultBinaryPath("")
              kaniko.buildTarImage(codeVariables)
            }
          }
        }
      }
      stage("trivy") {
        environment {
          GIT_CREDS = credentials("${pipelineConfig.git.credentials.id}")
        }
        when {
          expression {
            return (validation.isHotFixBranch(codeVariables.getActionType(), codeVariables.getBranch(),
                codeVariables.getPreviousCommitSha())
                || validation.isMergeWithMergedStatus(codeVariables.getActionType(), codeVariables.getMergeStatus()))
          }
        }
        steps {
          script {
            container('trivy') {
              TrivyService trivy = new TrivyService(this)
              trivy.executeCodeAndImageAnalysis(codeVariables)
              GitService git = new GitService(this)
              git.uploadSecurityReport(codeVariables)
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
            return (validation.isMergedHotfixBranchBugfixTagOrFeatureTag(codeVariables)
                || validation.isHotFixBranch(codeVariables.getActionType(), codeVariables.getBranch(),
                codeVariables.getPreviousCommitSha()))
          }
        }
        steps {
          script {
            container('java-maven') {
              new MavenPublishArtifactStage(this).execute()
            }
          }
        }
      }
      stage("Publish Image") {
        environment {
          HARBOR_CREDS = credentials("${pipelineConfig.crane.credentials.id}")
        }
        when {
          expression {
            return (validation.isMergedHotfixBranchBugfixTagOrFeatureTag(codeVariables)
                || validation.isHotFixBranch(codeVariables.getActionType(), codeVariables.getBranch(),
                codeVariables.getPreviousCommitSha()))
          }
        }
        steps {
          script {
            container('crane') {
              CraneService crane = new CraneService(this, "${HARBOR_CREDS_USR}",
                  "${HARBOR_CREDS_PSW}")
              crane.publishImageFromFile(codeVariables.getRepoGroup(), codeVariables.getRepoName(),
                  codeVariables.getNextVersion())
            }
          }
        }
      }
    }
  }
}

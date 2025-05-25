package service

import groovy.json.JsonSlurper

import java.util.regex.MatchResult
import utils.Constant

class SonarqubeService implements Serializable {
  
  private def script
  
  SonarqubeService(script) {
    this.script = script
  }
  
  boolean isValidQuality(String repoName, String branchName) {
    String sonarResult = getSonarResult(branchName)
    String analysisID = null
    int maxRetries = 15
    String responseTask
    String urlTaskId = getUrlTaskId(sonarResult)
    
    while ((!analysisID || analysisID.isEmpty()) && maxRetries > 0) {
      script.sh(script: 'sleep 1')
      responseTask = script.sh(script: 'curl -s -u ' + script.env.SONAR_CREDS + ': \'' + urlTaskId +'\'',
              returnStdout: true)
      analysisID = new JsonSlurper().parseText(responseTask).task?.analysisId
      maxRetries--
    }
    
    if (maxRetries >= 0 && analysisID && !analysisID.isEmpty()) {
      String responseQualityGate = script.sh(script: 'curl -s -u ' +
          script.env.SONAR_CREDS + ': ' +
          '\'' + "${script.pipelineConfig.services.sonar}" + '/api/qualitygates/project_status?analysisId=' +
          analysisID + '\'', returnStdout: true)
      Object reponseQualityGateJSON = new JsonSlurper().parseText(responseQualityGate)
      if (reponseQualityGateJSON.projectStatus.status == "ERROR") {
        String errorString = getError(repoName)
        script.echo("${errorString}")
        return false
      }
    } else {
      script.echo(Constant.errorSonarMaxRetries)
      throw new IllegalArgumentException(Constant.errorSonarMaxRetries)
    }
    return true
  }

  private String getSonarResult(String branchName) {
    String sonarHost = " -Dsonar.host.url=" + script.pipelineConfig.services.sonar
    String sonarLogin = " -Dsonar.token=" + script.env.SONAR_CREDS.toString()
    String sonarProperties = " -Dproject.settings=sonar-project.properties"
    String sonarBranch = " -Dsonar.branch.name=" + branchName
    
    String sonarScript = 'sonar-scanner ' +
        sonarHost + sonarLogin + sonarProperties + sonarBranch
    
    return script.sh(script: sonarScript, returnStdout: true).trim()
  }

  private String getUrlTaskId(String sonarResult) {
    MatchResult analysisLinkLine = sonarResult=~/INFO: More about the report processing at (.+)/
    return analysisLinkLine ? analysisLinkLine[0].get(1) : null
  }
  
  private String getError(repoName) {
    return "ERROR WITH QUALITY GATES: Check details on: ${script.pipelineConfig.urls.sonar}/dashboard?id=${repoName}"
  }
}

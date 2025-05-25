package commonsteps

import service.SonarqubeService
import utils.Constant


class SonarScannerAnalysisStage {
  
  private final Script script
  
  SonarScannerAnalysisStage(Script script) {
    this.script = script
  }
  
  void execute(String repoName, String branchName) {
    SonarqubeService sonarqube = new SonarqubeService(script)
    if (!sonarqube.isValidQuality(repoName, branchName)) {
      throw new IllegalStateException(Constant.errorInvalidQuality)
    }
  }
}

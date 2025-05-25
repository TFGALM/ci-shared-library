package commonsteps

import service.MavenService


class MavenPublishArtifactStage {
  
  private final Script script
  
  MavenPublishArtifactStage(Script script) {
    this.script = script
  }
  
  void execute() {
    MavenService mvn = new MavenService(script)
    mvn.deployArtifacts()
  }
}

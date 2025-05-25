package commonsteps

import service.MavenService


class MavenBuildArtifactStage {
  private final Script script
  
  MavenBuildArtifactStage(Script script) {
    this.script = script
  }
  
  void execute(String nextVersion, def config) {
    MavenService mvn = new MavenService(script)
    mvn.setSettingsfile()
    mvn.setNewVersion(nextVersion)
    mvn.buildApp(config)
  }
}

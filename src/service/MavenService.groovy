package service

class MavenService implements Serializable {
  
  private def script
  private String mavenOpts = "--no-transfer-progress " +
      "-Dmaven.repo.local=.m2/repository " +
      "-gs=.mvn/settings.xml " +
      "-t=.mvn/toolchains.xml"
  
  MavenService(script) {
    this.script = script
  }
  
  void setSettingsfile() {
    script.env.nexusUrl = script.pipelineConfig.services.nexus
    List<Map<String, String>> filesToWrite = [["resourceFile": "config/java/settings.xml",
                                               "writeFile"   : ".mvn/settings.xml"],
                                              ["resourceFile": "config/java/toolchains.xml",
                                               "writeFile"   : ".mvn/toolchains.xml"]]
    
    filesToWrite.each {file ->
      String content = script.libraryResource(file["resourceFile"].toString())
      script.writeFile file: file["writeFile"], text: content
    }
  }
  
  void buildApp(def config) {
    boolean checkstyle = config.mavenSkipCheckstyle
    boolean pmd = config.mavenSkipPmd
    boolean jacoco = config.mavenSkipJacoco
    boolean tests = config.mavenSkipTests
    script.sh("mvn clean package ${mavenOpts} '-DskipTests=${tests}' '-Dcheckstyle.skip=${checkstyle}' '-Dpmd.skip=${pmd}' -Ddependency-check.skip=true '-Djacoco.skip=${jacoco}' ")
    script.echo("Packaged successfully.")
  }
  
  void deployArtifacts() {
    script.sh("mvn deploy ${mavenOpts} -DskipTests -Dcheckstyle.skip=true -Dpmd.skip=true -Ddependency-check.skip=true -Djacoco.skip=true '-DaltDeploymentRepository=releases-repository::default::${script.pipelineConfig.services.nexus}/repository/maven-releases/' ")
    script.echo("Published to nexus successfully.")
  }
  
  def setNewVersion(String version) {
    script.sh("mvn versions:set versions:update-child-modules versions:commit ${mavenOpts} -DnewVersion=${version} ")
  }
}

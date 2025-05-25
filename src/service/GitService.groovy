package service

import enums.VersionType
import model.CodeVariables
import model.Version
import utils.Constant

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class GitService implements Serializable {
  
  private def script
  private VersionService versionService
  
  GitService(script, versionService) {
    this.script = script
    this.versionService = versionService
  }
  
  GitService(script) {
    this.script = script
  }
  
  void gitClone(CodeVariables codeVariables) {
    if (!codeVariables.getRepoUrl()) {
      script.echo(Constant.errorMissingSourceUrl)
      throw new IllegalArgumentException(Constant.errorMissingSourceUrl)
    }
    setSslVerify()
    String gitLoginWithUrl =
        getLoginWithUrl(codeVariables.getRepoUrl(), codeVariables.getGitUsrCred(),
            codeVariables.getGitPassCred())
    String commitId = codeVariables.getCommitId()
    script.sh("""
      rm -rf {,.[!.],..?}*
      git clone ${gitLoginWithUrl} .
      git checkout --detach $commitId
      echo 'Repository cloned successfully and switched to commit $commitId ...'
    """)
  }
  
  void uploadSecurityReport(CodeVariables codeVariables) {
    setSslVerify()
    String loginUrl =
        getLoginWithUrl("${script.pipelineConfig.urls.trivy}", codeVariables.getGitUsrCred(),
            codeVariables.getGitPassCred())
    String repoName = codeVariables.getRepoName()
    String nextVersion = codeVariables.getNextVersion()
    String reportPath = "/tmp/repo/" +
        codeVariables.getRepoGroup() + "/" + repoName + "/" + nextVersion + "/trivy"
    script.sh("""
      git clone -b main ${loginUrl} /tmp/repo
      mkdir -p ${reportPath} && cp trivyReport/* ${reportPath}/
      cd /tmp/repo
      git config --global user.email "Jenkins@alopezpa.homelab.es"
      git config --global user.name "Jenkins bot"
      pwd
      git add .
      git commit -m 'Trivy report ${repoName} version ${nextVersion} added.'
      git push -f origin main
      echo 'Trivy report ${repoName} version ${nextVersion} uploaded'
      """)
  }
  
  Version getAndPushNextTagVersion(CodeVariables codeVariables) {
    String title = codeVariables.getMergeRequestTitle().substring(0, 5)
    if (VersionType.isVersionTypeValid(title)) {
      return pushTag(
          versionService
              .getNextVersion(getCurrentVersion(), codeVariables.getMergeRequestTitle().substring(0, 5)),
          codeVariables)
    } else {
      script.echo(Constant.errorVersionNotSupported)
      throw new IllegalArgumentException(Constant.errorVersionNotSupported)
    }
  }
  
  void setAndPushHotfixTagVersion(CodeVariables codeVariables) {
    String timestamp = ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
    String tagName = codeVariables.getBranch().replace("/","_")+"_${timestamp}"
    codeVariables.setNextVersion(tagName)
    pushTagCommon(tagName, codeVariables)
  }
  
  private Version getCurrentVersion() {
    String actualTag = script
        .sh(script: 'git describe --tags --abbrev=0 origin/main || true', returnStdout: true)
        .trim()
    if (actualTag == '') {
      return new Version(0, 0, 0)
    }
    return versionService.getVersionFromString(actualTag)
  }
  
  private void pushTagCommon(String tagName, CodeVariables codeVariables) {
    deleteRemoteTagIfNecessary(codeVariables, tagName)
    script.sh("""
        git config --global user.email 'Jenkins@alopezpa.homelab.es'
        git config --global user.name 'Jenkins bot'
        git tag -a ${tagName} -m 'v${tagName}'
        git push origin ${tagName}
    """)
  }
  
  private Version pushTag(Version newVersion, CodeVariables codeVariables) {
    String tagName = newVersion.toString()
    pushTagCommon(tagName, codeVariables)
    return newVersion
  }
  
  private void deleteRemoteTagIfNecessary(CodeVariables codeVariables, String tagName) {
    String loginWithUrl = getLoginWithUrl(codeVariables.getRepoUrl(), codeVariables.getGitUsrCred(),
        codeVariables.getGitPassCred())
    def tagExists =
        script.sh(script: 'git ls-remote --tags ' + loginWithUrl + ' origin refs/tags/' + tagName,
            returnStdout: true)
    if (tagExists) {
      script.sh("""
        git push origin :refs/tags/$tagName"
        git tag -d $tagName 2>/dev/null"
        echo 'The tag $tagName was removed.'
      """)
    } else {
      script.echo("The tag $tagName not found.")
    }
  }
  
  private String getLoginWithUrl(String repoUrl, gitUsrCred, gitPassCred) {
    String login = 'https://' + gitUsrCred + ':' + gitPassCred + '@'
    return repoUrl.replaceAll('https://', login)
  }
  
  private void setSslVerify() {
    script.sh(Constant.caCertificate + """
        git config --global http.https://forgejo.alopezpa.homelab/.sslcainfo /tmp/certificate/ca-alopezpa-homelab-raiz.crt
      """)
  }
  
  String getBranchName() {
    String branchOutput = script.sh(script: 'git branch -r --contains HEAD', returnStdout: true).trim()
    String[] branchLines = branchOutput.readLines().findAll {
      it.trim()
    }
    String branchName
    
    if (branchLines[0].contains('->')) {
      branchName = branchLines[0].split('->')[1].trim().replace('origin/', '')
    } else {
      branchName = branchLines[0].trim().replace('origin/', '')
    }
    return branchName
  }
}

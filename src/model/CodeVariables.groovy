package model

class CodeVariables implements Serializable {
  
  private String repoUrl
  private String repoGroup
  private String repoName
  private String branch
  private String actionType
  private String mergeStatus
  private String mergeRequestTitle
  private String commitId
  private String nexusUrl
  private String pipelineType
  private String nextVersion
  private String gitUsrCred
  private String gitPassCred
  private String trivyUrl
  private String defaultDockerImage
  private String defaultBinaryPath
  private String currentBuildResult
  private String buildUrl
  private String repoHost
  private String previousCommitSha
  private String tagName
  
  CodeVariables(repoUrl, repoGroup, repoName, branch, actionType, mergeStatus, mergeRequestTitle,
      commitId, nexusUrl, trivyUrl,currentBuildResult,buildUrl,repoHost, previousCommitSha, tagName) {
    this.repoUrl = repoUrl
    this.repoGroup = repoGroup
    this.repoName = repoName
    this.branch = branch
    this.actionType = actionType
    this.mergeStatus = mergeStatus
    this.mergeRequestTitle = mergeRequestTitle
    this.commitId = commitId
    this.nexusUrl = nexusUrl
    this.nextVersion = "1.0.0-SNAPSHOT"
    this.trivyUrl = trivyUrl
    this.currentBuildResult = currentBuildResult
    this.buildUrl = buildUrl
    this.repoHost = repoHost
    this.previousCommitSha = previousCommitSha
    this.tagName = tagName
  }
  
  String getRepoUrl() {
    return repoUrl
  }
  
  void setRepoUrl(String repoUrl) {
    this.repoUrl = repoUrl
  }
  
  String getRepoGroup() {
    return repoGroup
  }
  
  void setRepoGroup(String repoGroup) {
    this.repoGroup = repoGroup
  }
  
  String getRepoName() {
    return repoName
  }
  
  void setRepoName(String repoName) {
    this.repoName = repoName
  }
  
  String getBranch() {
    return branch
  }
  
  void setBranch(String branch) {
    this.branch = branch
  }
  
  String getActionType() {
    return actionType
  }
  
  void setActionType(String actionType) {
    this.actionType = actionType
  }
  
  String getMergeStatus() {
    return mergeStatus
  }
  
  void setMergeStatus(String mergeStatus) {
    this.mergeStatus = mergeStatus
  }
  
  String getMergeRequestTitle() {
    return mergeRequestTitle
  }
  
  void setMergeRequestTitle(String mergeRequestTitle) {
    this.mergeRequestTitle = mergeRequestTitle
  }
  
  String getCommitId() {
    return commitId
  }
  
  void setCommitId(String commitId) {
    this.commitId = commitId
  }
  
  String getNexusUrl() {
    return nexusUrl
  }
  
  void setNexusUrl(String nexusUrl) {
    this.nexusUrl = nexusUrl
  }
  
  String getPipelineType() {
    return pipelineType
  }
  
  void setPipelineType(String pipelineType) {
    this.pipelineType = pipelineType
  }
  
  String getNextVersion() {
    return nextVersion
  }
  
  void setNextVersion(String nextVersion) {
    this.nextVersion = nextVersion
  }
  
  String getGitUsrCred() {
    return gitUsrCred
  }
  
  void setGitUsrCred(String gitUsrCred) {
    this.gitUsrCred = gitUsrCred
  }
  
  String getGitPassCred() {
    return gitPassCred
  }
  
  void setGitPassCred(String gitPassCred) {
    this.gitPassCred = gitPassCred
  }
  
  String getTrivyUrl() {
    return trivyUrl
  }
  
  void setTrivyUrl(String trivyUrl) {
    this.trivyUrl = trivyUrl
  }
  
  String getDefaultDockerImage() {
    return defaultDockerImage
  }
  
  void setDefaultDockerImage(String defaultDockerImage) {
    this.defaultDockerImage = defaultDockerImage
  }
  
  String getDefaultBinaryPath() {
    return defaultBinaryPath
  }
  
  String getCurrentBuildResult() {
    return currentBuildResult
  }
  
  void setCurrentBuildResult(String currentBuildResult) {
    this.currentBuildResult = currentBuildResult
  }
  
  String getBuildUrl() {
    return buildUrl
  }
  
  void setBuildUrl(String buildUrl) {
    this.buildUrl = buildUrl
  }
  
  String getRepoHost() {
    return repoHost
  }
  
  void setRepoHost(String repoHost) {
    this.repoHost = repoHost
  }
  
  void setDefaultBinaryPath(String defaultBinaryPath) {
    this.defaultBinaryPath = defaultBinaryPath
  }
  
  String getPreviousCommitSha() {
    return previousCommitSha
  }
  
  void setPreviousCommitSha(String previousCommitSha) {
    this.previousCommitSha = previousCommitSha
  }
  
  String getTagName() {
    return tagName
  }
  
  void setTagName(String tagName) {
    this.tagName = tagName
  }
  
  @Override
  public String toString() {
    return "CodeVariables{" + "repoUrl='" +
        repoUrl + '\'' + ", repoGroup='" +
        repoGroup + '\'' + ", repoName='" +
        repoName + '\'' + ", branch='" +
        branch + '\'' + ", actionType='" +
        actionType + '\'' + ", mergeStatus='" +
        mergeStatus + '\'' + ", mergeRequestTitle='" +
        mergeRequestTitle + '\'' + ", commitId='" +
        commitId + '\'' + ", nexusUrl='" +
        nexusUrl + '\'' + ", pipelineType='" +
        pipelineType + '\'' + ", nextVersion='" +
        nextVersion + '\'' + ", gitUsrCred='" +
        gitUsrCred + '\'' + ", gitPassCred='" +
        gitPassCred + '\'' + ", trivyUrl='" +
        trivyUrl + '\'' + ", defaultDockerImage='" +
        defaultDockerImage + '\'' + ", defaultBinaryPath='" +
        defaultBinaryPath + '\'' + ", currentBuildResult='" +
        currentBuildResult + '\'' + ", buildUrl='" +
        buildUrl + '\'' + ", repoHost='" +
        repoHost + '\'' + ", previousCommitSha='" +
        previousCommitSha + '\'' + ", tagName='" + tagName + '\'' + '}';
  }
}

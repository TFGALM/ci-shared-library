package config

import enums.EventActionType
import enums.MergeStatus
import utils.Constant
import model.CodeVariables


class EnvironmentVariables implements Serializable {
  
  private def script
  
  EnvironmentVariables(script) {
    this.script = script
  }
  
  
  CodeVariables createEnvironmentVariables(def repository) {
    switch (repository.toUpperCase()) {
      case "FORGEJO":
        return createForgejoVariables()
        break
      default:
        print "ERROR : repository unknown"
        throw new IllegalArgumentException("ERROR : repository unknown")
        break
    }
  }
  
  private CodeVariables createForgejoVariables() {
    String repoUrl = script.env.cloneUrl
    String[] splitResult = script.env.repositoryGroup.split('/', 2)
    String repoGroup = splitResult[0]
    String repoName = script.env.repositoryName
    
    List<String> commitAndBranchName = getCommitIdAndBranchName()
    String commitId = commitAndBranchName[0]
    
    return new CodeVariables(repoUrl,
        repoGroup, repoName, filterBranchName(commitAndBranchName[1]), getActionType(),
        getMergeState(), script.env.mergeTitle, commitId,
        script.pipelineConfig.services.nexus,
        script.pipelineConfig.urls.trivy,
        script.currentBuild.currentResult, script.env.BUILD_URL, script.pipelineConfig.urls.forgejo,
        script.env.previousCommitSha, filterTagName(commitAndBranchName[1]))
  }
  
  private String filterTagName(String branchName) {
    if (branchName.startsWith("refs/tags/")) {
      return branchName.substring(10)
    }
    return null
  }
  
  private String filterBranchName(String branchName) {
    if(branchName.startsWith("refs/heads/")) {
      return branchName.substring(11)
    }
    return branchName
  }
  
  private String getMergeState() {
    if(script.env.action == "closed" && script.env.isMerged == "false"){
      return MergeStatus.CLOSED.getPrefix()
    } else if(script.env.isMerged == "true") {
      return MergeStatus.MERGED.getPrefix()
    } else if (script.env.isMerged == "false") {
      return MergeStatus.OPENED.getPrefix()
    } else {
      return null
    }
  }
  
  private String getActionType() {
    if(script.env.action){
      return EventActionType.MERGE.getPrefix()
    } else if (script.env.ref.startsWith("refs/tags/")) {
      return EventActionType.TAG_PUSH.getPrefix()
    } else if (script.env.ref.startsWith("refs/heads/")) {
      return EventActionType.PUSH.getPrefix()
    } else {
      throw new IllegalArgumentException("Received event is not valid.")
    }
  }
  
  private List<String> getCommitIdAndBranchName() {
    
    if(script.env.isMerged == "true") {
      return [script.env.mergeCommitSha, script.env.pullRequestBranchName]
    } else if (script.env.isMerged == "false") {
      return [script.env.commitHeadSha, script.env.pullRequestBranchName]
    } else {
      return [script.env.commitPush, script.env.ref]
    }
  }
}

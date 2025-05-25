package commonsteps

import enums.BranchType
import enums.MergeStatus
import enums.VersionType
import model.CodeVariables
import service.GitService
import service.VersionService
import utils.Constant
import enums.EventActionType
import service.ValidationService

class CloneRepositoryStage {
  
  private final Script script
  private final GitService git
  private final ValidationService validationService
  
  CloneRepositoryStage(Script script) {
    this.script = script
    VersionService versionService = new VersionService()
    this.git = new GitService(script, versionService)
    this.validationService = new ValidationService()
  }
  
  void execute(CodeVariables codeVariables) {
    String[] branchParts = codeVariables.getBranch().toString().split('/')
    
    if (!validationService.isValidBranch(branchParts[0], codeVariables.getBranch())) {
      throw new IllegalArgumentException(Constant.errorInvalidBranch)
    }
    
    setGitCredentials(codeVariables)
    git.gitClone(codeVariables)
    
    if (codeVariables.getMergeStatus() != null) {
      manageMergeStatus(codeVariables)
    } else if(hasSetAndPusHotfixTagVersion(codeVariables)) {
      git.setAndPushHotfixTagVersion(codeVariables)
    } else if (codeVariables.getActionType() == EventActionType.TAG_PUSH.getPrefix()) {
      manageTagPush(codeVariables)
    }
    
    def repoConfFile = script.pipelineConfig.repoConfFile.replaceFirst("^/", "")
    script.projectConfig = [:]
    if (script.fileExists(repoConfFile)) {
      script.projectConfig = script.readYaml(file: repoConfFile)
    }
  }
  
  private boolean hasSetAndPusHotfixTagVersion(CodeVariables codeVariables){
    return (validationService.isHotFixBranch(codeVariables.getActionType(),
        codeVariables.getBranch(), codeVariables.getPreviousCommitSha())
        && codeVariables.getMergeStatus() == null)
  }
  
  private void setGitCredentials(CodeVariables codeVariables) {
    codeVariables.setGitUsrCred("${script.env.GIT_CREDS_USR}")
    codeVariables.setGitPassCred("${script.env.GIT_CREDS_PSW}")
  }
  
  private void manageTagPush(CodeVariables codeVariables) {
    if (codeVariables.getTagName().contains('/')) {
      throw new IllegalArgumentException(Constant.errorInvalidTagName)
    }
    codeVariables.setBranch(git.getBranchName().split("/")[0])
    String branch = codeVariables.getBranch()
    if (branch == BranchType.BUGFIX.getPrefix() || branch == BranchType.FEATURE.getPrefix()) {
      codeVariables.setNextVersion(codeVariables.getTagName())
    } else {
      throw new IllegalArgumentException(Constant.errorInvalidBranchType)
    }
  }
  
  private void manageMergeStatus(CodeVariables codeVariables) {
    String mergeStatus = codeVariables.getMergeStatus()
    
    if (mergeStatus == MergeStatus.MERGED.getPrefix()) {
      codeVariables.setNextVersion(git.getAndPushNextTagVersion(codeVariables).toString())
    } else if (mergeStatus == MergeStatus.OPENED.getPrefix()) {
      String mergeRequestTitle = codeVariables.getMergeRequestTitle().substring(0, 5)
      if (!VersionType.isVersionTypeValid(mergeRequestTitle)) {
        throw new IllegalArgumentException(Constant.errorInvalidMergeRequestTitle)
      }
    }
  }
}

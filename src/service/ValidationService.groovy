package service

import enums.EventActionType
import enums.MergeStatus
import enums.BranchType
import model.CodeVariables

class ValidationService {
  
  ValidationService() {
  }
  
  boolean isHotfixOrFeatureOrBugfixBranch(CodeVariables codeVariables){
    return (
        isHotFixBranch(codeVariables.getActionType(), codeVariables.getBranch(), codeVariables.getPreviousCommitSha())
        || isFeatureOrBugfixBranch(codeVariables.getActionType(), codeVariables.getBranch()))
  }
  boolean isFeatureOrBugfixBranch(String actionType, String branchName) {
    return (
        actionType == EventActionType.PUSH.getPrefix()
        && (branchName.startsWith(BranchType.FEATURE.getPrefix())
          || branchName.startsWith(BranchType.BUGFIX.getPrefix())))
  }
  
  boolean isMainBranch(String actionType, String branch){
    return (
        actionType == EventActionType.PUSH.getPrefix()
            && branch.startsWith(BranchType.MAIN.getPrefix()))
  }
  
  boolean isMergeWithMergedStatus(String actionType, String status) {
    return (
        actionType == EventActionType.MERGE.getPrefix()
        && status == MergeStatus.MERGED.getPrefix())
  }
  
  boolean isHotFixBranch(String actionType, String branch, String previousCommitSha){
    return (
        actionType == EventActionType.PUSH.getPrefix()
        && branch.startsWith(BranchType.HOTFIX.getPrefix())
        && !isFirstCommit(previousCommitSha))
  }
  
  boolean isValidTag(String actionType,String branch){
    return (actionType == EventActionType.TAG_PUSH.getPrefix()
        && (branch.startsWith(BranchType.BUGFIX.getPrefix())
        || (branch.startsWith(BranchType.FEATURE.getPrefix()))))
  }
  
  boolean isMergedHotfixBranchBugfixTagOrFeatureTag(CodeVariables codeVariables) {
    return (isMergeWithMergedStatus(codeVariables.getActionType(),codeVariables.getMergeStatus())
        || isHotFixBranch(codeVariables.getActionType(),codeVariables.getBranch(),codeVariables.getPreviousCommitSha())
        || isValidTag(codeVariables.getActionType(),codeVariables.getBranch()))
  }
  
  boolean isValidBranch(String branchPart, String branch) {
    return (BranchType.isBranchTypeValid(branchPart)
        || branch.toString().startsWith("refs/tags/"))
  }
  
  boolean isFirstCommit(String previousCommitSha) {
    return previousCommitSha.startsWith("00000")
  }
}

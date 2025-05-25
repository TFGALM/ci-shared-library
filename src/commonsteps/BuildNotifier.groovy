package commonsteps

import model.CodeVariables

class BuildNotifier {
  private final Script script
  
  BuildNotifier(Script script) {
    this.script = script
  }
  void notifyBuildStatus(CodeVariables codeVariables) {
    script.sh """
            curl -k -s -X POST "${codeVariables.getRepoHost() + "/api/v1/repos/" + codeVariables.getRepoGroup() + "/" + codeVariables.getRepoName()}/statuses/${codeVariables.getCommitId()}" -d '{
                  "context": "Build",
                  "description": "${codeVariables.getCurrentBuildResult().toLowerCase()}",
                  "state": "${codeVariables.getCurrentBuildResult().toLowerCase()}",
                  "target_url": "${codeVariables.getBuildUrl()}"
                  }' -H "Content-Type: application/json" -H "Authorization: token ${codeVariables.getGitPassCred()}"
           """
  }
}

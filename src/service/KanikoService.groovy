package service

import model.CodeVariables
import utils.Constant


class KanikoService implements Serializable {
  private def script
  
  KanikoService(script) {
    this.script = script
  }
  
  void buildTarImage(CodeVariables codeVariables) {
    
    def dockerfileResource = ('dockerfiles/' + codeVariables.getPipelineType() + '/Dockerfile.'
        + (script.projectConfig.runtime?.container ?: codeVariables.getDefaultDockerImage()))
    def binaryPath = (script.projectConfig.runtime?.'binary-path' ?: codeVariables.getDefaultBinaryPath())
    
    String content = script.libraryResource(dockerfileResource)
    script.writeFile(file: 'Dockerfile', text: content, encoding: 'UTF-8')
    String tarPath = codeVariables.getRepoName() + ".tar"
    
    String command = """
    /kaniko/executor \\
    --destination=dummie-image \\
    --no-push \\
    --tar-path='${tarPath}' \\
    --context=. \\
    --build-arg NEXUS_CREDS_USR=${script.env.NEXUS_CREDS_USR} \\
    --build-arg NEXUS_CREDS_PSW=${script.env.NEXUS_CREDS_PSW} \\
    --build-arg nexusUrl=${script.pipelineConfig.services.nexus} \\
    --build-arg BINARY_PATH='${binaryPath}' \\
    --registry-certificate harbor.alopezpa.homelab=/tmp/certificate/ca-alopezpa-homelab-raiz.crt
"""
    
    script.sh("${Constant.caCertificate}\n${command}")
  }
}

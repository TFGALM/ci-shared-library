package service

import utils.Constant
import model.CodeVariables

import java.time.Instant
import java.time.temporal.ChronoUnit


class TrivyService implements Serializable {
  
  private def script
  
  TrivyService(script) {
    this.script = script
  }
  
  void executeCodeAndImageAnalysis(CodeVariables codeVariables) {
    if (codeVariables?.getRepoName() == null || codeVariables.getNextVersion() == null) {
      script.echo(Constant.errorInvalidTrivyArguments)
      throw new IllegalArgumentException(Constant.errorInvalidTrivyArguments)
    }
    String inputFile = codeVariables.getRepoName() + ".tar"
    String nowDate = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
    String srcOutputFile = "trivyReport/src_${nowDate}.json"
    String imgOutputFile = "trivyReport/image_${nowDate}.json"
    script.echo("Repository and image analysis for vulnerabilities will start..")
    script.sh("""
      mkdir trivyReport
      trivy fs --db-repository "${script.pipelineConfig.harbor.trivyPath}${script.pipelineConfig.harbor.genericDb}" --java-db-repository "${script.pipelineConfig.harbor.trivyPath}${script.pipelineConfig.harbor.javaDb}" --insecure . --output ${srcOutputFile} --format json --license-full -q
      cp ${srcOutputFile} trivyReport/src_LATEST.json
      trivy image --db-repository "${script.pipelineConfig.harbor.trivyPath}${script.pipelineConfig.harbor.genericDb}" --java-db-repository "${script.pipelineConfig.harbor.trivyPath}${script.pipelineConfig.harbor.javaDb}" --insecure --input ${inputFile} --output ${imgOutputFile} --format json --license-full -q
      cp ${imgOutputFile} trivyReport/image_LATEST.json
    """)
    script.echo("Repository and image analysis for vulnerabilities successfully finished")
  }
}

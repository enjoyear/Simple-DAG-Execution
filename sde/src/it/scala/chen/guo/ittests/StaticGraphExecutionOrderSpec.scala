package chen.guo.ittests

import java.io.File

import chen.guo.dagexe.config.ConfigUtil
import chen.guo.dagexe.execution.{DAGExecution, ExecutableNode}
import chen.guo.test.common.UnitSpec
import chen.guo.util.EnvUtil

import scala.sys.process._


class StaticGraphExecutionOrderSpec extends UnitSpec {

  "this order test for a static graph execution" should "have a very high possibility to pass" in {
    val graphDefFile = getResourceFile("test-support/StaticGraphExecutionOrderSpec/GraphDef.conf")
    val nodeDefFile = getResourceFile("test-support/StaticGraphExecutionOrderSpec/NodeDef.conf")

    val nodeDefMap: Map[String, ExecutableNode] = ConfigUtil.getNodeDefConfig(nodeDefFile)
    val graphDefMap: Map[String, List[String]] = ConfigUtil.getGraphDefConfig(graphDefFile)

    new DAGExecution(nodeDefMap, graphDefMap).execute()

    println("Comparing output with ExpectedOutput.txt")
    val outputFilePath = EnvUtil.dereferenceSystemEnvironmentVariable("${SDE_HOME}/sde/src/it/resources/test-support/StaticGraphExecutionOrderSpec/output")
    val expectedOutputFilePath = EnvUtil.dereferenceSystemEnvironmentVariable("${SDE_HOME}/sde/src/it/resources/test-support/StaticGraphExecutionOrderSpec/ExpectedOutput.txt")
    val ret = {
      EnvUtil.dereferenceSystemEnvironmentVariable(s"diff $outputFilePath $expectedOutputFilePath") !
    }

    assert(ret == 0)
    new File(outputFilePath).delete()
  }
}

package chen.guo.ittests

import java.io.File

import chen.guo.dagexe.config.{ConfigUtil, DAG, ExecutableItem, ShellNode}
import chen.guo.dagexe.execution.DAGExecution
import chen.guo.test.common.UnitSpec
import chen.guo.util.EnvUtil

import scala.sys.process._


class StaticGraphExecutionOrderSpec2 extends UnitSpec {

  object NodesDef {
    val initialize = ShellNode("if [ -a ${SDE_HOME}/sde/src/it/resources/test-support/StaticGraphExecutionOrderSpec/output ]; then rm ${SDE_HOME}/sde/src/it/resources/test-support/StaticGraphExecutionOrderSpec/output; fi")

    val prepare1 = new SleepWriteNode("pre1", "600",
      "${SDE_HOME}/sde/src/it/resources/test-support/StaticGraphExecutionOrderSpec/output")

    val prepare2 = new SleepWriteNode("pre2", "100",
      "${SDE_HOME}/sde/src/it/resources/test-support/StaticGraphExecutionOrderSpec/output")

    val node1 = new SleepWriteNode("n1", "100",
      "${SDE_HOME}/sde/src/it/resources/test-support/StaticGraphExecutionOrderSpec/output")

    val node2 = new SleepWriteNode("n2", "600",
      "${SDE_HOME}/sde/src/it/resources/test-support/StaticGraphExecutionOrderSpec/output")

    val node3 = new SleepWriteNode("n3", "1200",
      "${SDE_HOME}/sde/src/it/resources/test-support/StaticGraphExecutionOrderSpec/output")

    val node4 = new SleepWriteNode("n4", "100",
      "${SDE_HOME}/sde/src/it/resources/test-support/StaticGraphExecutionOrderSpec/output")

    val node5 = new SleepWriteNode("n5", "1800",
      "${SDE_HOME}/sde/src/it/resources/test-support/StaticGraphExecutionOrderSpec/output")

    val node6 = new SleepWriteNode("n6", "100",
      "${SDE_HOME}/sde/src/it/resources/test-support/StaticGraphExecutionOrderSpec/output")

    val end1 = new SleepWriteNode("end1", "100",
      "${SDE_HOME}/sde/src/it/resources/test-support/StaticGraphExecutionOrderSpec/output")

    val end2 = new SleepWriteNode("end2", "600",
      "${SDE_HOME}/sde/src/it/resources/test-support/StaticGraphExecutionOrderSpec/output")
  }


  "this order test for a static graph execution" should "have a very high possibility to pass" in {

    val dag = new DAG()
    import NodesDef._
    dag.addEdges(initialize, List(prepare1, prepare2))
    dag.addEdges(prepare1, List(node1, node2, node3))
    dag.addEdge(prepare2, node4)
    dag.addEdge(node1, node5)
    dag.addEdge(node2, node5)
    dag.addEdge(node3, node6)
    dag.addEdge(node4, node6)
    dag.addEdges(node5, List(end1, end2))
    dag.addEdges(node6, List(end1, end2))

    dag.execute()

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

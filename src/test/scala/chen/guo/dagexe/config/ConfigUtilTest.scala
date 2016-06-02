package chen.guo.dagexe.config

import chen.guo.dagexe.execution.{ExecutableNode, ScriptNode}
import com.typesafe.config.ConfigFactory
import org.scalatest.FlatSpec

class ConfigUtilTest extends FlatSpec {
  "getNodeDefConfig" should "work" in {
    val config = ConfigFactory.parseString(
      s"""
         |{
         |  prepare1: {
         |    ARGS = ["bash /Users/chenguo/Simple-DAG-Execution/samples/prepare1.sh"],
         |    NODE_CLASS = "chen.guo.dagexe.execution.ScriptNode"
         |  },
         |
         |  prepare2: {
         |    ARGS = ["bash /Users/chenguo/Simple-DAG-Execution/samples/prepare2.sh"],
         |    NODE_CLASS = "chen.guo.dagexe.execution.ScriptNode"
         |  }
         |}
       """.stripMargin)

    val conf: Map[String, ExecutableNode] = ConfigUtil.getNodeDefConfig(config)
    assertResult(2)(conf.size)
    assertResult(ScriptNode("bash /Users/chenguo/Simple-DAG-Execution/samples/prepare1.sh"))(conf("prepare1"))
    assertResult(ScriptNode("bash /Users/chenguo/Simple-DAG-Execution/samples/prepare2.sh"))(conf("prepare2"))
  }
}

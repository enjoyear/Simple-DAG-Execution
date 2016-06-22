package chen.guo.dagexe.config

import chen.guo.test.common.UnitSpec
import com.typesafe.config.ConfigFactory

class ConfigUtilSpec extends UnitSpec {
  "getNodeDefConfig" should "work" in {
    val config = ConfigFactory.parseString(
      s"""
         |{
         |  prepare1: {
         |    ARGS = ["bash /path/to/script1.sh"],
         |    NODE_CLASS = "chen.guo.dagexe.config.ShellNode"
         |  },
         |
         |  prepare2: {
         |    ARGS = ["bash /path/to/script2.sh"],
         |    NODE_CLASS = "chen.guo.dagexe.config.ShellNode"
         |  }
         |}
       """.stripMargin)

    val conf: Map[String, ExecutableItem] = ConfigUtil.getNodeDefConfig(config)
    assertResult(2)(conf.size)
    assertResult(ShellNode("bash /path/to/script1.sh"))(conf("prepare1"))
    assertResult(ShellNode("bash /path/to/script2.sh"))(conf("prepare2"))
  }
}

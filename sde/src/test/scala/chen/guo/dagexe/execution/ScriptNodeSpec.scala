package chen.guo.dagexe.execution

import chen.guo.dagexe.config.ShellNode
import chen.guo.test.common.UnitSpec

class ScriptNodeSpec extends UnitSpec {
  "ScriptNode" should "run multiple bash commands" in {
    val exitCode = ShellNode("echo job1", "echo job2").execute()
    assert(exitCode == 0)
  }

  "ScriptNode" should "run until it fails" in {
    val exitCode = ShellNode("echo job1", "exit 1", "echo job2").execute()
    assert(exitCode == 1)
  }
}

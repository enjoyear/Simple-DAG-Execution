package chen.guo.dagexe.execution

import java.io.File

import chen.guo.test.common.UnitSpec

class ScriptNodeSpec extends UnitSpec {
  "ScriptNode" should "run bash script" in {
    val file: File = getResourceFile("test-support/print-word.sh")
    val exitCode = ScriptNode("bash " + file.getAbsolutePath).execute()
    assert(exitCode == 0)
  }
}

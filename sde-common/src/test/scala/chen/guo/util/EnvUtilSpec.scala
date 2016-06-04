package chen.guo.util

import chen.guo.test.common.UnitSpec

class EnvUtilSpec extends UnitSpec {

  "Everyone" should "set up the project path for this Project" in {
    val setupYourProjectPath = EnvUtil.dereferenceSystemEnvironmentVariable("Your project directory has been set to: ${SDE_HOME}")
    println(setupYourProjectPath)
  }
}

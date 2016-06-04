package chen.guo.util

object EnvUtil {
  val envRegex = "\\$\\{(\\w+)\\}|\\$(\\w+)".r
  val envVarMap = System.getenv()

  def dereferenceSystemEnvironmentVariable(target: String): String = {
    envRegex.replaceAllIn(target, regMatch => {
      val ms = regMatch.toString()
      envVarMap.get(
        if (ms.endsWith("}"))
          ms.substring(2, ms.length - 1)
        else
          ms.substring(1))
      match {
        case null => throw new RuntimeException(s"System Environment Variable NOT Defined for: $ms")
        case x => x
      }
    })
  }
}

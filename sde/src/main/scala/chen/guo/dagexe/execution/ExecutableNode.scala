package chen.guo.dagexe.execution

import chen.guo.dagexe.util.MessageBuilder
import org.slf4j.{Logger, LoggerFactory}

trait ExecutableNode {
  def execute(): Int
}

case class SleepNode(id: String, sleepTimeMillis: String) extends ExecutableNode {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)
  val sleepTime = sleepTimeMillis.toLong

  override def execute(): Int = {
    logger.info(MessageBuilder.build(
      s"Start executing '$id' at ${Thread.currentThread().getName}",
      s"Sleeping $sleepTime milli-seconds."))
    Thread.sleep(sleepTime)
    0
  }
}


case class ScriptNode(commands: String*) extends ExecutableNode {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def execute(): Int = {
    logger.info(s"Start executing: ${this}")

    val exitCodes = commands.map(cmd => {
      logger.info("Executing: " + cmd)

      val cmdExitCode = if (cmd.trim == "") 0
      else Runtime.getRuntime.exec(Array("/bin/bash", "-c", cmd)).waitFor

      logger.info(
        s"""${getClass.getName} partially finishes with code $cmdExitCode
           |Current command is: $cmd
           |Exit code is: $cmdExitCode
           """.stripMargin)
      cmdExitCode
    })

    if (exitCodes.forall(_ == 0)) {
      logger.info(MessageBuilder.build(
        s"${getClass.getName} finishes with code 0", s"The node is ${this}"))
      0
    }
    else {
      val exitCode: Int = exitCodes.filter(_ > 0).head
      logger.info(MessageBuilder.build(
        s"${getClass.getName} finishes with code $exitCode", s"The node is ${this}"))
      exitCode
    }
  }
}

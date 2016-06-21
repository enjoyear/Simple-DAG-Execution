package chen.guo.dagexe.config

import chen.guo.dagexe.util.MessageBuilder
import org.slf4j.{Logger, LoggerFactory}

/**
  * This node will execute a list of commands in order and stops immediately at the first one that fails.
  *
  * @param commands a list of bash commands to be executed
  */
case class ScriptNode(commands: String*) extends ExecutableNode {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def execute(): Int = {
    logger.info(s"Start executing: ${this}")

    val exitCode = commands.toSeq.foldLeft(0)((lastExit, cmd) =>
      lastExit match {
        case 0 =>
          logger.info("Executing: " + cmd)

          val cmdExitCode = if (cmd.trim == "") 0
          else Runtime.getRuntime.exec(Array("/bin/bash", "-c", cmd)).waitFor

          logger.info(s"${this} finishes cmd `$cmd` with code $cmdExitCode")
          cmdExitCode
        case x => x
      })

    if (exitCode == 0)
      logger.info(MessageBuilder.build(
        s"${getClass.getName} finishes with code 0", s"Current node is ${this}"))
    else
      logger.info(MessageBuilder.build(
        s"${getClass.getName} finishes with code $exitCode", s"Current node is ${this}"))

    exitCode
  }
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

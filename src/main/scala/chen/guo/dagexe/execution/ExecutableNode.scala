package chen.guo.dagexe.execution

import chen.guo.dagexe.util.MessageBuilder
import org.slf4j.{Logger, LoggerFactory}

import scala.sys.process._

trait ExecutableNode {
  def execute(): Int
}

case class SleepNode(id: String, sleepTimeMillis: Long) extends ExecutableNode {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def execute(): Int = {
    logger.info(MessageBuilder.build(
      s"Executing $id at ${Thread.currentThread().getName}",
      "Sleeping $sleepTimeMillis milli-seconds."))
    Thread.sleep(sleepTimeMillis)
    0
  }
}


case class ScriptNode(fileName: String) extends ExecutableNode {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def execute(): Int = {
    logger.info("Executing" + fileName)
    val cmd: String = s"bash $fileName"
    val ret = cmd !

    logger.info(MessageBuilder.build(
      s"${getClass.getName} finish with code $ret",
      "CMD is:" + cmd))

    ret
  }
}

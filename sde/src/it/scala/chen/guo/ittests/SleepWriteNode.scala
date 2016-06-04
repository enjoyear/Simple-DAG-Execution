package chen.guo.ittests

import java.io._
import java.util
import java.util.concurrent._

import chen.guo.dagexe.execution.SleepNode
import chen.guo.dagexe.util.MessageBuilder
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try

class SleepWriteNode(id: String, sleepTimeMillis: String, outputFilePath: String) extends SleepNode(id, sleepTimeMillis) {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def execute(): Int = {
    Try {
      logger.info(MessageBuilder.build(
        s"Start executing '$id' at ${Thread.currentThread().getName}",
        s"Sleeping $sleepTime milli-seconds."))
      Thread.sleep(sleepTime)

      val outputFile = new File(outputFilePath)
      if (!outputFile.exists()) {
        outputFile.createNewFile()
      }
      //Write to the queue immediately to keep the order as much as possible
      SleepWriteNode.buffer.put(id + System.lineSeparator)

      SleepWriteNode.lock.synchronized {
        val fw = new FileWriter(outputFile.getAbsoluteFile, true)
        val bw = new BufferedWriter(fw)
        bw.write(SleepWriteNode.buffer.take())
        bw.close()
      }
      0
    }.getOrElse(1)
  }
}

object SleepWriteNode {
  val buffer = new LinkedBlockingQueue[String]()
  val lock = this
}

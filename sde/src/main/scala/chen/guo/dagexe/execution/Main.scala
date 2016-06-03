package chen.guo.dagexe.execution

import java.util.Random

import chen.guo.dagexe.config.ConfigUtil._
import org.slf4j.{Logger, LoggerFactory}

object Main extends App {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)
  logger.info(s"Using node definition configuration file at ${args(0)}")
  logger.info(s"Using graph definition configuration file at ${args(1)}")

  private val nodeDefMap: Map[String, ExecutableNode] = getNodeDefConfig(args(0))
  private val graphDefMap: Map[String, List[String]] = getGraphDefConfig(args(1))

  private val testingMap: Map[String, SleepNode] =
    nodeDefMap.map(x => (x._1, SleepNode(x._1, 200 * (new Random().nextInt(7) + 1))))

  new DAGExecution(testingMap, graphDefMap).execute()
}

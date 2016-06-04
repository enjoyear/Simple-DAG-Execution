package chen.guo.dagexe.execution

import java.io.File

import chen.guo.dagexe.config.ConfigUtil._
import org.slf4j.{Logger, LoggerFactory}

object Main extends App {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)
  logger.info(s"Using node definition configuration file at ${args(0)}")
  logger.info(s"Using graph definition configuration file at ${args(1)}")

  private val nodeDefMap: Map[String, ExecutableNode] = getNodeDefConfig(new File(args(0)))
  private val graphDefMap: Map[String, List[String]] = getGraphDefConfig(new File(args(1)))

  new DAGExecution(nodeDefMap, graphDefMap).execute()
}

package chen.guo.dagexe.execution

import java.io.File

import chen.guo.dagexe.config.ConfigUtil._
import chen.guo.util.ErrorUtil
import com.beust.jcommander.converters.FileConverter
import com.beust.jcommander.{JCommander, Parameter, ParameterException}
import org.slf4j.{Logger, LoggerFactory}

object Main {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  @Parameter(names = Array("--graph"),
    description = "Set the graph definition configuration file",
    required = true,
    converter = classOf[FileConverter])
  private val graphDefFile: File = null

  @Parameter(names = Array("--nodes"),
    description = "Set the nodes definition configuration file",
    required = true,
    converter = classOf[FileConverter])
  private val nodesDefFile: File = null

  @Parameter(names = Array("-h", "-?", "-help", "--help"),
    help = true, hidden = true)
  private val help: Boolean = false


  def main(args: Array[String]) {
    var jCommander: JCommander = null
    try {
      jCommander = new JCommander(this, args: _*)
      jCommander.setProgramName(getClass.getName)

      if (help) {
        jCommander.usage()
        System.exit(0)
      }

      logger.info(s"Using graph definition configuration file at ${graphDefFile.getAbsolutePath}")
      logger.info(s"Using node definition configuration file at ${nodesDefFile.getAbsolutePath}")

      val graphDefMap: Map[String, List[String]] = getGraphDefConfig(graphDefFile)
      val nodeDefMap: Map[String, ExecutableNode] = getNodeDefConfig(nodesDefFile)

      new DAGExecution(nodeDefMap, graphDefMap).execute()
    }
    catch {
      case e: ParameterException =>
        val message: String = s"Failed parsing your arguments: ${e.getMessage}"
        logger.error(message)
        jCommander.usage()
        System.exit(1);
      case e: Throwable =>
        val errorMessage = ErrorUtil.getStackTrace(e)
        logger.error(errorMessage)
        System.exit(1)
    }
  }

}

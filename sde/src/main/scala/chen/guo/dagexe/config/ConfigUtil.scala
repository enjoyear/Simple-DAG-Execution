package chen.guo.dagexe.config

import java.io.File
import java.lang.reflect.Constructor
import java.util

import chen.guo.util.{EnvUtil, ErrorUtil}
import com.typesafe.config.{Config, ConfigFactory, ConfigObject}
import org.apache.commons.io.FileUtils
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.util.Try

object ConfigUtil {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  final val KEY_ARGS = "ARGS"
  final val KEY_NODE_CLASS = "NODE_CLASS"

  def getNodeDefConfig(nodeDefFile: File): Map[String, ExecutableItem] = {
    val scriptConfig: Config = ConfigFactory.parseString(getDereferenceString(nodeDefFile))
    getNodeDefConfig(scriptConfig)
  }

  def getNodeDefConfig(scriptConfig: Config): Map[String, ExecutableItem] = {
    scriptConfig.root.entrySet().asScala.map(x => x.getKey -> {
      val config = x.getValue.asInstanceOf[ConfigObject]

      val args: util.ArrayList[String] = config.get(KEY_ARGS).unwrapped().asInstanceOf[util.ArrayList[String]]
      val nodeClassString: String = config.get(KEY_NODE_CLASS).unwrapped().asInstanceOf[String]

      try {
        val constructorType = List.fill(args.size)(classOf[String])
        val nodeClass: Class[_] = Class.forName(nodeClassString)

        Try {
          //First try the constructor that accepts a variable list of arguments
          val constructor: Constructor[_] = nodeClass.getDeclaredConstructor(classOf[Seq[String]])
          constructor.newInstance(args.asScala).asInstanceOf[ExecutableItem]
        }.getOrElse {
          //Then try the constructor that accepts a fixed number of arguments
          val constructor: Constructor[_] = nodeClass.getDeclaredConstructor(constructorType.toArray: _*)
          constructor.newInstance(args.toArray: _*).asInstanceOf[ExecutableItem]
        }
      }
      catch {
        case e: NoSuchMethodException =>
          logger.error(
            s"""
               |Cannot find constructor of '$nodeClassString' that takes ${args.size} String(s).
               |The constructor arguments are $args
             """.stripMargin)
          logger.error(ErrorUtil.getStackTrace(e))
          sys.exit(1)
      }
    }).toMap
  }

  def getGraphDefConfig(graphDefFile: File): Map[String, List[String]] = {
    val graphConfig: Config = ConfigFactory.parseString(getDereferenceString(graphDefFile))
    graphConfig.entrySet().asScala.map(x =>
      x.getKey -> x.getValue.unwrapped().asInstanceOf[util.ArrayList[String]].asScala.toList).toMap
  }

  private def getDereferenceString(nodeDefFile: File): String = {
    val confString = FileUtils.readFileToString(nodeDefFile, "UTF-8")
    val deReferencedNodeDefString: String = EnvUtil.dereferenceSystemEnvironmentVariable(confString)
    logger.debug(deReferencedNodeDefString)
    deReferencedNodeDefString
  }
}

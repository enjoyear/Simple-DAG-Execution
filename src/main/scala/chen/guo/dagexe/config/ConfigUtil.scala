package chen.guo.dagexe.config

import java.io.File
import java.util

import chen.guo.dagexe.execution.ExecutableNode
import com.typesafe.config.{Config, ConfigFactory, ConfigObject}

import scala.collection.JavaConverters._

object ConfigUtil {
  final val KEY_ARGS = "ARGS"
  final val KEY_NODE_CLASS = "NODE_CLASS"

  def getNodeDefConfig(locationConfigFilePath: String): Map[String, ExecutableNode] = {
    val scriptConfig: Config = ConfigFactory.parseFile(new File(locationConfigFilePath))
    getNodeDefConfig(scriptConfig)
  }

  def getNodeDefConfig(scriptConfig: Config): Map[String, ExecutableNode] = {
    scriptConfig.root.entrySet().asScala.map(x => x.getKey -> {
      val config = x.getValue.asInstanceOf[ConfigObject]

      val args: util.ArrayList[String] = config.get(KEY_ARGS).unwrapped().asInstanceOf[util.ArrayList[String]]
      val nodeClass: String = config.get(KEY_NODE_CLASS).unwrapped().asInstanceOf[String]

      val constructorType = List.fill(args.size)(classOf[String])

      Class.forName(nodeClass).getDeclaredConstructor(constructorType.toArray: _*)
        .newInstance(args.toArray: _*).asInstanceOf[ExecutableNode]
    }).toMap
  }

  def getGraphDefConfig(graphDefConfigFilePath: String): Map[String, List[String]] = {
    val graphConfig: Config = ConfigFactory.parseFile(new File(graphDefConfigFilePath))
    graphConfig.entrySet().asScala.map(x =>
      x.getKey -> x.getValue.unwrapped().asInstanceOf[util.ArrayList[String]].asScala.toList).toMap
  }
}

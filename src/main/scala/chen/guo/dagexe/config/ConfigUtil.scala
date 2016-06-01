package chen.guo.dagexe.config

import java.io.File
import java.util

import com.typesafe.config.{Config, ConfigFactory, ConfigObject}

import scala.collection.JavaConverters._

object ConfigUtil {
  final val KEY_LOCATION = "LOCATION"
  final val KEY_NODE_CLASS = "NODE_CLASS"

  def getNodeDefConfig(locationConfigFilePath: String): Map[String, Map[String, String]] = {
    val scriptConfig: Config = ConfigFactory.parseFile(new File(locationConfigFilePath))
    scriptConfig.root.entrySet().asScala.map(x => x.getKey -> {
      val config = x.getValue.asInstanceOf[ConfigObject]
      Map(
        KEY_LOCATION -> config.get(KEY_LOCATION).unwrapped().asInstanceOf[String],
        KEY_NODE_CLASS -> config.get(KEY_NODE_CLASS).unwrapped().asInstanceOf[String]
      )
    }).toMap
  }

  def getGraphDefConfig(graphDefConfigFilePath: String): Map[String, List[String]] = {
    val graphConfig: Config = ConfigFactory.parseFile(new File(graphDefConfigFilePath))
    graphConfig.entrySet().asScala.map(x =>
      x.getKey -> x.getValue.unwrapped().asInstanceOf[util.ArrayList[String]].asScala.toList).toMap
  }
}

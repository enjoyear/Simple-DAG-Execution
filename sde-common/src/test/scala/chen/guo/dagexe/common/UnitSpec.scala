package chen.guo.dagexe.common

import java.io.File

import org.scalatest.FlatSpec

abstract class UnitSpec extends FlatSpec {

  def getResourceFile(resourcePath: String): File = {
    val url = getClass.getResource("/" + resourcePath)
    if (url == null)
      throw new RuntimeException(s"File $resourcePath cannot be found.")
    new File(url.getPath)
  }

}

package chen.guo.test.common

import java.io.File

import org.scalatest.FlatSpec

abstract class UnitSpec extends FlatSpec {

  def getResourceFile(resourcePath: String): File = {
    val resourceFilePath = getClass.getResource("/" + resourcePath)
    if (resourceFilePath == null)
      throw new RuntimeException(s"Resource file $resourcePath cannot be found.")
    new File(resourceFilePath.getPath)
  }

}

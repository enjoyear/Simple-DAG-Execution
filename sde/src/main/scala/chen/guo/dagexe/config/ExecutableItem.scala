package chen.guo.dagexe.config

import scala.collection.mutable

trait ExecutableItem {
  def execute(): Int
}

trait DirectedExecutableItem extends ExecutableItem {
  private val children = mutable.ArrayBuffer[DirectedExecutableItem]()
  private val parents = mutable.ArrayBuffer[DirectedExecutableItem]()

  def addChild(child: DirectedExecutableItem): Unit = {
    children += child
  }

  def addParent(parent: DirectedExecutableItem): Unit = {
    parents += parent
  }

  def getParents: Vector[DirectedExecutableItem] = parents.toVector

  def getChildren: Vector[DirectedExecutableItem] = children.toVector
}


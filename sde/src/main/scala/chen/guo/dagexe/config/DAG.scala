package chen.guo.dagexe.config

import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable

class DAG extends DirectedExecutableItem {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)
  private val allExecutables = mutable.HashSet[DirectedExecutableItem]()

  def addEdge(from: DirectedExecutableItem, to: DirectedExecutableItem): Unit = {
    allExecutables += from
    allExecutables += to
    from.addChild(to)
    to.addParent(from)
  }

  def addEdges(froms: List[DirectedExecutableItem], merge: DirectedExecutableItem) =
    froms.foreach(addEdge(_, merge))

  def addEdges(from: DirectedExecutableItem, forks: List[DirectedExecutableItem]) =
    forks.foreach(addEdge(from, _))

  override def execute(): Int = {
    checkForCycles()
    0
  }

  private def checkForCycles() = {

    val unprocessed = new mutable.HashSet[DirectedExecutableItem]()
    unprocessed ++= allExecutables

    val inBounds = mutable.Map(allExecutables.map(e => e -> e.getParents.size).toSeq: _*)

    var startables = new mutable.Queue[DirectedExecutableItem]()
    startables ++= inBounds.filter(_._2 == 0).keys

    while (startables.nonEmpty) {
      val job = startables.dequeue()
      unprocessed -= job
      for (child <- job.getChildren) {
        val decreased: Int = inBounds(child) - 1
        if (decreased == 0)
          startables.enqueue(child)
        inBounds(child) = decreased
      }
    }

    if (unprocessed.nonEmpty) {
      logger.error(s"Detected cycles in your graph definition around ${unprocessed.mkString("[", ",", "]")}")
      sys.exit(1)
    }
  }
}

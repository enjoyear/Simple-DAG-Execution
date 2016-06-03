package chen.guo.dagexe.execution

import java.util.concurrent.atomic.AtomicBoolean

import chen.guo.dagexe.util.FutureUtil
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.concurrent.TrieMap
import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.util.control.Breaks._

class DAGExecution(nodeDefMap: Map[String, ExecutableNode], graphDefMap: Map[String, List[String]]) {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def execute() = {
    checkForCycles(graphDefMap)

    val syncJobFutureMap: TrieMap[String, Future[Int]] = TrieMap[String, Future[Int]]()
    val (inBoundMap, outBoundMap) = getEdgeCountMap(graphDefMap)
    val syncInBoundMap: TrieMap[String, Int] = TrieMap[String, Int](inBoundMap.toArray: _*)
    val leaves = new mutable.HashSet[String]()
    leaves ++= outBoundMap.filter(_._2 == 0).keys
    logger.info(s"Graph leaves are: $leaves")

    val lock = this
    val anyWorkDone: AtomicBoolean = new AtomicBoolean(false)

    while (leaves.nonEmpty) {
      val startables: Iterable[String] = syncInBoundMap.filter(_._2 == 0).keys
      logger.info(s"""Startables are: $startables""")

      for (start <- startables) {
        breakable {
          val currentJob: String = start
          if (syncJobFutureMap.contains(currentJob)) {
            logger.warn(s"Trying to execute $currentJob for the second time. The job can only be initialized once.")
            break
          }

          if (leaves(currentJob))
            leaves -= currentJob

          syncJobFutureMap(currentJob) = FutureUtil.create(
            nodeDefMap(currentJob),
            Some(() => {
              logger.info(s"Removing $currentJob from waiting list before execution...")
              syncInBoundMap -= currentJob
            }),
            Some(() => {
              logger.info(s"Removing $currentJob dependency from graph...")
              for (dest <- graphDefMap(currentJob)) {
                syncInBoundMap(dest) = syncInBoundMap(dest) - 1
              }
              lock.synchronized {
                anyWorkDone.set(true)
                logger.info(s"Notifying because $currentJob finishes...")
                lock.notify()
              }
            }))
        }
      }

      synchronized {
        if (!anyWorkDone.get()) {
          logger.info("Waiting for some node to finish...")
          wait()
        }
        anyWorkDone.set(false)
      }
    }

    for (leaf <- leaves) {
      Await.ready(syncJobFutureMap(leaf), scala.concurrent.duration.Duration.Inf)
    }

    println("Graph finishes successfully.")
  }

  private def getEdgeCountMap(graphDefMap: Map[String, List[String]]): (mutable.HashMap[String, Int], mutable.HashMap[String, Int]) = {
    val inBounds = new mutable.HashMap[String, Int]()
    val outBounds = new mutable.HashMap[String, Int]()

    for (entry <- graphDefMap;
         src = entry._1;
         dest <- entry._2) {
      inBounds.getOrElseUpdate(src, 0)
      inBounds(dest) = inBounds.getOrElseUpdate(dest, 0) + 1

      outBounds.getOrElseUpdate(dest, 0)
      outBounds(src) = outBounds.getOrElseUpdate(src, 0) + 1
    }

    (inBounds, outBounds)
  }

  private def checkForCycles(graphDefMap: Map[String, List[String]]) = {

    val inBounds = new mutable.HashMap[String, Int]()

    for (entry <- graphDefMap;
         src = entry._1;
         dest <- entry._2) {
      inBounds.getOrElseUpdate(src, 0)
      inBounds(dest) = inBounds.getOrElseUpdate(dest, 0) + 1
    }

    var startables = new mutable.Queue[String]()
    val visited = new mutable.HashSet[String]()
    startables ++= inBounds.filter(_._2 == 0).keys

    while (startables.nonEmpty) {
      val job = startables.dequeue()
      visited += job
      for (dest <- graphDefMap(job)) {
        val decreased: Int = inBounds(dest) - 1
        if (decreased == 0)
          startables.enqueue(dest)
        inBounds(dest) = decreased
      }
    }

    if (visited.size < inBounds.size) {
      logger.error(s"Detected cycles in your graph definition around ${inBounds.keys.toList.diff(visited.toList).mkString("[", ",", "]")}")
      sys.exit(1)
    }
  }
}

package chen.guo.dagexe.execution

import java.util.Random
import java.util.concurrent.atomic.AtomicBoolean

import chen.guo.dagexe.config.ConfigUtil._
import chen.guo.dagexe.util.FutureUtil
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.concurrent.TrieMap
import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.util.control.Breaks._

object DAGExecution extends App {

  private val logger: Logger = LoggerFactory.getLogger(this.getClass)
  logger.info(s"Using node definition configuration file at ${args(0)}")
  logger.info(s"Using graph definition configuration file at ${args(1)}")

  private val nodeDefMap: Map[String, ExecutableNode] = getNodeDefConfig(args(0))
  private val graphDefMap: Map[String, List[String]] = getGraphDefConfig(args(1))
  checkForCycles(graphDefMap)

  private val syncJobFutureMap: TrieMap[String, Future[Int]] = TrieMap[String, Future[Int]]()
  private val (inBoundMap, outBoundMap) = getEdgeCountMap(graphDefMap)
  private val syncInBoundMap: TrieMap[String, Int] = TrieMap[String, Int](inBoundMap.toArray: _*)
  private val leaves = new mutable.HashSet[String]()
  leaves ++= outBoundMap.filter(_._2 == 0).keys
  logger.info(s"Graph leaves are: $leaves")

  val obj = this
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

        val executable = nodeDefMap(currentJob)

        syncJobFutureMap(currentJob) = FutureUtil.create(
          //executable,
          SleepNode(currentJob, 100 * (new Random().nextInt(7) + 1)),
          Some(() => {
            logger.info(s"Removing $currentJob from waiting list before execution...")
            syncInBoundMap -= currentJob
          }),
          Some(() => {
            logger.info(s"Removing $currentJob dependency from graph...")
            for (dest <- graphDefMap(currentJob)) {
              syncInBoundMap(dest) = syncInBoundMap(dest) - 1
            }
            obj.synchronized {
              anyWorkDone.set(true)
              logger.info(s"Notifying because $currentJob finishes...")
              notify()
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

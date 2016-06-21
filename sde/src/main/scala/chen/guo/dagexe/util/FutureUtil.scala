package chen.guo.dagexe.util

import chen.guo.dagexe.config.ExecutableNode
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object FutureUtil {

  private val logger: Logger = LoggerFactory.getLogger(FutureUtil.getClass)

  def create(en: ExecutableNode,
             beforeExecution: Option[() => Unit] = None,
             afterSuccessfulExecution: Option[() => Unit] = None
            ): Future[Int] = {
    val f = Future {
      runExecutable(en, beforeExecution, afterSuccessfulExecution)
    }
    addDefaultCallbacks(f, en)
    f
  }

  def concatenate(en: ExecutableNode, dependency: Future[Int],
                  beforeExecution: Option[() => Unit] = None,
                  afterSuccessfulExecution: Option[() => Unit] = None
                 ): Future[Int] = {
    val concatenated: Future[Int] = dependency.map {
      case 0 => runExecutable(en, beforeExecution, afterSuccessfulExecution)
      case _ =>
        logger.error(MessageBuilder.build(
          "Dependency Failed",
          s"Dependency for $en failed."))
        sys.exit(1)
    }
    addDefaultCallbacks(concatenated, en)
    concatenated
  }

  def fork(forkEns: List[ExecutableNode], dependency: Future[Int],
           beforeExecution: Option[() => Unit] = None,
           afterSuccessfulExecution: Option[() => Unit] = None
          ): List[Future[Int]] = {
    forkEns.map(s => concatenate(s, dependency, beforeExecution, afterSuccessfulExecution))
  }

  def merge(en: ExecutableNode, dependencies: List[Future[Int]],
            beforeExecution: Option[() => Unit] = None,
            afterSuccessfulExecution: Option[() => Unit] = None
           ): Future[Int] = {
    val dependenciesSeq: Future[List[Int]] = Future.sequence(dependencies)
    val merged: Future[Int] = dependenciesSeq.map {
      case returns if returns.forall(_ == 0) => runExecutable(en, beforeExecution, afterSuccessfulExecution)
      case _ =>
        logger.error(MessageBuilder.build(
          "One of the dependencies failed",
          s"One dependency for $en failed"))
        sys.exit(1)
    }
    addDefaultCallbacks(merged, en)
    merged
  }

  def mergeFork(forkEns: List[ExecutableNode], dependencies: List[Future[Int]],
                beforeExecution: Option[() => Unit] = None,
                afterSuccessfulExecution: Option[() => Unit] = None
               ): List[Future[Int]] = {
    forkEns.map(s => merge(s, dependencies, beforeExecution, afterSuccessfulExecution))
  }

  private def runExecutable(executable: ExecutableNode, beforeExecution: Option[() => Unit], afterSuccessfulExecution: Option[() => Unit]): Int = {
    beforeExecution match {
      case Some(runnable) =>
        logger.info(s"Executing preparation step for $executable")
        runnable()
      case None =>
    }

    val ret = executable.execute()
    logger.info(s"$executable finished with code $ret")

    if (ret == 0) {
      afterSuccessfulExecution match {
        case Some(runnable) =>
          logger.info(s"Executing post-success step for $executable")
          runnable()
        case None =>
      }
    }
    ret
  }

  private def addDefaultCallbacks(f: Future[Int], executable: ExecutableNode): Unit = {
    //served as side effects
    f.onSuccess {
      case 0 =>
      case x =>
        logger.error(MessageBuilder.build(
          s"ExecutableNode finished with non-zero",
          s"$executable finished with exit code $x"))
        sys.exit(1)
    }

    f.onFailure {
      case exception: Throwable =>
        logger.error(MessageBuilder.build(
          "ExecutableNode Execution Failed",
          s"""
             |$executable execution failed.
             |Error Message:
             |${exception.getMessage}
             |""".stripMargin))
        sys.exit(1)
    }
  }
}

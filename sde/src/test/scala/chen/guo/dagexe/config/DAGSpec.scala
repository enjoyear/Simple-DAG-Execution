package chen.guo.dagexe.config

import chen.guo.test.common.UnitSpec
import com.typesafe.config.ConfigFactory

class DAGSpec extends UnitSpec {
  "dag" should "give error for cycled graph" in {
    val dag = new DAG()
    val n1 = SleepNode("n1", "100")
    val n2 = SleepNode("n2", "100")
    dag.addEdge(n1, n2)
    dag.addEdge(n2, n1)
    dag.execute()
  }
}

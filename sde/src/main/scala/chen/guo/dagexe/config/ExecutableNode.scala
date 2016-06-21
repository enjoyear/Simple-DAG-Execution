package chen.guo.dagexe.config

trait ExecutableNode {
  def execute(): Int
}

trait ExecutableGraphNode extends ExecutableNode {
  def getDependencies: List[ExecutableGraphNode]

  def getChildren: List[ExecutableGraphNode]
}


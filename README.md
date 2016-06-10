# Simple-DAG-Execution
This project provide a light weighted way to create and execute a statically defined DAG.

## How to setup
Define SDE_HOME in the environment variables. For example, add the following to your .bashrc/.bash_profile file:
```shell
    export SDE_HOME=/where/you/check/out/this/project/Simple-DAG-Execution
```

Create uber jar
```shell
    sbt clean assembly
```
### Development
Tests have been disabled for assembly. Specifically call running tests when you need. 
```shell
    sbt test        # run all unit tests
    sbt it:test     # run all integration tests
```

## How to execute

See jar usage
```shell
    java -cp $SDE_HOME/sde/target/scala-2.11/sde-assembly-1.0.jar chen.guo.dagexe.execution.Main -h
```

A simple example
```shell
    java -cp $SDE_HOME/sde/target/scala-2.11/sde-assembly-1.0.jar chen.guo.dagexe.execution.Main \
    --graph $SDE_HOME/examples/example1/conf/GraphDef.conf \
    --nodes $SDE_HOME/examples/example1/conf/NodeDef.conf
```


## ToDo
### Features
-1. Avoid the configuration file. Implement a graph builder and pass the class to the executor.
0. Allow variable declaration in conf file.
1. Make start and end of execution configurable.
2. Make graph node executable depending different exit codes of parents.
3. Add UI.
4. Don't need to write leaf nodes in the configuration file.

### Improvements
1. Add manifest file.
2. Add parent and children fields to ExecutableNode.


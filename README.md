# Simple-DAG-Execution
This project provide a light weighted way to create and execute a statically defined DAG.

## How to setup
Define SDE_HOME in the environment variables. For example, add the following to your .bashrc/.bash_profile file:
```shell
    export SDE_HOME=/where/you/check/out/this/project/Simple-DAG-Execution
```
Run test to make sure it works.



## Feature to be done.
0. Allow variable declaration in conf file.
1. Make start and end of execution configurable.
2. Make graph node executable depending different exit codes of parents.
3. Add UI.
4. Don't need to write leaf nodes in the configuration file.

## Code to be improved.
1. Add help for CLI
2. Add parent and children fields to ExecutableNode.
3. Add test to ScriptNode.

### Useful commands

#### Create dependency graph

Requires `graphviz` to be installed.

`mvn com.github.ferstl:depgraph-maven-plugin:aggregate -DcreateImage=true -DreduceEdges=false -Dscope=compile "-Dincludes=ovh.migmolrod.food-ordering-system*:*"`

It will create a .dot file and a .png file in root `target` folder.

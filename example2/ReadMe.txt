The sample program in the src directory demonstrates how to use the Cascading extensions for Accumulo to perform table operations.

The following are commands to run the sample program.

a) Run the program with parameter createTable
====================================================

hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://authors?instance=indra&user=root&password=xxxx&zookeepers=cdh-dn01:2181" "createTable"



b) Run the program with parameter createTableWithSplits
========================================================

hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://bookshelf?instance=indra&user=root&password=xxxx&zookeepers=cdh-dn01:2181" "createTableWithSplits" "A,G,X"
….

c) Run the program with parameter checkIfTableExists
====================================================

hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://bookshelf?instance=indra&user=root&password=xxxx&zookeepers=cdh-dn01:2181" "checkIfTableExists"
….


d) Run the program with parameter flush key range
====================================================

hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://bookshelf?instance=indra&user=root&password=xxxx&zookeepers=cdh-dn01:2181" "flushKeyRange"  "b001" "b003"

e) Run the program with parameter flush
====================================================

hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://bookshelf?instance=indra&user=root&password=xxxx&zookeepers=cdh-dn01:2181" "flush"

f) Run the program with parameter deleteTable
====================================================

hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://bookshelf?instance=indra&user=root&password=xxxx&zookeepers=cdh-dn01:2181" "deleteTable" 



The sample program in the src directory demonstrates how to use the Cascading extensions for Accumulo to perform table operations.

It covers the following operations:
1.  Create table 
[parameters: Accumulo connection string, "createTable"]
2.  Create table with splits
[parameters: Accumulo connection string, "createTableWithSplits",CSVListOfSplits]
3.  Check if table exists
[parameters: Accumulo connection string, "checkIfTableExists"]
4.  Flush specific key range
[parameters: Accumulo connection string, "flushKeyRange", rangeStartRowKey, rangeEndRowKey]
5.  Flush
[parameters: Accumulo connection string, "flush"]
6.  Delete table
[parameters: Accumulo connection string, "deleteTable"]

Construct of Accumulo connection string parameter:
accumulo://table1?instance=myinstance&user=root&password=secret&zookeepers=CSVListofZooserver:portNum&auths=PRIVATE,PUBLIC&write_threads=3
[Modeled after construct in Accumulo-Pig]
Required:
table, instance, user, password, zookeepers=CSVListofZooserver:portNum

The following are details to run the sample program.

a) Run the program with parameter createTable
====================================================

hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://authors?instance=indra&user=root&password=xxxx&zookeepers=cdh-dn01:2181" "createTable"

b) Run the program with parameter createTableWithSplits
========================================================

hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://bookshelf?instance=indra&user=root&password=xxxx&zookeepers=cdh-dn01:2181" "createTableWithSplits" "A,G,X"

c) Run the program with parameter checkIfTableExists
====================================================

hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://bookshelf?instance=indra&user=root&password=xxxx&zookeepers=cdh-dn01:2181" "checkIfTableExists"

d) Run the program with parameter flush key range
====================================================

hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://bookshelf?instance=indra&user=root&password=xxxx&zookeepers=cdh-dn01:2181" "flushKeyRange"  "b001" "b003"

e) Run the program with parameter flush
====================================================

hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://bookshelf?instance=indra&user=root&password=xxxx&zookeepers=cdh-dn01:2181" "flush"

f) Run the program with parameter deleteTable
====================================================

hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://bookshelf?instance=indra&user=root&password=xxxx&zookeepers=cdh-dn01:2181" "deleteTable" 



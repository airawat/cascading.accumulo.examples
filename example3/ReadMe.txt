The sample program in the “src” directory demonstrates how to dump (all the) data in Accumulo to HDFS using the Cascading extensions for Accumulo.  The program output is exactly as the data is laid out in Accumulo.  The next example transposes the data before writing to HDFS.

Program parameters:
============================================
Parameter 1 = Accumulo connection string
Parameter 2 = Accumulo query criteria
Parameter 3 = HDFS path for Accumulo dump
Parameter 4 = Number of reducers

Construct of Accumulo connection string:
============================================
accumulo://table1?instance=myinstance&user=root&password=secret&zookeepers=CSVListofZooserver:portNum&auths=PRIVATE,PUBLIC&write_threads=3
[Modeled after construct in Accumulo-Pig]

Required:
table, instance, user, password, zookeepers=CSVListofZooserver:portNum


Construct of query critera:
============================================
columns=colFam1|cq1,colFam1|cq2&rowKeyRangeStart=X0001&rowKeyRangeEnd=X0005&rowRegex=*&columnFamilyRegex=&columnQualifierRegex=*&valueRegex=*

Required elements:
rowkey range and/or rowRegex


Run the program
============================================
E.g.
hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0.jar "accumulo://221BakerStreet?instance=indra&user=root&password=xxxx&zookeepers=cdh-dn01:2181" "rowRegex=.*" "cascading.accumulo.examples/output-221BakerStreet-HDFSDump" "1"


Verify
============================================
The following is the output from the sample run of the program..

a) Check for the output file

hadoop fs -cat cascading.accumulo.examples/output-221BakerStreet-HDFSDump/part-00000

b001	Book	Name		1393007668782	A study in scarlet
b001	Book	Published		1393007668959	1887
b002	Book	Name		1393007669025	A sign of four
b002	Book	Published		1393007669050	1890
b003	Book	Name		1393007669083	The adventures of Sherlock Holmes
b003	Book	Published		1393007669106	1892
b004	Book	Name		1393007669148	The memoirs of Sherlock Holmes
b004	Book	Published		1393007669179	1893
b005	Book	Name		1393007669228	The hounds of Baskerville
b005	Book	Published		1393007669258	1901
b006	Book	Name		1393007669300	The return of Sherlock Holmes
b006	Book	Published		1393007669337	1905

b) Get record count in Accumulo

From Linux command line, run the command-
 ./bin/accumulo shell -u root -p xxxx -e "scan -np -t 221BakerStreet" | wc -l
13
(ignore the one extra record, internal to Accumulo)

c) Get record count in HDFS

hadoop fs -cat cascading.accumulo.examples/output-221BakerStreet-HDFSDump/part-00000 | wc -l
12


An alternate program run - that specifies column family/column qualifier
=========================================================================
hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://221BakerStreet?instance=indra&user=root&password=xxxxx&zookeepers=cdh-dn01:2181" "rowRegex=.*&columns=Book|Published" "cascading.accumulo.examples/output-221BakerStreetDump" "1"

hadoop fs -cat cascading.accumulo.examples/output-221BakerStreetDump/part*
b001	Book	Published		1393007668959	1887
b002	Book	Published		1393007669050	1890
b003	Book	Published		1393007669106	1892
b004	Book	Published		1393007669179	1893
b005	Book	Published		1393007669258	1901
b006	Book	Published		1393007669337	1905



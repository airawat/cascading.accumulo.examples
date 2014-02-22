The sample program in the “src” directory demonstrates how to export data in Accumulo to HDFS, in a flat format, with column headers, using the Cascading extensions for Accumulo.  

The program accepts -
query criteria to limit rows, column families and column qualifiers (parameter 2),
a list of fields that can be used to control ordering of columns in output (parameter 3), 
field delimiter,
flag whether to include column header..
and transposes the data in Accumulo to a flat format.

Program parameters:
============================================
Parameter 1 = Accumulo connection string (see construct below)
Parameter 2 = Accumulo query criteria (see construct below)                  
Parameter 3 = Output fields declarator (should match column qualifier name exactly) - this will also serve as column header
Parameter 4 = Output field delimiter
Parameter 5 = Output display headers (TRUE/FALSE)
Parameter 6 = HDFS path for program output
Parameter 7 = Number of reducers
Parameter 8 = HDFS path for failures

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
hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://221BakerStreet?instance=indra&user=root&password=xxxxx&zookeepers=cdh-dn01:2181" "rowRegex=.*" "Book_ID,Name,Published" "," "TRUE" "cascading.accumulo.examples/output-AccumuloExport" "1" "cascading.accumulo.examples/Trap-AccumuloExport"

Results
============================================
hadoop fs -cat cascading.accumulo.examples/output-AccumuloExport/part*

Book_ID,Name,Published
b002,A sign of four,1890
b004,The memoirs of Sherlock Holmes,1893
b006,The return of Sherlock Holmes,1905
b001,A study in scarlet,1887
b003,The adventures of Sherlock Holmes,1892
b005,The hounds of Baskerville,1901
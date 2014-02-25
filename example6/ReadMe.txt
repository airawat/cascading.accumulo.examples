The sample program in the “src” directory demonstrates how to read data in Accumulo and write it back to Accumulo using the Cascading extensions for Accumulo.  

This example builds on example 5.

Program parameters
===================
Parameter 1 = Accumulo connection string for input source table (see construct below)
Parameter 2 = Accumulo query criteria for input source(see construct below) 
Parameter 3 = Accumulo connection string for destination tale (see construct, below) 
Parameter 4 = HDFS path for failures

Construct of Accumulo connection string:
============================================
accumulo://table1?instance=myinstance&user=root&password=secret&zookeepers=CSVListofZooserver:portNum[&auths=PRIVATE,PUBLIC&write_threads=3]
[Modeled after construct in Accumulo-Pig]
Required:
table, instance, user, password, zookeepers=CSVListofZooserver:portNum

Construct of query criteria parameter:
============================================
columns=colFam1|cq1,colFam1|cq2&rowKeyRangeStart=X0001&rowKeyRangeEnd=X0005&rowRegex=*&columnFamilyRegex=&columnQualifierRegex=*&valueRegex=*
Required:(rowKeyRangeStart, rowKeyRangeEnd=X0005) and/or rowRegex


Execute sample program:
============================================

a) Create table in Accumulo
createtable Books_Copy


b) Run the program
E.g.
hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://Books?instance=indra&user=root&password=xxxxx&zookeepers=cdh-dn01:2181&auths=Public" "rowRegex=.*"  "accumulo://Books_Copy?instance=indra&user=root&password=xxxxx&zookeepers=cdh-dn01:2181&write_threads=2" "cascading.accumulo.examples/output-MoveDataAcrossAccumuloTables"

c) Verify
root@indra Books_Copy> setauths -s Public
root@indra Books_Copy> scan -np

root@indra Books_Copy> scan
CDoyleBook.b001 Book:Book_ID [Public]    b001
CDoyleBook.b001 Book:Name [Public]    A study in scarlet
CDoyleBook.b001 Book:Published [Public]    1887
CDoyleBook.b002 Book:Book_ID [Public]    b002
CDoyleBook.b002 Book:Name [Public]    A sign of four
CDoyleBook.b002 Book:Published [Public]    1890
CDoyleBook.b003 Book:Book_ID [Public]    b003
CDoyleBook.b003 Book:Name [Public]    The adventures of Sherlock Holmes
CDoyleBook.b003 Book:Published [Public]    1892
CDoyleBook.b004 Book:Book_ID [Public]    b004
CDoyleBook.b004 Book:Name [Public]    The memoirs of Sherlock Holmes
CDoyleBook.b004 Book:Published [Public]    1893
CDoyleBook.b005 Book:Book_ID [Public]    b005
CDoyleBook.b005 Book:Name [Public]    The hounds of Baskerville
CDoyleBook.b005 Book:Published [Public]    1901
CDoyleBook.b006 Book:Book_ID [Public]    b006
CDoyleBook.b006 Book:Name [Public]    The return of Sherlock Holmes
CDoyleBook.b006 Book:Published [Public]    1905



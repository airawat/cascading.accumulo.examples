The sample program (very basic) in the “src” directory demonstrates how to read data in a flat delimited file with a header, in HDFS, transpose it, and save to Accumulo.

The data used is in this program is from example 4.

Program parameters:
============================================
Parameter 1 = File in HDFS that is the data source
Parameter 2 = Flag to indicate whether the file has a header; Accepted values are Y, N.               
Parameter 3 = Field delimiter of input fileParameter 4 = Output field delimiter
Parameter 4 = CSV list of fields to describe the input file
Parameter 5 = Fields to discard from parameter 4
Parameter 6 = Fields to retain from parameter 4
Parameter 7 = Deyails on how to construct row key (see details below)
Parameter 8 = Column family details (see construct, below)
Parameter 9 = Column visibility details (see construct, below)
Parameter 10 = Accumulo connection string (see construct, below)
Parameter 11 = HDFS path for failures

Construct of row key related parameter:
============================================
FIELDS_CSV=<CSV list of fields in order they need to appear in row key>&DELIMITER=<delimiter for fields in row key>&PREFIX=<any literal string that needs to be included in the row key as prefix>&SUFFIX=<any literal string that needs to be included in the row key as suffix>

E.g. 
FIELDS-CSV=Book_ID&DELIMITER=.&PREFIX=CDoyleBooks
Lets say Book_ID=b001, the row key will be CDoyleBooks.b001.

Construct of column family related parameter:
============================================
<ColumnQualifier1>=<ColumnFamilyName>&<ColumnQualifier2>=<ColumnFamilyName>

E.g.
"Book_ID=Book&Name=Book&Published=Book"
In this example, the fields/column qualifiers Book_ID, Name and Published will belong to column family, "Book".

Construct of column visibility related parameter:
============================================
<ColumnQualifier1>=<ColumnVisibility>&<ColumnQualifier2>=<ColumnVisibility>

E.g.
"Book_ID=Public&Name=Public&Published=Book"
In this example, the fields/column qualifiers Book_ID, Name and Published will be visible for user authorized to view records marked for "Public" visibility.

In order to view the records inserted by the program, you need to run the following command in Accumulo on the table -
setauths -s Public

Construct of Accumulo connection string:
============================================
accumulo://table1?instance=myinstance&user=root&password=secret&zookeepers=CSVListofZooserver:portNum[&auths=PRIVATE,PUBLIC&write_threads=3]
[Modeled after construct in Accumulo-Pig]
Required:
table, instance, user, password, zookeepers=CSVListofZooserver:portNum

Input data format
=================================================
Book_ID,Name,Published
b002,A sign of four,1890
b004,The memoirs of Sherlock Holmes,1893
b006,The return of Sherlock Holmes,1905

Input fields & output fields
=================================================
Input=["Book_ID","Name","Published"]
Output=["rowID", "colF", "colQ", "colVis", "colTimestamp", "colVal”]

Defaulting the column visibility to "Public"
		
Create table in Accumulo
=================================================

root@indra> createtable Books
root@indra Books> setauths -s Public


Command to run the program
=================================================
E.g.
hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "cascading.accumulo.examples/output-AccumuloExport/part*"  "Y" "," "Book_ID,Name,Published" "" "Book_ID,Name,Published" "FIELDS-CSV=Book_ID&DELIMITER=.&PREFIX=CDoyleBooks" "Book_ID=Book&Name=Book&Published=Book" "Book_ID=Public&Name=Public&Published=Public" "accumulo://Books?instance=indra&user=root&password=xxxxx&zookeepers=cdh-dn01:2181&write_threads=2" "cascading.accumulo.examples/trap-AccumuloImport"

Results in Accumuo
=================================================
root@indra Books> scan 
              
CDoyleBooks.b001 Book:Book_ID [Public]    b001
CDoyleBooks.b001 Book:Name [Public]    A study in scarlet
CDoyleBooks.b001 Book:Published [Public]    1887
CDoyleBooks.b002 Book:Book_ID [Public]    b002
CDoyleBooks.b002 Book:Name [Public]    A sign of four
CDoyleBooks.b002 Book:Published [Public]    1890
CDoyleBooks.b003 Book:Book_ID [Public]    b003
CDoyleBooks.b003 Book:Name [Public]    The adventures of Sherlock Holmes
CDoyleBooks.b003 Book:Published [Public]    1892
CDoyleBooks.b004 Book:Book_ID [Public]    b004
CDoyleBooks.b004 Book:Name [Public]    The memoirs of Sherlock Holmes
CDoyleBooks.b004 Book:Published [Public]    1893
CDoyleBooks.b005 Book:Book_ID [Public]    b005
CDoyleBooks.b005 Book:Name [Public]    The hounds of Baskerville
CDoyleBooks.b005 Book:Published [Public]    1901
CDoyleBooks.b006 Book:Book_ID [Public]    b006
CDoyleBooks.b006 Book:Name [Public]    The return of Sherlock Holmes
CDoyleBooks.b006 Book:Published [Public]    1905

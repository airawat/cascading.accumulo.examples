The sample program in the “src” directory demonstrates how to read data in Accumulo and write it back to Accumulo using the Cascading extensions for Accumulo.  

This example builds on example 6.

Program parameters
===================
Parameter 1 = Accumulo connection string for input source table (see construct below)
Parameter 2 = Accumulo query criteria for input source(see construct below) 
Parameter 3 = CSV list of output fields
Parameter 4 = Mysql connection string for destination tale (see construct, below)
Parameter 5 = Number of reducers 
Parameter 6 = HDFS path for failures

Construct of Accumulo connection string:
============================================
accumulo://table1?instance=myinstance&user=root&password=secret&zookeepers=CSVListofZooserver:portNum[&auths=PRIVATE,PUBLIC&write_threads=3]
[Modeled after construct in Accumulo-Pig]
Required:
table, instance, user, password, zookeepers=CSVListofZooserver:portNum
See example..below

Construct of query criteria parameter:
============================================
columns=colFam1|cq1,colFam1|cq2&rowKeyRangeStart=X0001&rowKeyRangeEnd=X0005&rowRegex=*&columnFamilyRegex=&columnQualifierRegex=*&valueRegex=*
Required:(rowKeyRangeStart, rowKeyRangeEnd=X0005) and/or rowRegex
See example..below

Construct of Mysql connection string:
============================================
"jdbc:mysql://<mysqlServer>:<portNum/<database>?user=<UID>&password=<PWD>&autoReconnect=true&removeAbandonedTimeout=<timeout>&tableName=<table>&colNames=<CSVListOfColumns>&colDefinition=CSVListOfColumnDataTypesWithLengths&primaryKey=<primarykey>";
		
See example..below


Execute sample program:
============================================

a) Create table in Mysql

mysql> 
create database talk3;

use talk3;

create table Books
(
Book_ID varchar(25),
Name varchar(100),
Published varchar(4)
);

alter table Books add primary key(Book_ID);

grant all on talk3.* to 'devUser'@'%';



b) Run the program
E.g.
hadoop jar cascading.accumulo.examples/jars/cascading.accumulo.examples-1.0-jar-with-dependencies.jar "accumulo://Books?instance=indra&user=root&password=xxxx&zookeepers=cdh-dn01:2181&auths=Public" "rowRegex=.*" "Book_ID,Name,Published" "jdbc:mysql://cdh-dev01:3306/talk3?user=devUser&password=xxxx&autoReconnect=true&removeAbandonedTimeout=60&tableName=Books&colNames=Book_ID,Name,Published&colDefinition=Varchar(25),Varchar(100),Varchar(4)&primaryKey=Book_ID" 2 "cascading.accumulo.examples/outputTrap-AccumuloExportToMySql"

c) Verify in mysql

mysql> select * from Books;
+-----------------+-----------------------------------+-----------+
| Book_ID         | Name                              | Published |
+-----------------+-----------------------------------+-----------+
| CDoyleBook.b002 | A sign of four                    | 1890      |
| CDoyleBook.b004 | The memoirs of Sherlock Holmes    | 1893      |
| CDoyleBook.b006 | The return of Sherlock Holmes     | 1905      |
| CDoyleBook.b001 | A study in scarlet                | 1887      |
| CDoyleBook.b003 | The adventures of Sherlock Holmes | 1892      |
| CDoyleBook.b005 | The hounds of Baskerville         | 1901      |
+-----------------+-----------------------------------+-----------+
6 rows in set (0.05 sec)


package com.talk3.cascading.accumulo.examples;

import cascading.flow.Flow;
import cascading.flow.FlowDef;
import cascading.flow.hadoop.HadoopFlowConnector;
import cascading.jdbc.JDBCScheme;
import cascading.jdbc.JDBCTap;
import cascading.jdbc.TableDesc;
import cascading.operation.Identity;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.scheme.hadoop.TextLine;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;
import com.talk3.cascading.accumulo.AccumuloScheme;
import com.talk3.cascading.accumulo.AccumuloTap;
import java.util.Properties;
import org.apache.hadoop.mapred.JobConf;

public class AccumuloExportToMysql {

    public static void main(String[] args) throws Exception {

        if (args.length < 6) {
            throw new Exception("Insufficient parameters!  "
                    + "(1)Input source Accumulo connection string, "
                    + "(2)Input source query criteria, "
                    + "(3)output fields declarator, "
                    + "(4)output sink Mysql connection string, "
                    + "(5)number of reducers, "
                    + "(6)HDFS path for failures "
                    + "are the required parameters!");
        }

        // {{
        // ARGUMENTS
        String accumuloConnectionString = args[0].toString();
        String accumuloQueryCriteria = args[1].toString();
        String outputSinkFieldsDeclaratorCSV = args[2].toString();
        String mySqlConnectionString = args[3].toString();
        int numReducers = Integer.parseInt(args[4].toString());
        String trapHDFSPath = args[5].toString();
        
        System.out.println("accumuloConnectionString=" + accumuloConnectionString);
	// }}

        // {{
        //Output field declarator (Can be used to control field placement/order in output)
        Fields outputSinkFieldsDeclarator = new Fields();
        if (outputSinkFieldsDeclaratorCSV.length() > 0) {
            outputSinkFieldsDeclarator = outputSinkFieldsDeclarator.append(Util.buildFieldList(outputSinkFieldsDeclaratorCSV));
        }
        // }}

        // {{
        // JOB 
        JobConf jobConf = new JobConf();
        jobConf.setJarByClass(AccumuloExportToMysql.class);

        Properties properties = AppProps.appProps()
                .setName("AccumuloExportToMysql").setVersion("1.0.0")
                .buildProperties(jobConf);
        // }}

        // {{
        // SOURCE tap - Accumulo
        AccumuloTap sourceTapAccumulo = new AccumuloTap(accumuloConnectionString,
                new AccumuloScheme(accumuloQueryCriteria), SinkMode.REPLACE);
        // }}

        // {{
        // SINK tap - Mysql
        // mysql related
        String mysqldbDriver = "com.mysql.jdbc.Driver";
        String mysqldbTblExistsQuery = "";

        String mysqldbUIDSink = Util
                .getValueFromMysqlConnectionString(mySqlConnectionString, "user");
        String mysqldbPwdSink = Util
                .getValueFromMysqlConnectionString(mySqlConnectionString,
                        "password");
        String mysqldbTableNameSink = Util
                .getValueFromMysqlConnectionString(mySqlConnectionString,
                        "tableName");

        String[] mysqldbColumnNamesSink = Util
                .getValueArrayFromMysqlConnectionString(mySqlConnectionString,
                        "colNames");
        

        String[] mysqldbColumnDefinitionsSink = Util
                .getValueArrayFromMysqlConnectionString(mySqlConnectionString,
                        "colDefinition");
        String[] mysqldbPrimaryKeysSink = Util
                .getValueArrayFromMysqlConnectionString(mySqlConnectionString,
                        "primaryKey");

        TableDesc dbTableDescOutput = new TableDesc(mysqldbTableNameSink,
                mysqldbColumnNamesSink, mysqldbColumnDefinitionsSink,
                mysqldbPrimaryKeysSink, mysqldbTblExistsQuery);

        JDBCScheme mysqldbJDBCScheme = new JDBCScheme(
                outputSinkFieldsDeclarator, mysqldbColumnNamesSink);
        mysqldbJDBCScheme.setNumSinkParts(numReducers);

        String jdbcConnectionStringForSink = Util
                .getMySqlConnectionString(mySqlConnectionString);

        JDBCTap sinkTapMysql = new JDBCTap(jdbcConnectionStringForSink,
                mysqldbUIDSink, mysqldbPwdSink, mysqldbDriver,
                dbTableDescOutput, new JDBCScheme(
                        outputSinkFieldsDeclarator, mysqldbColumnNamesSink));
        // }}

        // {{
        // TRAP tap - HDFS
        Tap sinkTrapTapHDFS = new Hfs(new TextLine(), trapHDFSPath,
                SinkMode.REPLACE);
	// }}

        // {{
        // PIPE
        Pipe rawDataPipe = new Pipe("Input");
        rawDataPipe = new Each(rawDataPipe, new Identity(sourceTapAccumulo.getDefaultAccumuloFields()));

        Pipe transformPipe = new GroupBy(rawDataPipe, new Fields("rowID"));
        transformPipe = new Every(
                transformPipe,
                Fields.ALL,
                new TransposeFromAccumuloLayoutFunction(outputSinkFieldsDeclarator),
                Fields.RESULTS);

        transformPipe = new Each(transformPipe, new Identity(
                outputSinkFieldsDeclarator));
	// }}

        // {{
        // EXECUTE
        // Connect the taps, pipes, etc., into a flow & execute
        HadoopFlowConnector flowConnector = new HadoopFlowConnector(properties);

        FlowDef flowDef = FlowDef
                .flowDef()
                .setName("AccumuloExportToMysql-flow-def")
                .addSource(rawDataPipe, sourceTapAccumulo)
                .addTrap(rawDataPipe, sinkTrapTapHDFS)
                .addTailSink(transformPipe, sinkTapMysql);
        Flow flow = flowConnector.connect(flowDef);
        flow.complete();

        // }}
    }

}

package com.talk3.cascading.accumulo.examples;

import cascading.flow.Flow;
import cascading.flow.hadoop.HadoopFlowConnector;
import cascading.operation.Identity;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.scheme.hadoop.TextLine;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import com.talk3.cascading.accumulo.AccumuloScheme;
import com.talk3.cascading.accumulo.AccumuloTap;
import java.util.Properties;
import org.apache.hadoop.mapred.JobConf;

public class MoveDataAcrossAccumuloTables {
    public static void main(String[] args) throws Exception {

        // {{
        // Basic validation
        if (args.length < 4) {
            throw new Exception("Insufficient parameters!  "
                    + "(1)Accumulo source table connection string, "
                    + "(2)Accumulo source table query criteria, "
                    + "(3)output Accumulo connection string, and "
                    + "(4)HDFS path for failures "
                    + "are the required parameters!");
        }
        // }} 
        
        // {{
        // ARGS
        String inputSourceAccumuloConnectionString = args[0].toString();
        String inputSourceAccumuloQueryCriteria = args[1].toString();
        String outputSinkAccumuloConnectionString = args[2].toString();
        String trapHDFSPath = args[3].toString();
        // }}
    
        
        // {{
        // JOB 
        JobConf jobConf = new JobConf();
        jobConf.setJarByClass(AccumuloExport.class);

        Properties properties = AppProps.appProps()
                .setName("MoveDataAcrossAccumuloTables").setVersion("1.0.0")
                .buildProperties(jobConf);
        // }}
        
         // {{
        // SOURCE tap - Accumulo
        AccumuloTap sourceTapAccumulo = new AccumuloTap(inputSourceAccumuloConnectionString,
                new AccumuloScheme(inputSourceAccumuloQueryCriteria), SinkMode.REPLACE);
        // }}
        
        // {{
        // SINK tap - Accumulo
        AccumuloTap sinkTapAccumulo = new AccumuloTap(outputSinkAccumuloConnectionString,
                new AccumuloScheme(), SinkMode.UPDATE);
	// }}

        // {{
        // TRAP tap - HDFS
        Tap sinkTrapTapHDFS = new Hfs(new TextLine(), trapHDFSPath,
                SinkMode.REPLACE);
	// }}

        // {{
        // PIPE
        Pipe readPipe = new Pipe("readPipe");
        readPipe = new Each(readPipe, new Identity(
                sourceTapAccumulo.getDefaultAccumuloFields()));
	// }}

        // {{
        // EXECUTE
        // Connect the taps, pipes, etc., into a flow & execute
        Flow flow = new HadoopFlowConnector(properties).connect("MoveDataAcrossAccumuloTables",
                sourceTapAccumulo, sinkTapAccumulo, sinkTrapTapHDFS,readPipe);
        flow.complete();
        // }}

    }
}

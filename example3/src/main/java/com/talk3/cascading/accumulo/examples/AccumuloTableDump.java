package com.talk3.cascading.accumulo.examples;

import java.util.Properties;

import org.apache.hadoop.mapred.JobConf;
import cascading.flow.Flow;
import cascading.operation.Identity;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.flow.hadoop.HadoopFlowConnector;
import cascading.flow.hadoop.HadoopFlowProcess;
import cascading.scheme.Scheme;
import cascading.tap.hadoop.Hfs;
import cascading.scheme.hadoop.TextLine;
import com.talk3.cascading.accumulo.AccumuloScheme;
import com.talk3.cascading.accumulo.AccumuloTap;

public class AccumuloTableDump {

    public static void main(String[] args) throws Exception {

        if(args.length<4)
        {
            throw new Exception("Insufficient arguments!  (1)Connection string, (2)query criteria, (3)output HDFS path and (4)number of reducers are required parameters!");       
        }
	// {{
        // ARGUMENTS
        String accumuloConnectionString = args[0].toString();
        String accumuloQueryCriteria = args[1].toString();
        String outputHDFSPath = args[2].toString();
        int numReducers=Integer.parseInt(args[3].toString());
	// }}

	// {{
        // JOB 
        JobConf jobConf = new JobConf();
        jobConf.setJarByClass(AccumuloTableDump.class);

        Properties properties = AppProps.appProps()
                .setName("AccumuloTableDump").setVersion("1.0.0")
                .buildProperties(jobConf);

	// }}
		
        // {{
        // SOURCE tap - Accumulo
        HadoopFlowProcess hfp = new HadoopFlowProcess(jobConf);
        AccumuloTap sourceTapAccumulo = new AccumuloTap(accumuloConnectionString,
                new AccumuloScheme(accumuloQueryCriteria), SinkMode.REPLACE);
        // }}
		
        // {{
        // SINK tap - HDFS
        Scheme schemeTextLine = new TextLine();
        schemeTextLine.setNumSinkParts(numReducers);
        Tap sinkTapHDFS = new Hfs(schemeTextLine, outputHDFSPath,
                SinkMode.REPLACE);
	// }}

	
        // {{
        // PIPE
        Pipe readPipe = new Each("readPipe", new Identity(sourceTapAccumulo.getDefaultAccumuloFields()));
	// }}

	// {{
        // EXECUTE
        Flow flow = new HadoopFlowConnector(properties).connect(
                sourceTapAccumulo, sinkTapHDFS, readPipe);
        flow.complete();
	// }}

    }

}

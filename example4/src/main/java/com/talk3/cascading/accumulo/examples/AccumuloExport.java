package com.talk3.cascading.accumulo.examples;

import java.util.Properties;
import org.apache.hadoop.mapred.JobConf;
import cascading.flow.Flow;
import cascading.flow.FlowDef;
import cascading.operation.Identity;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.flow.hadoop.HadoopFlowConnector;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.scheme.hadoop.TextDelimited;
import cascading.tap.hadoop.Hfs;
import cascading.scheme.hadoop.TextLine;
import cascading.tuple.Fields;
import com.talk3.cascading.accumulo.AccumuloScheme;
import com.talk3.cascading.accumulo.AccumuloTap;

public class AccumuloExport {

    public static void main(String[] args) throws Exception {

        if (args.length < 8) {
            throw new Exception("Insufficient parameters!  "
                    + "(1)Connection string, "
                    + "(2)query criteria, "
                    + "(3)output fields declarator, "
                    + "(4)output field delimiter (should match column qualifier name exactly) "
                    + "(5)output display headers (TRUE/FALSE), "
                    + "(6)output HDFS path, "
                    + "(7)number of reducers, "
                    + "(8)HDFS path for failures "
                    + "are the required parameters!");
        }

        // {{
        // ARGUMENTS
        String accumuloConnectionString = args[0].toString();
        String accumuloQueryCriteria = args[1].toString();
        String outputFieldDeclarator = args[2].toString();
        String outputFileFieldDelimiter = args[3].toString();
        String outputDisplayColumnHeaders = args[4].toString();
        String outputHDFSPath = args[5].toString();
        int numReducers = Integer.parseInt(args[6].toString());
        String trapHDFSPath = args[7].toString();
	// }}

        // {{
        //Output field declarator (Can be used to control field placement/order in output)
        Fields outputSinkFieldsDeclarator = new Fields();
        if (outputFieldDeclarator.length() > 0) {
            outputSinkFieldsDeclarator = outputSinkFieldsDeclarator.append(Util.buildFieldList(outputFieldDeclarator));
        }
        // }}

        // {{
        // JOB 
        JobConf jobConf = new JobConf();
        jobConf.setJarByClass(AccumuloExport.class);

        Properties properties = AppProps.appProps()
                .setName("AccumuloExport").setVersion("1.0.0")
                .buildProperties(jobConf);
        // }}

        // {{
        // SOURCE tap - Accumulo
        AccumuloTap sourceTapAccumulo = new AccumuloTap(accumuloConnectionString,
                new AccumuloScheme(accumuloQueryCriteria), SinkMode.REPLACE);
        // }}

        // {{
        // SINK tap - HDFS
        TextDelimited textDelimitedScheme = new TextDelimited(outputDisplayColumnHeaders.toUpperCase().equals("TRUE"), outputFileFieldDelimiter);
        textDelimitedScheme.setNumSinkParts(numReducers);
        Tap sinkTapHDFS = new Hfs(textDelimitedScheme, outputHDFSPath,
                SinkMode.REPLACE);
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
                .setName("DataIngester-flow-def")
                .addSource(rawDataPipe, sourceTapAccumulo)
                .addTrap(rawDataPipe, sinkTrapTapHDFS)
                .addTailSink(transformPipe, sinkTapHDFS);
        Flow flow = flowConnector.connect(flowDef);
        flow.complete();

        // }}
    }

}

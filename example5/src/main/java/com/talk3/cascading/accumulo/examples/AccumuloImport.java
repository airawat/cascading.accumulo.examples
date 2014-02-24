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
import cascading.flow.hadoop.HadoopFlowProcess;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.assembly.Discard;
import cascading.pipe.assembly.Retain;
import cascading.scheme.hadoop.TextDelimited;
import cascading.tap.hadoop.Hfs;
import cascading.scheme.hadoop.TextLine;
import cascading.tap.MultiSourceTap;
import cascading.tap.hadoop.GlobHfs;
import cascading.tuple.Fields;
import com.talk3.cascading.accumulo.AccumuloScheme;
import com.talk3.cascading.accumulo.AccumuloTap;
import java.util.HashMap;

public class AccumuloImport {

    public static void main(String[] args) throws Exception {

        // {{
        // Basic validation
        if (args.length < 11) {
            throw new Exception("Insufficient parameters!  "
                    + "(1)HDFS input source, "
                    + "(2)has header flag (Y/N), "
                    + "(3)Input source field delimiter, "
                    + "(4)input fields declarator, "
                    + "(5)input discard fields declarator, "
                    + "(6)output fields declarator, "
                    + "(7)rowKey construct, "
                    + "(8)column family map, "
                    + "(9)column visibility map, "
                    + "(10)output Accumulo connection string, and "
                    + "(11)HDFS path for failures "
                    + "are the required parameters!");
        }
        // }} 

        // {{
        // ARGUMENTS
        String HDFSInputSourcePath = args[0].toString();
        String hasHeader = args[1].toString();
        String inputSourceFieldDelimiter = args[2].toString();
        String inputSourceFieldDeclaratorCSV = args[3].toString();
        String inputSourceDiscardFieldDeclaratorCSV = args[4].toString();
        String inputSourceKeepFieldDeclaratorCSV = args[5].toString();
        String rowKeyConstructCSV = args[6].toString();
        String columnFamilyMapCSV = args[7].toString();
        String columnVisibilityMapCSV = args[8].toString();
        String accumuloConnectionString = args[9].toString();
        String trapHDFSPath = args[10].toString();
	// }}

        // {{
        // JOB 
        JobConf jobConf = new JobConf();
        jobConf.setJarByClass(AccumuloExport.class);

        Properties properties = AppProps.appProps()
                .setName("ImportIntoAccumulo").setVersion("1.0.0")
                .buildProperties(jobConf);
        // }}

        // {{
        //Instantiate field list
        Fields inputSourceFieldsDeclarator = new Fields();
        boolean inputSourceFieldsInfoValid = false;
        if (inputSourceFieldDeclaratorCSV.length() > 1) 
        {
            inputSourceFieldsDeclarator = inputSourceFieldsDeclarator.append(Util.buildFieldList(inputSourceFieldDeclaratorCSV));
            if (inputSourceFieldsDeclarator.size() > 0) {
                inputSourceFieldsInfoValid = true;
            }
        }

        Fields discardInputFieldsDeclarator = new Fields();
        boolean inputSourceDiscardFieldsInfoValid = false;
        if (inputSourceDiscardFieldDeclaratorCSV.length() > 1)
        {
            discardInputFieldsDeclarator = discardInputFieldsDeclarator.append(Util.buildFieldList(inputSourceDiscardFieldDeclaratorCSV));
            if (discardInputFieldsDeclarator.size() > 0) {
                inputSourceDiscardFieldsInfoValid = true;
            }
        }
        
        Fields inputSourceKeepFieldsDeclarator = new Fields();
        boolean inputSourceKeepFieldsInfoValid = false;
        if (inputSourceKeepFieldDeclaratorCSV.length() > 1) 
        {
            inputSourceKeepFieldsDeclarator = inputSourceKeepFieldsDeclarator.append(Util.buildFieldList(inputSourceKeepFieldDeclaratorCSV));
            if (inputSourceKeepFieldsDeclarator.size() > 0) {
                inputSourceKeepFieldsInfoValid = true;
            }
        }
        
        // }}

        // {{
        // Structures for passing accumulo specific details to the transpose function
        HashMap<String, String> inputSourceRowKeyConstructMap = new HashMap<String, String>();
        HashMap<String, String> inputSourceColFamilyMap = new HashMap<String, String>();
        HashMap<String, String> inputSourceColVisibilityMap = new HashMap<String, String>();
        boolean rowKeyInfoValid = false;
        boolean columnFamilyInfoValid = false;
        boolean columnVisibilityInfoValid = false;

        if (rowKeyConstructCSV.length() >= 1)
        {
            if (Util.getMap("", rowKeyConstructCSV).size() > 0) {
                inputSourceRowKeyConstructMap
                        .putAll(Util
                                .getMap("", rowKeyConstructCSV));
                rowKeyInfoValid = true;
            }
        }
        
        if (columnFamilyMapCSV.length() > 1)
        {
            if (Util.getMap("", columnFamilyMapCSV).size() > 0) {
            inputSourceColFamilyMap
                        .putAll(Util
                                .getMap("", columnFamilyMapCSV));
                columnFamilyInfoValid = true;
            }
        }

        if (columnVisibilityMapCSV.length() >= 1)
        {
            if (Util.getMap("", columnVisibilityMapCSV).size() > 0) {
                inputSourceColVisibilityMap
                        .putAll(Util
                                .getMap("", columnVisibilityMapCSV));
                columnVisibilityInfoValid = true;
            }
        }
        // }}

        // {{
        // Further validation
        if (!rowKeyInfoValid || !columnFamilyInfoValid || !columnVisibilityInfoValid || !inputSourceFieldsInfoValid) {
            StringBuilder errBuilder = new StringBuilder();

            if(!hasHeader.equals("Y") && !hasHeader.equals("N")){
                errBuilder.append("Invalid value for \"has header\" flag;");
            }
            if (inputSourceFieldDelimiter.trim().length() == 0) {
                errBuilder.append("Input source field delimiter parameter is required;");
            }
            if (!inputSourceFieldsInfoValid) {
                errBuilder.append("Output fields parameter construct is invalid;");
            }
            if (!rowKeyInfoValid) {
                errBuilder.append("Row key parameter construct is invalid;");
            }
            if (!columnFamilyInfoValid) {
                errBuilder.append("Column family parameter construct is invalid;");
            }
            if (!columnVisibilityInfoValid) {
                errBuilder.append("Column visibility parameter construct is invalid;");
            }

            throw new Exception("The following issues were found for the parameters provided: "
                    + errBuilder.toString());
        }
        // }}

        // {{
        // SOURCE tap - HDFS
        HadoopFlowProcess hfp = new HadoopFlowProcess(jobConf);

        TextDelimited sourceSchemeData = new TextDelimited(
                inputSourceFieldsDeclarator, (hasHeader.equals("Y") ? true : false),inputSourceFieldDelimiter);
        GlobHfs sourceFilesGlobData = new GlobHfs(sourceSchemeData,
                HDFSInputSourcePath);
        Tap sourceTapHDFS = new MultiSourceTap(sourceFilesGlobData);
        // }}
        
        // {{
        // SINK tap - Accumulo
        AccumuloTap sinkTapAccumulo = new AccumuloTap(accumuloConnectionString,
                new AccumuloScheme(), SinkMode.UPDATE);
	// }}

        // {{
        // TRAP tap - HDFS
        Tap sinkTrapTapHDFS = new Hfs(new TextLine(), trapHDFSPath,
                SinkMode.REPLACE);
	// }}

        // {{
        // PIPE
        Pipe rawDataPipe = new Pipe("rawDataPipe");
        rawDataPipe = new Each(rawDataPipe, new Identity(
                inputSourceFieldsDeclarator));

        if (inputSourceDiscardFieldsInfoValid) {
            rawDataPipe = new Discard(rawDataPipe,discardInputFieldsDeclarator);
        }
        
        Pipe transformPipe = new Pipe("transformPipe");
        
        // Transpose data, create key, set column family, qualifier name etc
        transformPipe = new Each(rawDataPipe,       
                                   inputSourceKeepFieldsDeclarator, 
                                   new TransposeToAccumuloLayoutFunction(
                                        inputSourceRowKeyConstructMap,
                                        inputSourceColFamilyMap,
                                        inputSourceColVisibilityMap), 
                        Fields.RESULTS);

        transformPipe = new Each(transformPipe, new Identity(
                sinkTapAccumulo.getDefaultAccumuloFields()));
	// }}

        // {{
        // EXECUTE
        // Connect the taps, pipes, etc., into a flow & execute
        HadoopFlowConnector flowConnector = new HadoopFlowConnector(properties);

        FlowDef flowDef = FlowDef
                .flowDef()
                .setName("AccumuloImport-flow-def")
                .addSource(rawDataPipe, sourceTapHDFS)
                .addTrap(rawDataPipe, sinkTrapTapHDFS)
                .addTailSink(transformPipe, sinkTapAccumulo);
        Flow flow = flowConnector.connect(flowDef);
        flow.complete();
        // }}

    }

}

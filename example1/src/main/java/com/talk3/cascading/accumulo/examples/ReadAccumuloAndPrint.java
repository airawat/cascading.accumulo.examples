package com.talk3.cascading.accumulo.examples;

import java.util.Properties;
import org.apache.hadoop.mapred.JobConf;
import cascading.property.AppProps;
import cascading.flow.hadoop.HadoopFlowProcess;
import cascading.tuple.TupleEntryIterator;
import com.talk3.cascading.accumulo.AccumuloScheme;
import com.talk3.cascading.accumulo.AccumuloTap;
        
public class ReadAccumuloAndPrint {

	public static void main(String[] args) throws Exception {
            if(args.length == 0)
            {
                throw new Exception("Insufficient paramaters to run the program!");
            }
            
            
                // {{
		// ARGUMENTS
                String accumuloConnectionString = args[0].toString();
                String accumuloQueryCriteria="";
                
                if(args.length ==2)
                    accumuloQueryCriteria = args[1].toString();
                
                // }}
            
            
		// {{
		// JOB 
		JobConf jobConf = new JobConf();
		jobConf.setJarByClass(ReadAccumuloAndPrint.class);

		Properties properties = AppProps.appProps()
				.setName("ReadAccumuloAndPrint").setVersion("1.0.0")
				.buildProperties(jobConf);
		// }}

		// {{
		// READ and PRINT to standard out
		HadoopFlowProcess hfp = new HadoopFlowProcess(jobConf);
		AccumuloTap sourceTapAccumulo = new AccumuloTap(accumuloConnectionString,
				new AccumuloScheme(accumuloQueryCriteria));
		TupleEntryIterator tei = sourceTapAccumulo.openForRead(hfp);
		while (tei.hasNext()) {
			System.out.println(tei.next());
		}
		tei.close();
		// }}

	}

}

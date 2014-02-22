package com.talk3.cascading.accumulo.examples;

import java.util.Properties;

import org.apache.hadoop.mapred.JobConf;

import cascading.flow.FlowDef;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.flow.hadoop.HadoopFlowConnector;
import cascading.flow.hadoop.HadoopFlowProcess;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.TupleEntryIterator;
import cascading.scheme.hadoop.TextDelimited;
import com.talk3.cascading.accumulo.AccumuloScheme;
import com.talk3.cascading.accumulo.AccumuloTap;

public class TableOperations {

	public static void main(String[] args) throws Exception {

		if(args.length < 2)
                {
                    throw new Exception("Insufficient arguments!  Connection string and table operation parameter are required!");
                }

		// {{
		// ARGUMENTS - parse key arguments
		String accumuloConnectionString = args[0].toString();
		String tableOperation = args[1].toString();

		// }}

                // {{
		// JOB
		JobConf conf = new JobConf();
		conf.setJarByClass(TableOperations.class);
		Properties properties = AppProps.appProps()
				.setName("TableOperations").setVersion("1.0.0")
				.buildProperties(conf);

		// }}
                
		// {{
		// TABLE OPERATIONS
		if (tableOperation.equals("createTable")) {

			AccumuloTap accumuloTapTblOps;
			accumuloTapTblOps = new AccumuloTap(accumuloConnectionString,
					new AccumuloScheme());

			boolean tblOpStatus = accumuloTapTblOps.createResource(conf);

			System.out.println("The table " + " was "
					+ (tblOpStatus ? "created successfully!" : "not created!"));
			
		} else if (tableOperation.equals("createTableWithSplits")) {
                        if(args.length<3)
                        {
                            throw new Exception("Insufficient arguments!  Connection string, table operation parameter, and split list are required!");
                        }
			String tableSplits = args[2].toString().trim();
			conf.set("TableSplits", tableSplits);

			AccumuloTap accumuloTapTblOps;
			accumuloTapTblOps = new AccumuloTap(accumuloConnectionString,
					new AccumuloScheme());

			boolean tblOpStatus = accumuloTapTblOps.createResource(conf);

			System.out.println("The table " + " was "
					+ (tblOpStatus ? "created successfully!" : "not created!"));

		} else if (tableOperation.equals("checkIfTableExists")) {

			AccumuloTap accumuloTapTblOps;
			accumuloTapTblOps = new AccumuloTap(accumuloConnectionString,
					new AccumuloScheme());

			boolean tblOpStatus = accumuloTapTblOps.resourceExists(conf);

			System.out.println("The table "
					+ (tblOpStatus ? " exists!" : " does not exist!"));

		} else if (tableOperation.equals("deleteTable")) {

			AccumuloTap accumuloTapTblOps;
			accumuloTapTblOps = new AccumuloTap(accumuloConnectionString,
					new AccumuloScheme());

			boolean tblOpStatus = accumuloTapTblOps.deleteResource(conf);

			System.out.println("The table " + " was "
					+ (tblOpStatus ? "deleted successfully!" : "not deleted!"));
			
		} else if (tableOperation.equals("flush")) {

			AccumuloTap accumuloTapTblOps;
			accumuloTapTblOps = new AccumuloTap(accumuloConnectionString,
					new AccumuloScheme());

			boolean tblOpStatus = accumuloTapTblOps.flushResource(conf);

			System.out.println("The flush operation "
					+ (tblOpStatus ? "completed successfully!" : "failed!"));
			
		} else if (tableOperation.equals("flushKeyRange")) {

                        if(args.length<4)
                        {
                            throw new Exception("Insufficient arguments!  Connection string, table operation parameter, range start rowkey and range end rowkey are required!");
                        }
			AccumuloTap accumuloTapTblOps;
			accumuloTapTblOps = new AccumuloTap(accumuloConnectionString,
					new AccumuloScheme());
			
			//Key range
			conf.set("rowKeyRangeStart", args[2]);
			conf.set("rowKeyRangeEnd", args[3]);

			boolean tblOpStatus = accumuloTapTblOps.flushResource(conf);

			System.out.println("The flush operation "
					+ (tblOpStatus ? "completed successfully!" : "failed!"));
			
		} else {
			System.out.println("Could not match table operation - "
					+ tableOperation);
			System.out
					.println("---------------------------------------------------------------");
			System.out.println("Invalid table operation parameter!");
			System.out.println("Accepted parameters are - ");
			System.out.println("createTable");
			System.out.println("createTableWithSplits");
			System.out.println("checkIfTableExists");
                        System.out.println("flush");
                        System.out.println("flushKeyRange");
			System.out.println("deleteTable");
		}

		// }}
	}

}
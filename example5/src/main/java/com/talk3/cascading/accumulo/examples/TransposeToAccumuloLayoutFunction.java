package com.talk3.cascading.accumulo.examples;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cascading.flow.FlowProcess;
import cascading.operation.BaseOperation;
import cascading.operation.Function;
import cascading.operation.FunctionCall;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;

public class TransposeToAccumuloLayoutFunction extends BaseOperation implements Function {

    String colFamily = "";
    String colVisibility = "";
    String colValue = "";
    String colQualifier = "";

    HashMap<String, String> rowKeyConstructMap;
    HashMap<String, String> colFamilyMap;
    HashMap<String, String> colVisibilityMap;

    public TransposeToAccumuloLayoutFunction(HashMap<String, String> rowKeyConstructMap,
            HashMap<String, String> colFamilyMap,
            HashMap<String, String> colVisibilityMap) throws Exception {

        this.rowKeyConstructMap = rowKeyConstructMap;
        this.colFamilyMap = colFamilyMap;
        this.colVisibilityMap = colVisibilityMap;

        String errors = validate();
        if (errors.length() > 0) {
            throw new Exception("Unable to transpose due to errors - " + errors);
        }

    }

    private String validate() {
        StringBuilder errorBuilder = new StringBuilder();
        if (rowKeyConstructMap.size() == 0) {
            errorBuilder.append("Criteria for constructing row key is required;");

        } else if (colFamilyMap.size() == 0) {
            errorBuilder.append("Column family mapping is required; ");

        } else if (colVisibilityMap.size() == 0) {
            errorBuilder.append("Column visibility mapping is required; ");
        }

        return errorBuilder.toString();
    }

    private String TransformDate(String inputValue, String inputOutputCSVFormat) {

        String[] arrInputOutputCSVFormat = inputOutputCSVFormat.split(",");
        String inputFormat = arrInputOutputCSVFormat[0];
        String outputFormat = arrInputOutputCSVFormat[1];
        String outputValue = "";

        SimpleDateFormat inputFormatter = new SimpleDateFormat(inputFormat);
        SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat);
        try {

            Date inputDate = inputFormatter.parse(inputValue);
            outputValue = outputFormatter.format(inputDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outputValue;
    }

    public void operate(FlowProcess flowProcess, FunctionCall functionCall) {

        TupleEntry inputTupleEntry = functionCall.getArguments();
        long colTimestamp = System.currentTimeMillis();

        HashMap<String, Integer> fieldMap = new HashMap<String, Integer>();
        for (int i = 0; i < inputTupleEntry.getFields().size(); i++) {
            fieldMap.put(inputTupleEntry.getFields().get(i).toString(), i);
        }

        // Construct row key
        // {
        StringBuilder rowKeyBuilder = new StringBuilder();
        String[] rowKeyFields = rowKeyConstructMap.get("FIELDS-CSV").split(",");
        String rowkeyDelimiter = (rowKeyConstructMap.get("DELIMITER") == null ? "." : rowKeyConstructMap.get("DELIMITER"));
        String rowkeyPrefix = (rowKeyConstructMap.get("PREFIX") == null ? "" : rowKeyConstructMap.get("PREFIX"));
        String rowkeySuffix = (rowKeyConstructMap.get("SUFFIX") == null ? "" : rowKeyConstructMap.get("SUFFIX"));

        if (rowkeyPrefix.length() > 0) {
            rowKeyBuilder.append(rowkeyPrefix).append(rowkeyDelimiter);
        }
        for (String rowKeyField : rowKeyFields) {
            rowKeyBuilder.append(
                    inputTupleEntry.getString(fieldMap.get(rowKeyField)))
                    .append(rowKeyConstructMap.get("DELIMITER"));
        }
        if (rowkeySuffix.length() > 0) {
            rowKeyBuilder.append(rowkeySuffix).append(rowkeyDelimiter);
        }

        String colRowKey = rowKeyBuilder.toString();
        colRowKey = colRowKey.substring(0, colRowKey.length() - 1);
        // }

	// Transpose and add result tuple to output collector
        // {
        for (int i = 0; i < inputTupleEntry.getFields().size(); i++) {

            colQualifier = inputTupleEntry.getFields().get(i).toString();
            colFamily = colFamilyMap.get(colQualifier);
            colVisibility = colVisibilityMap.get(colQualifier);
            colValue = inputTupleEntry.getString(i);

            Tuple resultTuple = new Tuple();
            resultTuple.add(colRowKey);
            resultTuple.add(colFamily);
            resultTuple.add(colQualifier);
            resultTuple.add(colVisibility);
            resultTuple.add(colTimestamp);
            resultTuple.add(colValue);
            functionCall.getOutputCollector().add(resultTuple);

            colFamily = "";
            colVisibility = "";
            colValue = "";
            colQualifier = "";

        }
        // }
    }

}

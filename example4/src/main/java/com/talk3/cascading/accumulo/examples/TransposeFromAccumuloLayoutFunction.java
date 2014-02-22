package com.talk3.cascading.accumulo.examples;

import cascading.flow.FlowProcess;
import cascading.operation.Aggregator;
import cascading.operation.AggregatorCall;
import cascading.operation.BaseOperation;

import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import java.util.Hashtable;

public class TransposeFromAccumuloLayoutFunction extends
        BaseOperation<TransposeFromAccumuloLayoutFunction.Context> implements
        Aggregator<TransposeFromAccumuloLayoutFunction.Context> {

    public TransposeFromAccumuloLayoutFunction(Fields fieldDeclaration) {
        super(1, fieldDeclaration);
    }

    public static class Context {

        Hashtable htContext = new Hashtable();
    }

    public void start(FlowProcess flowProcess,
            AggregatorCall<Context> aggregatorCall) {
        aggregatorCall.setContext(new Context());
    }

    public void aggregate(FlowProcess flowProcess,
            AggregatorCall<Context> aggregatorCall) {
        TupleEntry arguments = aggregatorCall.getArguments();
        Context context = aggregatorCall.getContext();

        context.htContext.put(0, arguments.getString(0));

        int fieldIndex = Util.getFieldIndex(fieldDeclaration, arguments.getString(2));

        if (fieldIndex >= 0) {
            context.htContext.put(fieldIndex, arguments.getString(5));
        }
    }

    public void complete(FlowProcess flowProcess,
            AggregatorCall<Context> aggregatorCall) {
        Context context = aggregatorCall.getContext();

        Tuple resultTuple = new Tuple();
        for (int i = 0; i < context.htContext.size(); i++) {
            resultTuple.add(context.htContext.get(i));
        }

        aggregatorCall.getOutputCollector().add(resultTuple);
    }

}

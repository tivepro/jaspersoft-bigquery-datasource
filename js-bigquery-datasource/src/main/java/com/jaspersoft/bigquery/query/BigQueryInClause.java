package com.jaspersoft.bigquery.query;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryInClause extends BigQueryAbstractInClause {
    protected static final String OPERATOR_IN = "IN";

    protected static final String OPERATOR_NULL = "IS NULL";

    protected static final String OPERATOR_AND_OR = " OR ";

    protected static final BigQueryInClause singleton = new BigQueryInClause();

    /**
     * Returns the singleton function instance.
     * 
     * @return the singleton function instance
     */
    public static BigQueryInClause instance() {
        return singleton;
    }

    protected void appendInOperator(StringBuffer stringBuffer) {
        stringBuffer.append(OPERATOR_IN);
    }

    protected void appendNullOperator(StringBuffer stringBuffer) {
        stringBuffer.append(OPERATOR_NULL);
    }

    protected void appendAndOrOperator(StringBuffer stringBuffer) {
        stringBuffer.append(OPERATOR_AND_OR);
    }
}
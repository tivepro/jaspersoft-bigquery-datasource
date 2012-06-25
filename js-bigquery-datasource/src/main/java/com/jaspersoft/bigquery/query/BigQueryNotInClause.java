package com.jaspersoft.bigquery.query;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryNotInClause extends BigQueryAbstractInClause {
    protected static final String OPERATOR_NOT_IN = "NOT IN";

    protected static final String OPERATOR_NULL = "IS NOT NULL";

    protected static final String OPERATOR_AND_OR = " AND ";

    protected static final BigQueryNotInClause singleton = new BigQueryNotInClause();

    /**
     * Returns the singleton function instance.
     * 
     * @return the singleton function instance
     */
    public static BigQueryNotInClause instance() {
        return singleton;
    }

    protected void appendInOperator(StringBuffer stringBuffer) {
        stringBuffer.append(OPERATOR_NOT_IN);
    }

    protected void appendNullOperator(StringBuffer stringBuffer) {
        stringBuffer.append(OPERATOR_NULL);
    }

    protected void appendAndOrOperator(StringBuffer stringBuffer) {
        stringBuffer.append(OPERATOR_AND_OR);
    }
}
package com.jaspersoft.bigquery.query;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.query.JRAbstractQueryExecuter;

import org.apache.log4j.Logger;

import com.jaspersoft.bigquery.BigQueryDataSource;
import com.jaspersoft.bigquery.connection.BigQueryConnection;

/**
 * This implementation process report parameters to create a
 * {@link HBaseDataSource}
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryQueryExecuter extends JRAbstractQueryExecuter {

    private final static Logger logger = Logger.getLogger(BigQueryQueryExecuter.class);

    private Map<String, ? extends JRValueParameter> parameters;

    private BigQueryQueryWrapper wrapper;

    public BigQueryQueryExecuter(JRDataset dataset, Map<String, ? extends JRValueParameter> parameters) {
        super(dataset, parameters);
        this.parameters = parameters;
        parseQuery();
    }

    /**
     * Method not implemented
     */
    @Override
    public boolean cancelQuery() throws JRException {
        return false;
    }

    /**
     * Closes internal structures
     */
    @Override
    public void close() {
    }

    /**
     * Creates a new {@link HBaseDataSource} from the report parameters
     */
    @Override
    public JRDataSource createDatasource() throws JRException {
        BigQueryConnection connection = (BigQueryConnection) ((Map<?, ?>) getParameterValue(JRParameter.REPORT_PARAMETERS_MAP))
                .get(JRParameter.REPORT_CONNECTION);
        if (connection == null) {
            logger.error("No BigQuery connection");
            JRValueParameter valueParameter = parameters.get(JRParameter.REPORT_CONNECTION);
            if (valueParameter == null) {
                throw new JRException("No BigQuery connection");
            }
            Object value = valueParameter.getValue();
            if (value == null) {
                throw new JRException("No BigQuery connection");
            }
            connection = (BigQueryConnection) value;
        }

        wrapper = new BigQueryQueryWrapper(connection, getQueryString());
        return new BigQueryDataSource(wrapper);

    }

    /**
     * Replacement of parameters
     */
    @Override
    protected String getParameterReplacement(String parameterName) {
        Object parameterValue = parameters.get(parameterName);
        if (parameterValue == null) {
            throw new JRRuntimeException("Parameter \"" + parameterName + "\" does not exist.");
        }
        if (parameterValue instanceof JRValueParameter) {
            parameterValue = ((JRValueParameter) parameterValue).getValue();
        }
        return String.valueOf(parameterValue);
    }

    public String getProcessedQueryString() {
        return getQueryString();
    }
}
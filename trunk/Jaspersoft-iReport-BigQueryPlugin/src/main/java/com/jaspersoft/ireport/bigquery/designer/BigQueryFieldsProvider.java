package com.jaspersoft.ireport.bigquery.designer;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.log4j.Logger;

import com.jaspersoft.ireport.bigquery.connection.BigQueryConnection;
import com.jaspersoft.ireport.designer.FieldsProvider;
import com.jaspersoft.ireport.designer.FieldsProviderEditor;
import com.jaspersoft.ireport.designer.IReportConnection;
import com.jaspersoft.ireport.designer.data.ReportQueryDialog;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryFieldsProvider implements FieldsProvider {
    private final static Logger logger = Logger.getLogger(BigQueryFieldsProvider.class);

    public BigQueryFieldsProvider() {
    }

    @Override
    public String designQuery(IReportConnection arg0, String arg1, ReportQueryDialog arg2) throws JRException,
            UnsupportedOperationException {
        logger.warn("NOT IMPLEMENTED: designQuery");
        return null;
    }

    @Override
    public FieldsProviderEditor getEditorComponent(ReportQueryDialog reportQueryDialog) {
        logger.warn("NOT IMPLEMENTED: getEditorComponent");
        return null;
    }

    @Override
    public boolean supportsGetFieldsOperation() {
        return true;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public JRField[] getFields(IReportConnection ireportConnection, JRDataset dataset, Map parameters)
            throws JRException, UnsupportedOperationException {
        if (ireportConnection == null || !(ireportConnection instanceof BigQueryConnection)) {
            throw new JRException("The active connection is not valid. Activate a BigQuery connection first.");
        }
        return com.jaspersoft.bigquery.BigQueryFieldsProvider.getFields(dataset, parameters,
                (com.jaspersoft.bigquery.connection.BigQueryConnection) ((ireportConnection).getConnection())).toArray(
                new JRField[0]);
    }

    @Override
    public boolean hasEditorComponent() {
        return false;
    }

    @Override
    public boolean hasQueryDesigner() {
        return false;
    }

    @Override
    public boolean supportsAutomaticQueryExecution() {
        return true;
    }
}
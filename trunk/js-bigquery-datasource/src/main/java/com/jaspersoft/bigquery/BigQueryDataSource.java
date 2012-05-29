package com.jaspersoft.bigquery;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.log4j.Logger;

import com.google.api.services.bigquery.model.TableRow.F;
import com.jaspersoft.bigquery.query.BigQueryQueryWrapper;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryDataSource implements JRDataSource {
    private Logger logger = Logger.getLogger(BigQueryDataSource.class);

    private BigQueryQueryWrapper wrapper;

    public final static String QUERY_LANGUAGE = "BigQuery";

    private List<F> rowList;

    private Map<String, Class<?>> fieldsMap;

    public BigQueryDataSource(BigQueryQueryWrapper wrapper) throws JRException {
        if (wrapper == null) {
            throw new JRException("No BigQuery wrapper defined");
        }
        this.wrapper = wrapper;
        if (logger.isDebugEnabled()) {
            logger.debug("New BigQuery data source");
        }
        fieldsMap = BigQueryFieldsProvider.getFieldsMap(wrapper);
    }

    @Override
    public Object getFieldValue(JRField field) throws JRException {
        F fieldRow = rowList.get(wrapper.fieldsMap.get(field.getName()));
        if (fieldRow != null) {
            String value = fieldRow.getV();
            Class<?> typeClass = fieldsMap.get(field.getName());
            if (typeClass == null || typeClass.equals(String.class)) {
                return value;
            }
            try {
                if (typeClass.equals(Integer.class)) {
                    return Integer.valueOf(value);
                } else if (typeClass.equals(Boolean.class)) {
                    return Boolean.valueOf(value);
                } else if (typeClass.equals(Float.class)) {
                    return Float.valueOf(value);
                }
                return value;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean next() throws JRException {
        rowList = wrapper.next();
        return rowList != null;
    }

}
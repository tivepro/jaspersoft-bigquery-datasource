/*
 * Copyright (C) 2005 - 2012 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.bigquery.query;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuter;

import org.apache.log4j.Logger;

import com.jaspersoft.bigquery.BigQueryDataSource;
import com.jaspersoft.bigquery.connection.BigQueryConnection;

/**
 * This implementation processes report parameters to create a {@link BigQueryDataSource}
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryQueryExecuter extends JRJdbcQueryExecuter {

    private final static Logger logger = Logger.getLogger(BigQueryQueryExecuter.class);

    private BigQueryQueryWrapper wrapper;

    public BigQueryQueryExecuter(JRDataset dataset, Map<String, ? extends JRValueParameter> parameters) {
        super(dataset, parameters);
    }

    protected void registerFunctions() {
        registerClauseFunction(CLAUSE_ID_IN, BigQueryInClause.instance());
        registerClauseFunction(CLAUSE_ID_NOTIN, BigQueryNotInClause.instance());
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
     * Creates a new {@link BigQueryDataSource} from the report parameters
     */
    @Override
    public JRDataSource createDatasource() throws JRException {
        BigQueryConnection connection = (BigQueryConnection) ((Map<?, ?>) getParameterValue(JRParameter.REPORT_PARAMETERS_MAP))
                .get(JRParameter.REPORT_CONNECTION);
        if (connection == null) {
            logger.error("No BigQuery connection");
            JRValueParameter valueParameter = getValueParameter(JRParameter.REPORT_CONNECTION, true);
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
     * Parameters replacement
     */
    @Override
    protected String getParameterReplacement(String parameterName) {
        Object parameterValue = getValueParameter(parameterName, true);
        if (parameterValue == null) {
            throw new JRRuntimeException("Parameter \"" + parameterName + "\" does not exist.");
        }
        if (parameterValue instanceof JRValueParameter) {
            parameterValue = ((JRValueParameter) parameterValue).getValue();
        }
        System.out.println("Parameter value: " + parameterValue);
        return String.valueOf(parameterValue);
    }

    public String getProcessedQueryString() {
        return getQueryString();
    }
}
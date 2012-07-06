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

package com.jaspersoft.bigquery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.design.JRDesignField;

import org.apache.log4j.Logger;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.jaspersoft.bigquery.connection.BigQueryConnection;
import com.jaspersoft.bigquery.query.BigQueryParameter;
import com.jaspersoft.bigquery.query.BigQueryQueryExecuter;
import com.jaspersoft.bigquery.query.BigQueryQueryWrapper;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryFieldsProvider {

    private static final Logger logger = Logger.getLogger(BigQueryFieldsProvider.class);

    private static final Map<String, Class<?>> TYPES;

    static {
        TYPES = new HashMap<String, Class<?>>();
        TYPES.put("STRING", String.class);
        TYPES.put("INTEGER", Integer.class);
        TYPES.put("FLOAT", Float.class);
        TYPES.put("BOOLEAN", Boolean.class);
    }

    public static List<JRDesignField> getFields(JRDataset dataset, Map<String, Object> parameters,
            BigQueryConnection connection) throws JRException {
        BigQueryQueryExecuter queryExecuter = null;
        BigQueryQueryWrapper wrapper = null;
        try {
            Map<String, JRValueParameter> newValueParameters = new HashMap<String, JRValueParameter>();
            for (String parameterName : parameters.keySet()) {
                Object parameterValue = parameters.get(parameterName);
                BigQueryParameter newParameter = new BigQueryParameter(parameterName, parameterValue);
                newValueParameters.put(parameterName, newParameter);
            }
            if (!parameters.containsKey(JRParameter.REPORT_CONNECTION)) {
                newValueParameters.put(JRParameter.REPORT_CONNECTION, new BigQueryParameter(
                        JRParameter.REPORT_CONNECTION, connection));
            }
            parameters.clear();
            parameters.put(JRParameter.REPORT_CONNECTION, connection);
            newValueParameters.put(JRParameter.REPORT_PARAMETERS_MAP, new BigQueryParameter(
                    JRParameter.REPORT_PARAMETERS_MAP, parameters));

            queryExecuter = new BigQueryQueryExecuter(dataset, newValueParameters);
            wrapper = new BigQueryQueryWrapper(connection, queryExecuter.getProcessedQueryString());
            Map<String, Class<?>> fieldsMap = getFieldsMap(wrapper);
            List<JRDesignField> fields = new ArrayList<JRDesignField>();
            for (String fieldName : fieldsMap.keySet()) {
                fields.add(createField(fieldName, fieldsMap.get(fieldName)));
            }
            return fields;
        } finally {
            if (wrapper != null) {
                wrapper.close();
            }
            if (queryExecuter != null) {
                queryExecuter.close();
            }
        }
    }

    public static Map<String, Class<?>> getFieldsMap(BigQueryQueryWrapper wrapper) {
        Map<String, Class<?>> fieldsMap = new TreeMap<String, Class<?>>();
        if (wrapper.schema != null) {
            List<TableFieldSchema> fields = wrapper.schema.getFields();
            for (int index = 0; index < fields.size(); index++) {
                fieldsMap.put(fields.get(index).getName(), TYPES.get(fields.get(index).getType()));
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Fields Map: " + fieldsMap);
            }
        }
        return fieldsMap;
    }

    private static JRDesignField createField(String fieldName, Class<?> _class) {
        JRDesignField field = new JRDesignField();
        field.setName(fieldName);
        field.setValueClass(_class);
        field.setDescription(null);
        return field;
    }
}
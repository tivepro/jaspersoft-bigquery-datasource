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
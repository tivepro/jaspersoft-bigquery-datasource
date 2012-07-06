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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.query.JRClauseTokens;
import net.sf.jasperreports.engine.query.JRQueryClauseContext;
import net.sf.jasperreports.engine.query.JRSqlAbstractInClause;

/**
 * 
 * @author Eric Diaz
 * 
 */
public abstract class BigQueryAbstractInClause extends JRSqlAbstractInClause {

    public void apply(JRClauseTokens clauseTokens, JRQueryClauseContext queryContext) {
        String column = clauseTokens.getToken(POSITION_DB_COLUMN);
        String parameter = clauseTokens.getToken(POSITION_PARAMETER);
        if (column == null) {
            throw new JRRuntimeException("BigQuery SQL IN clause missing DB column token");
        }

        if (parameter == null) {
            throw new JRRuntimeException("BigQuery SQL IN clause missing parameter token");
        }

        StringBuffer stringBuffer = queryContext.queryBuffer();
        Object parameterValue = queryContext.getValueParameter(parameter).getValue();
        if (parameterValue == null) {
            handleNoValues(queryContext);
        } else {
            Collection<?> parametersCollection = convert(parameter, parameterValue);
            int count = parametersCollection.size();
            Iterator<?> parameterIterator = parametersCollection.iterator();

            if (count == 0) {
                handleNoValues(queryContext);
            } else {
                StringBuffer nullStringBuffer = new StringBuffer();
                StringBuffer notNullStringBuffer = new StringBuffer();
                boolean nullFound = false;
                boolean notNullFound = false;
                int index = 0;
                List<Object> notNullQueryParameters = new ArrayList<Object>();

                while (parameterIterator.hasNext()) {
                    Object element = parameterIterator.next();
                    if (element == null) {
                        if (!nullFound) {
                            nullFound = true;
                            nullStringBuffer.append(column);
                            nullStringBuffer.append(' ');
                            appendNullOperator(nullStringBuffer);
                        }
                    } else {
                        if (!notNullFound) {
                            notNullFound = true;
                            notNullStringBuffer.append(column);
                            notNullStringBuffer.append(' ');
                            appendInOperator(notNullStringBuffer);
                            notNullStringBuffer.append(' ');
                            notNullStringBuffer.append('(');
                        }

                        if (index > 0) {
                            notNullStringBuffer.append(", ");
                        }
                        System.out.println("Element: " + element);
                        boolean isString = element instanceof String;
                        notNullStringBuffer.append(isString ? "\"" : "");
                        notNullStringBuffer.append(String.valueOf(element));
                        notNullStringBuffer.append(isString ? "\"" : "");
                        notNullQueryParameters.add(element);
                        index++;
                    }
                }
                if (nullFound) {

                    if (notNullFound) {
                        stringBuffer.append("( ");
                        stringBuffer.append(nullStringBuffer);
                        appendAndOrOperator(stringBuffer);
                    } else {
                        stringBuffer.append(nullStringBuffer);
                    }
                }
                if (notNullFound) {
                    notNullStringBuffer.append(')');

                    if (nullFound) {
                        notNullStringBuffer.append(" )");
                    }
                    stringBuffer.append(notNullStringBuffer);
                    queryContext.addQueryMultiParameters(parameter, count, nullFound);
                }
            }
        }
    }
}
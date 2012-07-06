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
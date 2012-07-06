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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactory;
import net.sf.jasperreports.engine.query.QueryExecuterFactoryBundle;
import net.sf.jasperreports.engine.util.JRSingletonCache;

import com.jaspersoft.bigquery.BigQueryDataSource;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryQueryExecuterFactoryBundle implements QueryExecuterFactoryBundle {
    private static final JRSingletonCache<JRQueryExecuterFactory> cache = new JRSingletonCache<JRQueryExecuterFactory>(
            JRQueryExecuterFactory.class);

    private static final BigQueryQueryExecuterFactoryBundle instance = new BigQueryQueryExecuterFactoryBundle();

    private static final String[] languages = new String[] { BigQueryDataSource.QUERY_LANGUAGE };

    private BigQueryQueryExecuterFactoryBundle() {
    }

    public static BigQueryQueryExecuterFactoryBundle getInstance() {
        return instance;
    }

    public String[] getLanguages() {
        return languages;
    }

    public JRQueryExecuterFactory getQueryExecuterFactory(String language) throws JRException {
        if (BigQueryDataSource.QUERY_LANGUAGE.equals(language)) {
            return (JRQueryExecuterFactory) cache.getCachedInstance(BigQueryQueryExecuterFactory.class.getName());
        }
        return null;
    }
}
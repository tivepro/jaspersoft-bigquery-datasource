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
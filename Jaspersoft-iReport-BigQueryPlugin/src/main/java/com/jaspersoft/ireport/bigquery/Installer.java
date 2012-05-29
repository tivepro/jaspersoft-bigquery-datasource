package com.jaspersoft.ireport.bigquery;

import org.openide.modules.ModuleInstall;

import com.jaspersoft.bigquery.BigQueryDataSource;
import com.jaspersoft.bigquery.connection.BigQueryConnectionManager;
import com.jaspersoft.bigquery.query.BigQueryQueryExecuterFactory;
import com.jaspersoft.ireport.bigquery.connection.BigQueryConnectionFactory;
import com.jaspersoft.ireport.bigquery.designer.BigQueryFieldsProvider;
import com.jaspersoft.ireport.designer.IReportManager;
import com.jaspersoft.ireport.designer.data.queryexecuters.QueryExecuterDef;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class Installer extends ModuleInstall {
    private static BigQueryConnectionManager connectionManager;

    public void restored() {
        IReportManager.getInstance().addConnectionImplementationFactory(new BigQueryConnectionFactory());
        IReportManager.getInstance().addQueryExecuterDef(
                new QueryExecuterDef(BigQueryDataSource.QUERY_LANGUAGE, BigQueryQueryExecuterFactory.class.getName(),
                        BigQueryFieldsProvider.class.getName()), true);
        System.out.println("Initializing BigQuery Module");
        connectionManager = new BigQueryConnectionManager();
    }

    public static BigQueryConnectionManager getConnectionManager() {
        return connectionManager;
    }

    @Override
    public void close() {
        super.close();
        System.out.println("Closing BigQuery Module");
        if (connectionManager != null) {
            connectionManager.shutdown();
        }
    }
}
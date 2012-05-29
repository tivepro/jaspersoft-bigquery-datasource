package com.jaspersoft.ireport.bigquery.connection;

import com.jaspersoft.ireport.designer.IReportConnection;
import com.jaspersoft.ireport.designer.connection.IReportConnectionFactory;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryConnectionFactory implements IReportConnectionFactory {
	public IReportConnection createConnection() {
		return new BigQueryConnection();
	}

	public String getConnectionClassName() {
		return BigQueryConnection.class.getName();
	}
}

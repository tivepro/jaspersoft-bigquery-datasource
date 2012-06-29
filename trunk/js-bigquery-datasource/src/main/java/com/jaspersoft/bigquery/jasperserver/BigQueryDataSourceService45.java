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

package com.jaspersoft.bigquery.jasperserver;

import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;

import org.apache.log4j.Logger;

import com.jaspersoft.bigquery.connection.BigQueryConnection;
import com.jaspersoft.bigquery.connection.BigQueryConnectionManager;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryDataSourceService45 implements ReportDataSourceService {
    private final static Logger logger = Logger.getLogger(BigQueryDataSourceService45.class);

    protected BigQueryConnection connection;

    private String serviceAccountId;

    private String privateKeyFilePath;

    private String projectId;

    private BigQueryConnectionManager connectionManager;

    /**
     * Returns the active connection to the pool
     */
    @Override
    public void closeConnection() {
        if (logger.isDebugEnabled()) {
            logger.debug("BigQuery connection close requested");
        }
        if (connectionManager != null && connection != null) {
            connectionManager.returnConnection(connection);
            connection = null;
            if (logger.isDebugEnabled()) {
                logger.debug("BigQuery connection returned");
            }
        }
    }

    /**
     * Creates a new connection base on the parameters set
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setReportParameterValues(Map parameters) {
        try {
            createConnection();
            parameters.put(JRParameter.REPORT_CONNECTION, connection);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    protected void createConnection() throws JRException {
        if (connection != null) {
            closeConnection();
        }
        connectionManager.setServiceAccountId(serviceAccountId);
        connectionManager.setPrivateKeyFilePath(privateKeyFilePath);
        connectionManager.setProjectId(projectId);
        try {
            connection = connectionManager.borrowConnection();
            if (logger.isDebugEnabled()) {
                logger.debug("BigQuery connection created");
            }
        } catch (Exception e) {
            logger.error(e);
            throw new JRException(e.getMessage());
        }
    }

    public void setPrivateKeyFilePath(String privateKeyFilePath) {
        this.privateKeyFilePath = privateKeyFilePath;
    }

    public void setServiceAccountId(String serviceAccountId) {
        this.serviceAccountId = serviceAccountId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getPrivateKeyFilePath() {
        return privateKeyFilePath;
    }

    public String getServiceAccountId() {
        return serviceAccountId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setConnectionManager(BigQueryConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public BigQueryConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
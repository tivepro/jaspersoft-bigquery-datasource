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

package com.jaspersoft.bigquery.connection;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.log4j.Logger;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryConnectionFactory implements PoolableObjectFactory<BigQueryConnection> {
    private final Logger logger = Logger.getLogger(BigQueryConnectionFactory.class);

    private String serviceAccountId;

    private String privateKeyFilePath;

    private String projectId;

    @Override
    public void activateObject(BigQueryConnection connection) throws Exception {
    }

    @Override
    public void destroyObject(BigQueryConnection connection) throws Exception {
        logger.info("Factory destroy object");
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public BigQueryConnection makeObject() throws Exception {
        logger.info("Factory make object");
        try {
            return new BigQueryConnection(serviceAccountId, privateKeyFilePath, projectId);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new JRException("Error making a new BigQuery connection");
        }
    }

    @Override
    public void passivateObject(BigQueryConnection connection) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Passivate is not implemented");
        }
    }

    @Override
    public boolean validateObject(BigQueryConnection connection) {
        logger.info("Factory validate object");
        if (connection != null) {
            try {
                return (serviceAccountId != null && serviceAccountId.equals(connection.getServiceAccountId()))
                        && (privateKeyFilePath == null ? true : privateKeyFilePath.equals(connection
                                .getPrivateKeyFilePath()))
                        && (projectId == null ? true : projectId.equals(connection.getProjectId()))
                        && (connection.test() != null);
            } catch (JRException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void setPrivateKeyFilePath(String privateKeyFilePath) {
        this.privateKeyFilePath = privateKeyFilePath;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setServiceAccountId(String serviceAccountId) {
        this.serviceAccountId = serviceAccountId;
    }
}
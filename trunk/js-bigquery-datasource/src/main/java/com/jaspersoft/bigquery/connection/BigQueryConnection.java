package com.jaspersoft.bigquery.connection;

import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;

import org.apache.log4j.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.Bigquery.Datasets.List;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.DatasetList;
import com.google.api.services.bigquery.model.DatasetList.Datasets;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryConnection implements Connection {
    private HttpTransport transport;

    private JsonFactory jsonFactory;

    private String serviceAccountId;

    private String privateKeyFilePath;

    private String projectId;

    private final Logger logger = Logger.getLogger(BigQueryConnection.class);

    private Bigquery bigquery;

    public BigQueryConnection(String serviceAccountId, String privateKeyFilePath, String projectId) throws Throwable {
        transport = new NetHttpTransport();
        jsonFactory = new JacksonFactory();
        this.serviceAccountId = serviceAccountId;
        this.privateKeyFilePath = privateKeyFilePath;
        this.projectId = projectId;
        if (logger.isDebugEnabled()) {
            logger.debug("serviceAccountId: " + serviceAccountId);
            logger.debug("privateKeyFilePath: " + privateKeyFilePath);
        }
        create();
    }

    private void create() throws Throwable {
        File privateKeyFile = new File(privateKeyFilePath);
        if (!privateKeyFile.exists()) {
            throw new JRException("The file \"" + privateKeyFilePath + "\" doesn't exist");
        }
        GoogleCredential credential = new GoogleCredential.Builder().setTransport(transport)
                .setJsonFactory(jsonFactory).setServiceAccountId(serviceAccountId)
                .setServiceAccountScopes(BigqueryScopes.BIGQUERY)
                .setServiceAccountPrivateKeyFromP12File(privateKeyFile).build();

        bigquery = Bigquery.builder(transport, jsonFactory).setApplicationName("Google-BigQuery-Application/1.0")
                .setHttpRequestInitializer(credential).build();
    }

    public String getProjectId() {
        return projectId;
    }

    public Bigquery getBigquery() {
        return bigquery;
    }

    public String getPrivateKeyFilePath() {
        return privateKeyFilePath;
    }

    public String getServiceAccountId() {
        return serviceAccountId;
    }

    @Override
    public void close() {
        if (transport != null) {
            try {
                transport.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
            transport = null;
        }
        jsonFactory = null;
        bigquery = null;
        logger.info("BigQuery connection closed");
    }

    @Override
    public boolean isClosed() throws SQLException {
        return transport == null;
    }

    public String test() throws JRException {
        try {
            if (transport != null && jsonFactory != null && bigquery != null) {
                List datasetRequest = bigquery.datasets().list("publicdata");
                DatasetList datasetList = datasetRequest.execute();
                StringBuilder builder = new StringBuilder();
                builder.append("Available datasets on publicdata: ");
                if (datasetList != null) {
                    java.util.List<Datasets> datasets = datasetList.getDatasets();
                    for (Datasets dataset : datasets) {
                        builder.append(dataset.getId());
                        builder.append(",");
                    }
                }
                if (builder.charAt(builder.length() - 1) == ',') {
                    builder.setCharAt(builder.length() - 1, '.');
                }
                return builder.toString();
            }
        } catch (Throwable e) {
            if (e.getMessage() != null && e.getMessage().contains("unauthorized")) {
                logger.error(e);
                throw new JRException(
                        "Unauthorized exception. Please review your serviceAccountId and privateKeyFilePath."
                                + "Also ensure your system clock is synchronized with a NTC server");
            }
            throw new JRException(e);
        }
        throw new JRException("Couldn't connect to BigQuery");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return null;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return null;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return false;
    }

    @Override
    public void commit() throws SQLException {
    }

    @Override
    public void rollback() throws SQLException {
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
    }

    @Override
    public String getCatalog() throws SQLException {
        return null;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return 0;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return null;
    }

    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }
}

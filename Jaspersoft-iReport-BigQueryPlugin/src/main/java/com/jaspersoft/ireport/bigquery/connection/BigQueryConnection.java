package com.jaspersoft.ireport.bigquery.connection;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignQuery;

import org.openide.util.NbBundle;

import com.jaspersoft.bigquery.BigQueryDataSource;
import com.jaspersoft.bigquery.connection.BigQueryConnectionManager;
import com.jaspersoft.ireport.bigquery.Installer;
import com.jaspersoft.ireport.bigquery.designer.BigQueryFieldsProvider;
import com.jaspersoft.ireport.designer.IReportConnectionEditor;
import com.jaspersoft.ireport.designer.connection.JDBCConnection;
import com.jaspersoft.ireport.designer.data.WizardFieldsProvider;
import com.jaspersoft.ireport.designer.utils.Misc;

/**
 * 
 * @author Eric Diaz
 * 
 *         It extends from JDBCConnection to allow iReport to close the
 *         connection
 * 
 */
public class BigQueryConnection extends JDBCConnection implements WizardFieldsProvider {
    private static final String SERVICE_ACCOUNT_ID = "serviceAccountId";

    private static final String PRIVATE_KEY_FILE_PATH = "privateKeyFilePath";

    private static final String PROJECT_ID = "projectId";

    private String serviceAccountId;

    private String privateKeyFilePath;

    private String projectId;

    private com.jaspersoft.bigquery.connection.BigQueryConnection currentConnection;

    public BigQueryConnection() {
        super();
        setName(NbBundle.getMessage(BigQueryConnection.class, "connectionName"));
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(BigQueryConnection.class, "connectionType");
    }

    @Override
    public IReportConnectionEditor getIReportConnectionEditor() {
        return new BigQueryConnectionEditor();
    }

    @Override
    public Connection getConnection() {
        try {
            return createConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getServiceAccountId() {
        return serviceAccountId;
    }

    public void setServiceAccountId(String serviceAccountId) {
        this.serviceAccountId = serviceAccountId;
    }

    public String getPrivateKeyFilePath() {
        return privateKeyFilePath;
    }

    public void setPrivateKeyFilePath(String privateKeyFilePath) {
        this.privateKeyFilePath = privateKeyFilePath;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    private com.jaspersoft.bigquery.connection.BigQueryConnection createConnection() throws Exception {
        BigQueryConnectionManager connectionManager = Installer.getConnectionManager();
        if (currentConnection != null) {
            connectionManager.returnConnection(currentConnection);
        }
        connectionManager.setServiceAccountId(serviceAccountId);
        connectionManager.setPrivateKeyFilePath(privateKeyFilePath);
        connectionManager.setProjectId(projectId);
        return connectionManager.borrowConnection();
    }

    public String getQueryLanguage() {
        return BigQueryDataSource.QUERY_LANGUAGE;
    }

    @Override
    public JRDataSource getJRDataSource() {
        return new JREmptyDataSource();
    }

    @Override
    public boolean isJDBCConnection() {
        return true;
    }

    @Override
    public boolean isJRDataSource() {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public HashMap getProperties() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(SERVICE_ACCOUNT_ID, getServiceAccountId());
        map.put(PRIVATE_KEY_FILE_PATH, getPrivateKeyFilePath());
        map.put(PROJECT_ID, getProjectId());
        return map;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void loadProperties(HashMap map) {
        setServiceAccountId(String.valueOf(map.get(SERVICE_ACCOUNT_ID)));
        setPrivateKeyFilePath(String.valueOf(map.get(PRIVATE_KEY_FILE_PATH)));
        setProjectId(String.valueOf(map.get(PROJECT_ID)));
    }

    @Override
    public void test() throws Exception {
        com.jaspersoft.bigquery.connection.BigQueryConnection connection = null;
        String errorMessage = null;
        try {
            connection = createConnection();
            if (connection != null) {
                JOptionPane.showMessageDialog(Misc.getMainWindow(), connection.test(), "",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            errorMessage = "A connection could not be created. Please review the IDE log";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = String.valueOf(e) + "\nVerify IDE log";
        } finally {
            if (connection != null) {
                Installer.getConnectionManager().returnConnection(connection);
            }
        }
        JOptionPane.showMessageDialog(Misc.getMainWindow(), "Error: " + String.valueOf(errorMessage), "",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public String designQuery(String query) {
        return query;
    }

    @Override
    public List<JRDesignField> readFields(String query) throws Exception {
        BigQueryFieldsProvider provider = new BigQueryFieldsProvider();
        List<JRDesignField> result = new ArrayList<JRDesignField>();
        JRDesignDataset dataset = new JRDesignDataset(true);
        JRDesignQuery designQuery = new JRDesignQuery();
        designQuery.setLanguage(BigQueryDataSource.QUERY_LANGUAGE);
        designQuery.setText(query);
        dataset.setQuery(designQuery);
        Map<String, String> parameters = new HashMap<String, String>();
        JRField[] fields = provider.getFields(this, dataset, parameters);
        for (int i = 0; i < fields.length; ++i) {
            result.add((JRDesignField) fields[i]);
        }
        return result;
    }

    @Override
    public boolean supportsDesign() {
        return false;
    }
}
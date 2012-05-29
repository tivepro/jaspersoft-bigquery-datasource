package com.jaspersoft.ireport.bigquery.connection;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openide.util.NbBundle;

import com.jaspersoft.ireport.designer.IReportConnection;
import com.jaspersoft.ireport.designer.IReportConnectionEditor;
import com.jaspersoft.ireport.designer.utils.Misc;

/**
 * 
 * @author Eric Diaz
 * @modified Matthew Dahlman
 * 
 */
@SuppressWarnings("serial")
public class BigQueryConnectionEditor extends JPanel implements IReportConnectionEditor {
    private JTextField serviceAccountIdField;

    private JFileChooser privateFileChooser;

    private String privateFileKeyPath;

    private JTextField projectIdField;

    private JButton fileButton;

    private final String DEFAULT_SERVICE_ACCOUNT_ID = "XXXXXX@developer.gserviceaccount.com";

    private final String DEFAULT_PROJECT_ID = "12345";

    public BigQueryConnectionEditor() {
        initComponents();
    }

    private void initComponents() {
        JPanel propertiesPanel = new JPanel(new GridBagLayout());
        addLabel(propertiesPanel, "privateKeyFilePath", 0, 0, 0.0, 1.0);
        addComponent(propertiesPanel, fileButton = new JButton("Choose P12 private file"), 1, 0, 1.0, 1.0, 20,
                GridBagConstraints.HORIZONTAL);
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int returnValue = privateFileChooser.showOpenDialog(fileButton);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    privateFileKeyPath = privateFileChooser.getSelectedFile().getAbsolutePath();
                }
            }
        });
        privateFileChooser = new JFileChooser();
        privateFileChooser.setMultiSelectionEnabled(false);
        privateFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        addLabel(propertiesPanel, "serviceAccountId", 0, 1, 0.0, 0.0);
        addComponent(propertiesPanel, serviceAccountIdField = new JTextField(DEFAULT_SERVICE_ACCOUNT_ID), 1, 1, 0.5,
                0.0, 10, GridBagConstraints.HORIZONTAL);
        addLabel(propertiesPanel, "projectId", 0, 2, 0.0, 0.0);
        addComponent(propertiesPanel, projectIdField = new JTextField(DEFAULT_PROJECT_ID), 1, 2, 0.5, 0.0, 10,
                GridBagConstraints.HORIZONTAL);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        setLayout(new GridBagLayout());
        add(propertiesPanel, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        JPanel panel = new JPanel();
        add(panel, constraints);

    }

    private void addLabel(JPanel panel, String fieldName, int gridx, int gridy, double weightx, double weighty) {
        JLabel label = new JLabel(NbBundle.getMessage(BigQueryConnection.class, fieldName));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(5, 5, 2, 5);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = weightx;
        constraints.weighty = weighty;
        panel.add(label, constraints);
    }

    private void addComponent(JPanel panel, JComponent component, int gridx, int gridy, double weightx, double weighty,
            int ipadx, int fill) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.fill = fill;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(2, 5, 5, 5);
        constraints.ipadx = ipadx;
        constraints.weightx = weightx;
        constraints.weighty = weighty;
        panel.add(component, constraints);
    }

    @Override
    public IReportConnection getIReportConnection() {
        if (privateFileKeyPath == null) {
            JOptionPane.showMessageDialog(Misc.getMainWindow(), "Error: No P12 private key file specified", "",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
        BigQueryConnection connection = new BigQueryConnection();
        connection.setServiceAccountId(serviceAccountIdField.getText().trim());
        connection.setPrivateKeyFilePath(privateFileKeyPath);
        connection.setProjectId(projectIdField.getText().trim());
        return connection;
    }

    @Override
    public void setIReportConnection(IReportConnection connection) {
        if (connection instanceof BigQueryConnection) {
            BigQueryConnection bigQueryConnection = (BigQueryConnection) connection;
            serviceAccountIdField.setText(bigQueryConnection.getServiceAccountId());
            privateFileChooser
                    .setSelectedFile(new File(privateFileKeyPath = bigQueryConnection.getPrivateKeyFilePath()));
            projectIdField.setText(bigQueryConnection.getProjectId());
        }
    }
}
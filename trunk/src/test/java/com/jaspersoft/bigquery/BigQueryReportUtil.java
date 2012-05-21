package com.jaspersoft.bigquery;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;

import com.jaspersoft.bigquery.connection.BigQueryConnection;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryReportUtil {
    private final static String PATH = "src/test/resources/";

    public void runReport(String reportName) throws Throwable {
        long start = System.currentTimeMillis();
        // Preparing parameters
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ReportTitle", "Test Report");

        BigQueryConnection connection = null;
        FileInputStream fileInputStream = null;
        String jasperFileName = PATH + reportName + ".jasper";
        String jasperPrinFiletName = PATH + reportName + ".jrprint";
        try {

            Properties properties = new Properties();
            properties.load(fileInputStream = new FileInputStream("src/test/resources/connection.properties"));
            connection = new BigQueryConnection(properties.getProperty("serviceAccountId"),
                    properties.getProperty("privateKeyFilePath"), properties.getProperty("projectId"));
            File jasperFile = new File(jasperFileName);
            if (jasperFile.exists()) {
                jasperFile.delete();
            }
            File jasperPrintFile = new File(jasperPrinFiletName);
            if (jasperPrintFile.exists()) {
                jasperPrintFile.delete();
            }
            JasperCompileManager.compileReportToFile(PATH + reportName + ".jrxml", jasperFileName);
            JasperFillManager.fillReportToFile(jasperFileName, parameters, connection);
            start = System.currentTimeMillis();
            JasperExportManager.exportReportToPdfFile(jasperPrinFiletName);
            System.err.println("PDF creation time : " + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
        System.err.println("Filling time : " + (System.currentTimeMillis() - start));
    }
}
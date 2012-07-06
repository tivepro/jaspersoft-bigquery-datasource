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
package com.jaspersoft.bigquery;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.fill.JRBaseFiller;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.fill.JRFillParameter;

import org.junit.Test;

import com.jaspersoft.bigquery.connection.BigQueryConnection;
import com.jaspersoft.bigquery.query.BigQueryQueryExecuter;

/**
 * 
 * @author Eric Diaz
 * Extra comment to test svn checkin
 */
public class ConnectionTest {

    class JRTestValue extends JRFillParameter {
        private Object value;

        public JRTestValue(JRDesignParameter parameter, Object value) {
            super(parameter, new JRFillObjectFactory((JRBaseFiller) null, null));
            this.value = value;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    @Test
    public void test() throws Exception {
        BigQueryConnection connection = null;
        FileInputStream inputStream = null;
        BigQueryQueryExecuter queryExecuter = null;
        Map<String, JRValueParameter> parameters = new HashMap<String, JRValueParameter>();
        try {
            Properties properties = new Properties();
            properties.load(inputStream = new FileInputStream("src/test/resources/connection.properties"));
            connection = new BigQueryConnection(properties.getProperty("serviceAccountId"),
                    properties.getProperty("privateKeyFilePath"), properties.getProperty("projectId"));
            System.out.println(connection.test());
            Assert.assertNotNull(connection.test());
            JRDesignDataset dataset = new JRDesignDataset(true);
            JRDesignQuery query = new JRDesignQuery();
            query.setText("SELECT year, month, day, state, is_male, weight_pounds, mother_residence_state, mother_age, cigarette_use, cigarettes_per_day\n"
                    + "FROM [publicdata:samples.natality]\n"
                    + "WHERE\n"
                    + "    year = 2008\n"
                    + "    and month > 11\n"
                    + "ORDER BY\n" + "    mother_age");
            dataset.setQuery(query);
            JRDesignParameter parameter = new JRDesignParameter();
            parameter.setName(JRParameter.REPORT_CONNECTION);
            Map<String, Object> connectionMap = new HashMap<String, Object>();
            connectionMap.put(JRParameter.REPORT_CONNECTION, connection);
            parameters.put(JRParameter.REPORT_PARAMETERS_MAP, new JRTestValue(parameter, connectionMap));
            queryExecuter = new BigQueryQueryExecuter(dataset, parameters);
            BigQueryDataSource dataSource = (BigQueryDataSource) queryExecuter.createDatasource();
            JRDesignField field = new JRDesignField();
            field.setName("cigarette_use");
            int count = 0;
            while (dataSource.next()) {
                //System.out.println(dataSource.getFieldValue(field));
                count++;
            }
            System.out.println("Count: " + count);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
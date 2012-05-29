package com.jaspersoft.bigquery;

import org.junit.Test;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class BigQuerySimpleTest {

    @Test
    public void test() throws Throwable {
        BigQueryReportUtil reportUtil = new BigQueryReportUtil();
        reportUtil.runReport("BigQueryReport");
    }
}
package com.jaspersoft.bigquery.query;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;

import org.apache.log4j.Logger;

import com.google.api.services.bigquery.Bigquery.Jobs.Insert;
import com.google.api.services.bigquery.model.ErrorProto;
import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.JobConfiguration;
import com.google.api.services.bigquery.model.JobConfigurationQuery;
import com.google.api.services.bigquery.model.JobReference;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableRow.F;
import com.google.api.services.bigquery.model.TableSchema;
import com.jaspersoft.bigquery.connection.BigQueryConnection;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryQueryWrapper {
    private final static Logger logger = Logger.getLogger(BigQueryQueryWrapper.class);

    public static final byte[] EMPTY_ARRAY = new byte[0];

    public BigQueryConnection connection;

    private Iterator<TableRow> rowsIterator;

    private List<List<TableRow>> results;

    public Map<String, Integer> fieldsMap;

    public int resultsIndex = -1;

    public TableSchema schema;

    public BigQueryQueryWrapper(BigQueryConnection connection, String queryString) throws JRException {
        if (queryString == null) {
            throw new JRException("No query string");
        }
        this.connection = connection;
        createJob(queryString);
    }

    private void createJob(String queryString) {
        if (logger.isDebugEnabled()) {
            logger.debug("queryString: " + queryString);
        }

        Job job = new Job();
        JobConfiguration jobConfiguration = new JobConfiguration();
        JobConfigurationQuery queryConfiguration = new JobConfigurationQuery();
        jobConfiguration.setQuery(queryConfiguration);

        job.setConfiguration(jobConfiguration);
        queryConfiguration.setQuery(queryString);
        Job pollJob;
        boolean success = false;
        try {
            Insert insert = connection.getBigquery().jobs().insert(connection.getProjectId(), job);
            JobReference jobId = insert.execute().getJobReference();
            if (logger.isDebugEnabled()) {
                logger.debug("Job ID of Query Job is: " + jobId.getJobId());
            }
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0;
            while (true) {
                pollJob = connection.getBigquery().jobs().get(connection.getProjectId(), jobId.getJobId()).execute();
                elapsedTime = System.currentTimeMillis() - startTime;
                if (logger.isDebugEnabled()) {
                    logger.debug("Job status (" + elapsedTime + "ms) " + jobId.getJobId() + ": "
                            + pollJob.getStatus().getState());
                }
                if (pollJob.getStatus().getState().equals("DONE")) {
                    List<ErrorProto> errors = pollJob.getStatus().getErrors();
                    if (errors != null) {
                        for (ErrorProto error : errors) {
                            logger.error(error.getMessage());
                        }
                    }
                    success = true;
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    success = false;
                    break;
                }
            }
            if (success) {
                BigInteger currentStartIndex = BigInteger.valueOf(0);
                GetQueryResultsResponse queryResult = getQueryResults(currentStartIndex, pollJob);
                schema = queryResult.getSchema();
                processTableSchema();
                BigInteger totalRows = queryResult.getTotalRows();
                results = new ArrayList<List<TableRow>>();
                List<TableRow> rows = queryResult.getRows();
                while (rows != null) {
                    results.add(rows);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Current index: " + currentStartIndex.toString());
                    }
                    BigInteger newIndex = currentStartIndex.add(BigInteger.valueOf(rows.size()));
                    if (logger.isDebugEnabled()) {
                        logger.debug("Next index: " + newIndex.toString());
                    }
                    rows = null;
                    if (!totalRows.subtract(newIndex).equals(BigInteger.ZERO)) {
                        queryResult = getQueryResults(currentStartIndex = newIndex, pollJob);
                        rows = queryResult.getRows();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public List<F> next() {
        List<F> currentRow = getNext();
        if (currentRow != null) {
            return currentRow;
        }
        rowsIterator = null;
        if (results != null && results.size() > 0) {
            resultsIndex++;
            if (resultsIndex == results.size()) {
                return null;
            }
            rowsIterator = results.get(resultsIndex).iterator();
        }
        return getNext();
    }

    private List<F> getNext() {
        if (rowsIterator == null) {
            return null;
        }
        if (rowsIterator.hasNext()) {
            return rowsIterator.next().getF();
        }
        return null;
    }

    private GetQueryResultsResponse getQueryResults(BigInteger currentStartIndex, Job pollJob) throws IOException {
        return connection.getBigquery().jobs()
                .getQueryResults(connection.getProjectId(), pollJob.getJobReference().getJobId())
                .setStartIndex(currentStartIndex).execute();
    }

    private void processTableSchema() {
        fieldsMap = new HashMap<String, Integer>();
        List<TableFieldSchema> fields = schema.getFields();
        for (int index = 0; index < fields.size(); index++) {
            fieldsMap.put(fields.get(index).getName(), index);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Fields Map: " + fieldsMap);
        }
    }

    public void close() {

    }
}
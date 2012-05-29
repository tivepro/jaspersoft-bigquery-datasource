package com.jaspersoft.bigquery.query;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactory;

/**
 * Query executer factory for HBase queries. <br/>
 * This factory creates {@link BigQueryQueryExecuter}
 * 
 * @author Eric Diaz
 * 
 */
public class BigQueryQueryExecuterFactory implements JRQueryExecuterFactory {
	/**
	 * Creates an {@link BigQueryQueryExecuter}.<br/>
	 * <br/>
	 * This method is called at fill time for reports/datasets having a query
	 * supported by this factory (HBaseQuery).
	 */
	public JRQueryExecuter createQueryExecuter(JRDataset dataset, Map<String, ? extends JRValueParameter> parameters)
			throws JRException {
		return new BigQueryQueryExecuter(dataset, parameters);
	};

	/**
	 * Method not implemented
	 */
	@Override
	public Object[] getBuiltinParameters() {
		return null;
	}

	@Override
	public boolean supportsQueryParameterType(String queryParameterType) {
		return true;
	}
}
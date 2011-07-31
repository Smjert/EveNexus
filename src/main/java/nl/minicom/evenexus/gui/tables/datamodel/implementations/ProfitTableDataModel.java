package nl.minicom.evenexus.gui.tables.datamodel.implementations;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import nl.minicom.evenexus.gui.tables.datamodel.IPeriodFilter;
import nl.minicom.evenexus.gui.tables.datamodel.ITableDataModel;
import nl.minicom.evenexus.gui.tables.datamodel.ITypeNameFilter;
import nl.minicom.evenexus.gui.utils.dialogs.BugReportDialog;
import nl.minicom.evenexus.persistence.Database;
import nl.minicom.evenexus.utils.SettingsManager;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProfitTableDataModel implements ITableDataModel, ITypeNameFilter, IPeriodFilter {

	private final static Logger LOG = LoggerFactory.getLogger(ProfitTableDataModel.class);

	private final Database database;
	private final BugReportDialog dialog;
	
	private int period;
	private String typeName;
	
	@Inject
	public ProfitTableDataModel(SettingsManager settingsManager, Database database, BugReportDialog dialog) {
		this.database = database;
		this.dialog = dialog;
		
		setTypeName(null);
		setPeriod(settingsManager.loadInt(SettingsManager.FILTER_PROFIT_PERIOD, IPeriodFilter.WEEK));
	}
	
	@Override
	public List<Object[]> reload() {
		try {
			return loadTable();			
		}
		catch (HibernateException e) {
			LOG.error(e.getLocalizedMessage(), e);
			dialog.setVisible(true);
			throw e;
		}
	}

	private List<Object[]> loadTable() throws HibernateException {
		final String sql = new StringBuilder()
		.append("SELECT ")
		.append("typeName, ")
		.append("date, ")
		.append("quantity, ")
		.append("value, ")
		.append("taxes, ")
		.append("value + taxes AS profit, ")
		.append("value * quantity AS totalValue, ")
		.append("taxes * quantity AS totalTaxes, ")
		.append("(value + taxes) * quantity AS totalProfit ")
		.append("FROM profit ")
		.append("WHERE LCASE(typeName) LIKE ? ")
		.append("AND date > DATEADD('DAY', ?, CURRENT_TIMESTAMP()) ")
		.append("ORDER BY date DESC, sellTransactionId DESC")
		.toString();
		
		List<Object[]> result = new ArrayList<Object[]>();
		Session session = database.getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setString(0, typeName);
		query.setLong(1, period * -1);
		ScrollableResults resultSet = query.scroll();
		if (resultSet.first()) {
			do {
				result.add(resultSet.get().clone());
			}
			while (resultSet.next());
		}
		return result;
	}

	@Override
	public String[] getFields() {
		return new String[] {"typeName", "date", "quantity", "value", "taxes", "profit", "totalValue", "totalTaxes", "totalProfit"};
	}

	@Override
	public void setTypeName(String name) {
		if (name == null || name.isEmpty()) {
			typeName = "%";
			return;
		}
		typeName = "%" + name.toLowerCase() + "%";
	}

	@Override
	public void setPeriod(int days) {
		period = days;
	}

}

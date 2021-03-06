package nl.minicom.evenexus.core.report.definition;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import nl.minicom.evenexus.core.report.definition.components.GroupTranslator;
import nl.minicom.evenexus.core.report.definition.components.ReportFilter;
import nl.minicom.evenexus.core.report.definition.components.ReportGroup;
import nl.minicom.evenexus.core.report.definition.components.ReportItem;
import nl.minicom.evenexus.core.report.definition.components.utils.Aggregate;
import nl.minicom.evenexus.core.report.persistence.expressions.Column;
import nl.minicom.evenexus.core.report.persistence.expressions.Concat;
import nl.minicom.evenexus.core.report.persistence.expressions.DayOfYear;
import nl.minicom.evenexus.core.report.persistence.expressions.GreaterThan;
import nl.minicom.evenexus.core.report.persistence.expressions.Month;
import nl.minicom.evenexus.core.report.persistence.expressions.Or;
import nl.minicom.evenexus.core.report.persistence.expressions.SmallerThan;
import nl.minicom.evenexus.core.report.persistence.expressions.Table;
import nl.minicom.evenexus.core.report.persistence.expressions.Value;
import nl.minicom.evenexus.core.report.persistence.expressions.Week;
import nl.minicom.evenexus.core.report.persistence.expressions.Year;
import nl.minicom.evenexus.persistence.dao.Profit;
import nl.minicom.evenexus.persistence.dao.WalletJournal;
import nl.minicom.evenexus.persistence.dao.WalletTransaction;

/**
 * This class contains the following definitions.
 * <ul>
 * 	<li>{@link ReportFilter} objects</li>
 * 	<li>{@link ReportItem} objects</li>
 * 	<li>{@link ReportGroup} objects</li>
 * </ul>
 *
 * @author Michael
 */
public class ReportDefinition {
	
	// Filter keys
	public static final String FILTER_CHARACTER = "report_filter_character";
	public static final String FILTER_START_DATE = "report_filter_dates_start";
	public static final String FILTER_END_DATE = "report_filter_dates_end";
	public static final String FILTER_ITEM = "report_filter_item";

	// Item keys
	public static final String ITEM_ITEMS_BOUGHT = "report_item_items_bought";
	public static final String ITEM_ITEMS_SOLD = "report_item_items_sold";
	
	// Group keys
	public static final String GROUP_DAY = "report_group_day";
	public static final String GROUP_WEEK = "report_group_week";
	public static final String GROUP_MONTH = "report_group_month";
	public static final String GROUP_ITEM = "report_group_item";

	// Defined items & groups
	private final Map<String, ReportFilter> reportFilters = new LinkedHashMap<String, ReportFilter>();
	private final Map<String, ReportItem> reportItems = new LinkedHashMap<String, ReportItem>();
	private final Map<String, ReportGroup> reportGroups = new LinkedHashMap<String, ReportGroup>();
	
	/**
	 * This constructor returns a definition of all usuable.
	 * <ul>
	 * 	<li>{@link ReportFilter} objects</li>
	 * 	<li>{@link ReportItem} objects</li>
	 * 	<li>{@link ReportGroup} objects</li>
	 * </ul>
	 */
	public ReportDefinition() {

		addFilter(
				new ReportFilter(FILTER_CHARACTER)
					.defineExpression(Table.TRANSACTIONS, new Column(WalletTransaction.CHARACTER_ID))
					.defineExpression(Table.JOURNAL, new Or(
							new Column(WalletJournal.OWNER_ID_1), 
							new Column(WalletJournal.OWNER_ID_2)
					))
					// TODO: Add definition for profit table.
		);

		addFilter(
				new ReportFilter(FILTER_START_DATE)
					.defineExpression(Table.TRANSACTIONS, new Column(WalletTransaction.TRANSACTION_DATE_TIME))
					.defineExpression(Table.JOURNAL, new Column(WalletJournal.DATE))
					.defineExpression(Table.PROFIT, new Column(Profit.DATE))
		);
		
		addFilter(
				new ReportFilter(FILTER_END_DATE)
					.defineExpression(Table.TRANSACTIONS, new Column(WalletTransaction.TRANSACTION_DATE_TIME))
					.defineExpression(Table.JOURNAL, new Column(WalletJournal.DATE))
					.defineExpression(Table.PROFIT, new Column(Profit.DATE))
		);

		addFilter(
				new ReportFilter(FILTER_ITEM)
					.defineExpression(Table.TRANSACTIONS, new Column(WalletTransaction.TYPE_ID))
					.defineExpression(Table.PROFIT, new Column(Profit.TYPE_ID))
		);
		
		addItem(
				new ReportItem(
						ITEM_ITEMS_BOUGHT,
						Table.TRANSACTIONS, 
						Aggregate.SUM, 
						new Column(WalletTransaction.QUANTITY), 
						new SmallerThan(
								new Column(WalletTransaction.PRICE), 
								new Value(0)
						)
				)
		);
		
		addItem(
				new ReportItem(
						ITEM_ITEMS_SOLD,
						Table.TRANSACTIONS, 
						Aggregate.SUM, 
						new Column(WalletTransaction.QUANTITY), 
						new GreaterThan(
								new Column(WalletTransaction.PRICE), 
								new Value(0)
						)
				)
		);
		
		addGroup(
				new ReportGroup(
						GROUP_DAY, 
						new GroupTranslator() {
								@Override
								public String translate(String input) {
									String[] split = input.split("-");
									Calendar c = GregorianCalendar.getInstance();
									c.set(Calendar.YEAR, Integer.parseInt(split[0]));
									c.set(Calendar.DAY_OF_YEAR, Integer.parseInt(split[1]));
									return new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(c.getTime());
								}
						}
				)
				.defineExpression(
						Table.TRANSACTIONS, 
						new Concat(
								new Year(new Column(WalletTransaction.TRANSACTION_DATE_TIME)),
								new Value("-"),
								new DayOfYear(new Column(WalletTransaction.TRANSACTION_DATE_TIME))
						)
				)
		);
		
		addGroup(
				new ReportGroup(
						GROUP_ITEM
				)
				.defineExpression(
						Table.TRANSACTIONS, 
						new Column(WalletTransaction.TYPE_ID)
				)
		);
		
		addGroup(
				new ReportGroup(
						GROUP_WEEK, 
						new GroupTranslator() {
								@Override
								public String translate(String input) {
									String[] split = input.split("-");
									Calendar c = GregorianCalendar.getInstance();
									c.set(Calendar.YEAR, Integer.parseInt(split[0]));
									c.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(split[1]));
									return new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(c.getTime());
								}
						}
				)
				.defineExpression(
						Table.TRANSACTIONS, 
						new Concat(
								new Year(new Column(WalletTransaction.TRANSACTION_DATE_TIME)),
								new Value("-"),
								new Week(new Column(WalletTransaction.TRANSACTION_DATE_TIME))
						)
				)
		);
		
		addGroup(
				new ReportGroup(
						GROUP_MONTH, 
						new GroupTranslator() {
								@Override
								public String translate(String input) {
									String[] split = input.split("-");
									Calendar c = GregorianCalendar.getInstance();
									c.set(Calendar.YEAR, Integer.parseInt(split[0]));
									c.set(Calendar.MONTH, Integer.parseInt(split[1]));
									return new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(c.getTime());
								}
						}
				)
				.defineExpression(
						Table.TRANSACTIONS, 
						new Concat(
								new Year(new Column(WalletTransaction.TRANSACTION_DATE_TIME)),
								new Value("-"),
								new Month(new Column(WalletTransaction.TRANSACTION_DATE_TIME))
						)
				)
		);
	}
	
	private void addFilter(ReportFilter filter) {
		reportFilters.put(filter.getKey(), filter);
	}
	
	private void addItem(ReportItem item) {
		reportItems.put(item.getKey(), item);
	}
	
	private void addGroup(ReportGroup group) {
		reportGroups.put(group.getKey(), group);
	}

	/**
	 * @return 
	 * 		A {@link Collection} of {@link ReportFilter} objects which are defined.
	 */
	public Collection<ReportFilter> getFilters() {
		return Collections.unmodifiableCollection(reportFilters.values());
	}

	/**
	 * @return 
	 * 		A {@link Collection} of {@link ReportItem} objects which are defined.
	 */
	public Collection<ReportItem> getItems() {
		return Collections.unmodifiableCollection(reportItems.values());
	}

	/**
	 * @return 
	 * 		A {@link Collection} of {@link ReportGroup} objects which are defined.
	 */
	public Collection<ReportGroup> getGroups() {
		return Collections.unmodifiableCollection(reportGroups.values());
	}

	/**
	 * Returns a {@link ReportFilter} with the provided filterAlias.
	 * 
	 * @param filterAlias
	 * 		The alias of the {@link ReportFilter} to return.
	 * 
	 * @return
	 * 		The {@link ReportFilter} with the provided filterAlias.
	 */
	public ReportFilter getFilter(String filterAlias) {
		return reportFilters.get(filterAlias);
	}

	/**
	 * Returns a {@link ReportItem} with the provided itemAlias.
	 * 
	 * @param itemAlias
	 * 		The alias of the {@link ReportItem} to return.
	 * 
	 * @return
	 * 		The {@link ReportItem} with the provided itemAlias.
	 */
	public ReportItem getItem(String itemAlias) {
		return reportItems.get(itemAlias);
	}

	/**
	 * Returns a {@link ReportGroup} with the provided groupAlias.
	 * 
	 * @param groupAlias	
	 * 		The alias of the {@link ReportGroup} to return.
	 * 
	 * @return				
	 * 		The {@link ReportGroup} with the provided groupAlias.
	 */
	public ReportGroup getGroup(String groupAlias) {
		return reportGroups.get(groupAlias);
	}
	
}

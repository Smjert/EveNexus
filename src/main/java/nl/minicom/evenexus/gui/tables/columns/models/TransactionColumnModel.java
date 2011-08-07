package nl.minicom.evenexus.gui.tables.columns.models;


import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.inject.Inject;

import nl.minicom.evenexus.gui.tables.columns.Column;
import nl.minicom.evenexus.gui.tables.columns.ColumnModel;
import nl.minicom.evenexus.utils.SettingsManager;

public class TransactionColumnModel extends ColumnModel {

	@Inject
	public TransactionColumnModel(SettingsManager settingsManager) {
		super(settingsManager);
	}
	
	@Override
	public void initialize() {
		add(new Column(
				getSettingsManager(),
				"Date", 
				"transactionDateTime",
				SettingsManager.TABLE_TRANSACTION_DATE_VISIBLE,
				true,
				SettingsManager.TABLE_TRANSACTION_DATE_WIDTH,
				138, 
				Timestamp.class, 
				ColumnModel.DATE_TIME
		));
		
		add(new Column(
				getSettingsManager(),
				"Item", 
				"typename", 
				SettingsManager.TABLE_TRANSACTION_ITEM_VISIBLE,
				true,
				SettingsManager.TABLE_TRANSACTION_ITEM_WIDTH,
				302,
				String.class
		));
		
		add(new Column(
				getSettingsManager(),
				"Station", 
				"stationname", 
				SettingsManager.TABLE_TRANSACTION_STATION_VISIBLE,
				false,
				SettingsManager.TABLE_TRANSACTION_STATION_WIDTH,
				300,
				String.class
		));
		
		add(new Column(
				getSettingsManager(),
				"Client", 
				"clientName", 
				SettingsManager.TABLE_TRANSACTION_CLIENT_VISIBLE,
				false,
				SettingsManager.TABLE_TRANSACTION_CLIENT_WIDTH,
				200,
				String.class
		));
		
		add(new Column(
				getSettingsManager(),
				"Quantity", 
				"quantity", 
				SettingsManager.TABLE_TRANSACTION_QUANTITY_VISIBLE,
				true,
				SettingsManager.TABLE_TRANSACTION_QUANTITY_WIDTH,
				80, 
				Long.class, 
				ColumnModel.INTEGER
		));
		
		add(new Column(
				getSettingsManager(),
				"Price", 
				"price", 
				SettingsManager.TABLE_TRANSACTION_PRICE_VISIBLE,
				true,
				SettingsManager.TABLE_TRANSACTION_PRICE_WIDTH,
				132, 
				BigDecimal.class, 
				ColumnModel.CURRENCY
		));
		
		add(new Column(
				getSettingsManager(),
				"Taxes", 
				"totaltax", 
				SettingsManager.TABLE_TRANSACTION_TAXES_VISIBLE,
				false,
				SettingsManager.TABLE_TRANSACTION_TAXES_WIDTH,
				100, 
				BigDecimal.class, 
				ColumnModel.CURRENCY
		));
		
		add(new Column(
				getSettingsManager(),
				"Total (exc. taxes)", 
				"totalnotax", 
				SettingsManager.TABLE_TRANSACTION_TOTAL_WITHOUT_TAXES_VISIBLE,
				false,
				SettingsManager.TABLE_TRANSACTION_TOTAL_WITHOUT_TAXES_WIDTH,
				100, 
				BigDecimal.class, 
				ColumnModel.CURRENCY
		));
		
		add(new Column(
				getSettingsManager(),
				"Total (inc. taxes)", 
				"totalwithtax", 
				SettingsManager.TABLE_TRANSACTION_TOTAL_WITH_TAXES_VISIBLE,
				true,
				SettingsManager.TABLE_TRANSACTION_TOTAL_WITH_TAXES_WIDTH,
				131, 
				BigDecimal.class, 
				ColumnModel.CURRENCY
		));
	}
	
}

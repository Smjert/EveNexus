package nl.minicom.evenexus.gui.panels.transactions;


import javax.inject.Inject;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.minicom.evenexus.eveapi.ApiParser.Api;
import nl.minicom.evenexus.eveapi.importers.ImportListener;
import nl.minicom.evenexus.eveapi.importers.ImportManager;
import nl.minicom.evenexus.gui.GuiConstants;
import nl.minicom.evenexus.gui.panels.TabPanel;
import nl.minicom.evenexus.gui.tables.Table;
import nl.minicom.evenexus.gui.tables.columns.TableColumnSelectionFrame;
import nl.minicom.evenexus.gui.tables.columns.models.TransactionColumnModel;
import nl.minicom.evenexus.gui.tables.datamodel.implementations.TransactionTableDataModel;
import nl.minicom.evenexus.gui.utils.toolbar.ToolBar;
import nl.minicom.evenexus.gui.utils.toolbar.ToolBarButton;
import nl.minicom.evenexus.utils.SettingsManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TransactionsPanel extends TabPanel implements ImportListener {

	private static final long serialVersionUID = -4187071888216622511L;	
	private static final Logger LOG = LoggerFactory.getLogger(TransactionsPanel.class);

	private final Table table;
	private final TransactionColumnModel columnModel;
	private final TransactionTableDataModel tableDataModel;
	private final SettingsManager settingsManager;
	
	@Inject
	public TransactionsPanel(
			ImportManager importManager,
			SettingsManager settingsManager,
			TransactionColumnModel columnModel, 
			TransactionTableDataModel tableDataModel,
			Table table) {
		
		this.table = table;
		this.tableDataModel = tableDataModel;
		this.columnModel = columnModel;
		this.settingsManager = settingsManager;
		
		importManager.addListener(Api.CHAR_WALLET_TRANSACTIONS, this);
	}
	
	/**
	 * This method initializes this {@link TransactionsPanel} object.
	 */
	public void initialize() {
		synchronized (this) {
			columnModel.initialize();
			tableDataModel.initialize();
			table.initialize(tableDataModel, columnModel);
			
			setBackground(GuiConstants.getTabBackground());
			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.getVerticalScrollBar().setUnitIncrement(16);
			ToolBar panel = createTopMenu();
			
			GroupLayout layout = new GroupLayout(this);
			setLayout(layout);        
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
					.addGap(7)
					.addGroup(layout.createParallelGroup(Alignment.TRAILING)
							.addComponent(panel)
							.addComponent(scrollPane)
							)
							.addGap(7)
					);
			layout.setVerticalGroup(
					layout.createSequentialGroup()
					.addGap(5)
					.addComponent(panel)
					.addGap(7)
					.addComponent(scrollPane)
					.addGap(7)
					);
		}
	}
	
	@Override
	public void onImportComplete() {
		synchronized (this) {
			reloadTab();
		}
	}

	protected void reloadContent() {
		table.reload();
		LOG.info("Transaction panel reloaded!");
	}

	private ToolBar createTopMenu() {
		ToolBar toolBar = new ToolBar(settingsManager);
		
		JPanel typeNameSearchField = toolBar.createTypeNameSearchField(table);
		JPanel periodSelectionField = toolBar.createPeriodField(table, SettingsManager.FILTER_TRANSACTION_PERIOD);
		TableColumnSelectionFrame columnSelectionFrame = new TableColumnSelectionFrame(table.getColumns(), table);
		ToolBarButton button = toolBar.createTableSelectColumnsButton(columnSelectionFrame);
		JPanel spacer = toolBar.createSpacer();
		
        GroupLayout layout = new GroupLayout(toolBar);
        toolBar.setLayout(layout);
        
		layout.setHorizontalGroup(
        	layout.createSequentialGroup()
			.addComponent(typeNameSearchField)
			.addGap(3)
			.addComponent(periodSelectionField)
			.addGap(3)
			.addComponent(spacer)
			.addGap(3)
			.addComponent(button)
    	);
    	layout.setVerticalGroup(
    		layout.createSequentialGroup()
    		.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(typeNameSearchField)
					.addComponent(periodSelectionField)
					.addComponent(spacer)
					.addComponent(button)
		        )
        	)
    	);
		
		return toolBar;
	}
	
}

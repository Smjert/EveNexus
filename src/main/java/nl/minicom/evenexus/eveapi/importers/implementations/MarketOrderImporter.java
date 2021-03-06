package nl.minicom.evenexus.eveapi.importers.implementations;


import java.math.BigDecimal;

import javax.inject.Inject;
import javax.inject.Provider;

import nl.minicom.evenexus.eveapi.ApiParser;
import nl.minicom.evenexus.eveapi.ApiParser.Api;
import nl.minicom.evenexus.eveapi.importers.ImportManager;
import nl.minicom.evenexus.eveapi.importers.ImporterTask;
import nl.minicom.evenexus.eveapi.importers.ImporterThread;
import nl.minicom.evenexus.gui.utils.dialogs.BugReportDialog;
import nl.minicom.evenexus.persistence.Database;
import nl.minicom.evenexus.persistence.dao.ApiKey;
import nl.minicom.evenexus.persistence.dao.MarketOrder;
import nl.minicom.evenexus.persistence.interceptor.Transactional;
import nl.minicom.evenexus.utils.TimeUtils;

import org.mortbay.xml.XmlParser.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MarketOrderImporter extends ImporterTask {
	
	private static final Logger LOG = LoggerFactory.getLogger(MarketOrderImporter.class);
	
	private final BugReportDialog dialog;
	
	private volatile boolean isReady = true;
	
	@Inject
	public MarketOrderImporter(
			Database database, 
			Provider<ApiParser> apiParserProvider, 
			Provider<ImporterThread> importerThreadProvider, 
			ImportManager importManager,
			BugReportDialog dialog) {
		
		super(database, apiParserProvider, importerThreadProvider, importManager, Api.CHAR_MARKET_ORDERS);
		this.dialog = dialog;
	}

	@Override
	public void parseApi(Node node, ApiKey apiKey) {
		synchronized (this) {
			isReady = false;
			final Node root = node.get("result").get("rowset");
			MarketOrder.markAllActiveAsExpired(apiKey.getCharacterId());
			for (int i = root.size() - 1; i >= 0; i--) {
				processRow(root, i);
			}
			isReady = true;
		}
	}

	private void processRow(Node root, int i) {
		if (root.get(i) instanceof Node) {
			Node row = (Node) root.get(i);
			if (row.getTag().equals("row")) {
				persistChangeData(row);
			}
		}
	}

	@Transactional
	void persistChangeData(Node row) {
		try {
			long orderId = Long.parseLong(row.getAttribute("orderID"));
			
			MarketOrder order = new MarketOrder();
			order.setOrderId(orderId);
			order.setCharacterId(Long.parseLong(row.getAttribute("charID")));
			order.setStationId(Long.parseLong(row.getAttribute("stationID")));
			order.setVolumeEntered(Long.parseLong(row.getAttribute("volEntered")));
			order.setVolumeRemaining(Long.parseLong(row.getAttribute("volRemaining")));
			order.setMinimumVolume(Long.parseLong(row.getAttribute("minVolume")));
			order.setOrderState(Integer.parseInt(row.getAttribute("orderState")));
			order.setTypeId(Long.parseLong(row.getAttribute("typeID")));
			order.setRange(Integer.parseInt(row.getAttribute("range")));
			order.setAccountKey(Integer.parseInt(row.getAttribute("accountKey")));
			order.setDuration(Integer.parseInt(row.getAttribute("duration")));
			order.setEscrow(BigDecimal.valueOf(Double.valueOf(row.getAttribute("escrow"))));
			order.setPrice(BigDecimal.valueOf(Double.valueOf(row.getAttribute("price"))));
			order.setBid("1".equals(row.getAttribute("bid")));
			order.setIssued(TimeUtils.convertToTimestamp(row.getAttribute("issued")));
			
			getDatabase().getCurrentSession().saveOrUpdate(order);
		} 
		catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
			dialog.setVisible(true);
		}
	}

	@Override
	public boolean isReady() {
		synchronized (this) {
			return isReady;
		}
	}

}

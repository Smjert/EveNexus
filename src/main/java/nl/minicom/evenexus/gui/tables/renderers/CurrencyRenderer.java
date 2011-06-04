package nl.minicom.evenexus.gui.tables.renderers;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.NumberFormatter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class CurrencyRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 3654076174730284327L;
	
	private static final Logger logger = LogManager.getRootLogger();
	
	private Color RED = new Color(200, 16, 16);
	private Color GREEN = new Color(0, 128, 0);
	private AbstractFormatter formatter = new NumberFormatter(new DecimalFormat("###,###,###,###,###,##0.00", DecimalFormatSymbols.getInstance(Locale.US)));

	public CurrencyRenderer() {
		super();
		setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
	}
		
	@Override
	public String getText() {
		return(" " + super.getText() + " ISK ");
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (!(value instanceof BigDecimal)) {
			return null;
		}
		
		BigDecimal decimalValue = (BigDecimal) value;
		if (decimalValue.signum() == -1) {
			c.setForeground(RED);
		}
		else {
			c.setForeground(GREEN);
		}
		
		try {
			setValue(formatter.valueToString(decimalValue));
		}
		catch (ParseException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return c;
	}
	
}

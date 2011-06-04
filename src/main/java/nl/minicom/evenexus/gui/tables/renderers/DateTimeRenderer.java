package nl.minicom.evenexus.gui.tables.renderers;


import java.awt.Component;
import java.sql.Timestamp;
import java.text.ParseException;

import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import nl.minicom.evenexus.gui.tables.formatters.DateTimeFormatter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class DateTimeRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -3869075201627613304L;

	private static final Logger logger = LogManager.getRootLogger();
	
	private AbstractFormatter formatter = new DateTimeFormatter();

	public DateTimeRenderer() {
		super();
		setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
	}

	@Override
	public String getText() {
		return(" " + super.getText() + " ");
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (!(value instanceof Timestamp)) {
			return null;
		}
		
		Timestamp timestamp = (Timestamp) value;
		try {
			setValue(formatter.valueToString(timestamp));
		}
		catch (ParseException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return c;
	}
	
}

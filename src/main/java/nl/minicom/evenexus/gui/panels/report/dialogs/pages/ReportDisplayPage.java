package nl.minicom.evenexus.gui.panels.report.dialogs.pages;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import nl.minicom.evenexus.core.report.engine.DisplayType;
import nl.minicom.evenexus.core.report.engine.Model;
import nl.minicom.evenexus.core.report.engine.ReportModel;
import nl.minicom.evenexus.core.report.engine.ReportModel.GroupListener;
import nl.minicom.evenexus.gui.GuiConstants;
import nl.minicom.evenexus.gui.icons.Icon;
import nl.minicom.evenexus.gui.utils.dialogs.titles.DialogTitle;
import nl.minicom.evenexus.gui.utils.dialogs.titles.ReportDisplayTitle;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * This class is responsible for selecting a representation form for a report.
 *
 * @author michael
 */
public class ReportDisplayPage extends ReportWizardPage implements DisplayEntryListener, GroupListener {

	private static final long serialVersionUID = 3066113966844699181L;

	private static final Color BORDER_SELECTED = new Color(0, 114, 186);
	private static final Color BORDER_HOVER = new Color(128, 184, 221);
	private static final Color BORDER_DEFAULT = new Color(255, 255, 255);
	
	private static final Color BACKGROUND_SELECTED = new Color(128, 184, 221);
	private static final Color BACKGROUND_HOVER = new Color(196, 214, 238);
	private static final Color BACKGROUND_DEFAULT = null;

	private final List<ReportDisplayEntry> entries;
	
	private ReportModel model;
	
	/**
	 * This constructs a new {@link ReportDisplayPage}.
	 * 
	 * @param model
	 * 		The {@link ReportModel}.
	 */
	@Inject
	public ReportDisplayPage(ReportModel model) {
		super(model);
		this.entries = Lists.newArrayList();
		this.model = model;
		
		model.addListener(this);
	}

	/**
	 * This method builds the gui, allowing the user to select a display type.
	 */
	@Override
	public void buildGui() {
		GroupLayout layout = new GroupLayout(this);
		Group horizontalGroup = layout.createParallelGroup();
		Group verticalGroup = layout.createSequentialGroup();
		
		for (DisplayType type : DisplayType.values()) {
			ReportDisplayEntry entry = new ReportDisplayEntry(this, type, model);
			horizontalGroup.addComponent(entry);
			verticalGroup.addComponent(entry);
			entries.add(entry);
		}		
		
		setLayout(layout);        
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(horizontalGroup));
    	layout.setVerticalGroup(verticalGroup);
	}

	@Override
	public DialogTitle getTitle() {
		return new ReportDisplayTitle();
	}
	
	/**
	 * The {@link ReportDisplayEntry} class is a gui panel which contains an
	 * image, which when clicked sets the {@link DisplayType} of the report.
	 * 
	 * @author michael
	 */
	public class ReportDisplayEntry extends JPanel {

		private static final long serialVersionUID = 1280615798706096140L;
		
		private final ReportModel model;
		private final DisplayType type;

		private final JPanel iconPanel;
		private final JLabel title;
		private final JLabel description;
		
		private State state;
		
		/**
		 * This constructs a new {@link ReportDisplayEntry}.
		 * 
		 * @param listener
		 * 		The {@link ReportPageListener}. 
		 * 
		 * @param type
		 * 		The {@link DisplayType} of this {@link ReportDisplayEntry}.
		 * 
		 * @param model
		 * 		The {@link ReportModel}.
		 */
		public ReportDisplayEntry(final DisplayEntryListener listener, DisplayType type, ReportModel model) {
			this.model = model;
			this.type = type;
			this.state = State.DEFAULT;
			
			this.iconPanel = createImageButton(type);
			this.title = GuiConstants.createBoldLabel(type.getTitle());
			this.description = createDescription(type.getDescription());
			
			setCursor(new Cursor(Cursor.HAND_CURSOR));
			
			GroupLayout layout = new GroupLayout(this);
			setLayout(layout);
			
			layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGap(4)
				.addComponent(iconPanel)
				.addGap(10)
				.addGroup(
					layout.createParallelGroup()
					.addComponent(title)
					.addComponent(description)
				)
				.addGap(4)
			);
			
			layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGap(4)
				.addGroup(layout.createParallelGroup()
						.addComponent(iconPanel)
						.addGroup(
							layout.createSequentialGroup()
							.addComponent(title)
							.addComponent(description)
						)
				)
				.addGap(4)
			);
			
			final ReportDisplayEntry entry = this;
			addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent arg0) {
					// Do nothing.
				}
				
				@Override
				public void mousePressed(MouseEvent arg0) {
					// Do nothin.
				}
				
				@Override
				public void mouseExited(MouseEvent arg0) {
					listener.onDeselectionMove(entry);
				}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {
					listener.onSelectionMove(entry);
				}
				
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (!entry.getState().equals(State.DISABLED)) {
						listener.onStateChange(entry);
					}
				}
			});
			
			Model<DisplayType> displayType = model.getDisplayType();
			if (displayType.isSet() && displayType.getValue().equals(type)) {
				setState(State.SELECTED);
			}
			else {
				setState(State.DEFAULT);
			}
		}
		
		private DisplayType getType() {
			return type;
		}
		
		private JLabel createDescription(String value) {
			JLabel label = new JLabel("<html>" + value + "</html>");
			label.setVerticalAlignment(JLabel.TOP);
			label.setMinimumSize(new Dimension(0, GuiConstants.TEXT_FIELD_HEIGHT));
			label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
			return label;
		}

		private JPanel createImageButton(final DisplayType type) {
			final JPanel panel = new JPanel();
			panel.setBackground(Color.WHITE);
			panel.setMaximumSize(new Dimension(64, 64));
			panel.setMinimumSize(new Dimension(64, 64));
			panel.setLayout(null);
			
			ImageIcon iconImage = Icon.getIcon(type.getIcon());
			final JLabel iconLabel = new JLabel(iconImage);
			iconLabel.setBounds(8, 8, 48, 48);
			panel.add(iconLabel);
			
			return panel;
		}
		
		private void setState(State newState) {
			this.state = newState;
			switch (newState) {
				case SELECTED:
					model.getDisplayType().setValue(getType());
					drawState(BACKGROUND_SELECTED, BORDER_SELECTED, Color.BLACK, Cursor.HAND_CURSOR);
					break;
				case HOVER:
					drawState(BACKGROUND_HOVER, BORDER_HOVER, Color.BLACK, Cursor.HAND_CURSOR);
					break;
				case DISABLED:
					drawState(BACKGROUND_DEFAULT, BORDER_DEFAULT, Color.GRAY, Cursor.DEFAULT_CURSOR);
					break;
				default: 
					drawState(BACKGROUND_DEFAULT, BORDER_DEFAULT, Color.BLACK, Cursor.HAND_CURSOR);
			}
		}

		private void drawState(Color background, Color border, Color text, int cursor) {
			setBackground(background);
			iconPanel.setBorder(new CompoundBorder(
					new LineBorder(Color.GRAY, 1),
					new LineBorder(border, 3)
			));
			title.setForeground(text);
			description.setForeground(text);
			setCursor(Cursor.getPredefinedCursor(cursor));
		}

		public State getState() {
			return state;
		}
	}
	
	/**
	 * This enum represents the state of the {@link ReportDisplayEntry}.
	 * 
	 * @author michael
	 */
	private enum State {
		DEFAULT,
		HOVER,
		SELECTED,
		DISABLED;
	}

	@Override
	public boolean allowPrevious() {
		return true;
	}

	@Override
	public boolean allowNext() {
		return false;
	}

	@Override
	public void onStateChange(ReportDisplayEntry selected) {
		for (ReportDisplayEntry entry : entries) {
			if (!entry.getState().equals(State.DISABLED)) {
				if (entry.equals(selected)) {
					entry.setState(State.SELECTED);
				}
				else {
					entry.setState(State.DEFAULT);
				}
			}
		}
	}

	@Override
	public void onSelectionMove(ReportDisplayEntry selected) {
		DisplayType type = model.getDisplayType().getValue();
		for (ReportDisplayEntry entry : entries) {
			if (!entry.getState().equals(State.DISABLED)) {
				if (type != null && type.equals(entry.getType())) {
					entry.setState(State.SELECTED);
				}
				else if (entry.equals(selected)) {
					entry.setState(State.HOVER);
				}
				else {
					entry.setState(State.DEFAULT);
				}
			}
		}
	}

	@Override
	public void onDeselectionMove(ReportDisplayEntry deselected) {
		DisplayType type = model.getDisplayType().getValue();
		for (ReportDisplayEntry entry : entries) {
			if (!entry.getState().equals(State.DISABLED)) {
				if (type != null && type.equals(entry.getType())) {
					entry.setState(State.SELECTED);
				}
				else if (!entry.getState().equals(State.DISABLED)) {
					entry.setState(State.DEFAULT);
				}
			}
		}
	}
	
	@Override
	public void removeListeners() {
		model.removeListener(this);
	}

	@Override
	public void onReportGroupsModified() {
		for (ReportDisplayEntry entry : entries) {
			DisplayType type = entry.getType();
			if (type != null && !type.supportsAll(model.getDisplayTypes())) {
				entry.setState(State.DISABLED);
			}
			else if (type != null && type.supportsAll(model.getDisplayTypes()) && entry.getState().equals(State.DISABLED)) {
				entry.setState(State.DEFAULT);
			}
		}
	}
	
}
package plg.gui.widgets.list;

import org.ocpsoft.prettytime.PrettyTime;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * This class describes the renderer of a {@link MultilineImageListEntry}.
 *
 * @author Andrea Burattin
 */
public class MultilineImageListEntryRenderer<E>
	extends JPanel
	implements ListCellRenderer<E> {
	
	private static final long serialVersionUID = -1863880605178129502L;
	private static Color DEFAULT_HIGHLIGHT_BG = new Color(184, 207, 229);
	private static Border DEFAULT_HIGHLIGHT_BORDER = BorderFactory.createEmptyBorder(7, 5, 7, 5);
	private static Border DEFAULT_BORDER = BorderFactory.createEmptyBorder(7, 5, 7, 5);
	
	private Color highlightBackground;
	private Border highlightBorder;
	private Border normalBorder;
	private JLabel labelIcon;
	private JPanel labelsPanel;
	private JLabel labelLine1;
	private JLabel labelLine2;
	private JLabel labelLine3;

	
	/**
	 * Default constructor
	 */
	public MultilineImageListEntryRenderer() {
		this(DEFAULT_HIGHLIGHT_BG, DEFAULT_HIGHLIGHT_BORDER, DEFAULT_BORDER);
	}
	
	/**
	 * Class constructor with some parameters to be set
	 * 
	 * @param highlightBackground
	 * @param highlightBorder
	 * @param normalBorder
	 */
	public MultilineImageListEntryRenderer(Color highlightBackground, Border highlightBorder, Border normalBorder) {

		this.labelsPanel = new JPanel();
		this.highlightBackground = highlightBackground;
		this.highlightBorder = highlightBorder;
		this.normalBorder = normalBorder;
		this.labelIcon = new JLabel();
		this.labelLine1 = new JLabel();
		this.labelLine2 = new JLabel();
		this.labelLine3 = new JLabel();
		
		setOpaque(true);
		setLayout(new BorderLayout());

		labelIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));

		add(labelIcon, BorderLayout.WEST);
		add(labelsPanel, BorderLayout.CENTER);

		labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
		labelsPanel.setOpaque(false);
		labelsPanel.add(labelLine1);
		labelsPanel.add(labelLine2);
		labelsPanel.add(labelLine3);

		labelLine2.setFont(getFont().deriveFont(Font.PLAIN));
		labelLine3.setFont(getFont().deriveFont(labelLine3.getFont().getSize() * 0.8f));
		labelLine1.setOpaque(false);
		labelLine2.setOpaque(false);
		labelLine3.setOpaque(false);

	}
	
	@Override
	public Component getListCellRendererComponent(
			JList<? extends E> list,
			E value,
			int index,
			boolean isSelected,
			boolean cellHasFocus) {

		MultilineImageListEntry entry = (MultilineImageListEntry) value;

		PrettyTime p = new PrettyTime();
		String dateDifference = p.format(entry.getCreationDate());
		String difference = String.valueOf(Duration.between(entry.getCreationDate(), Instant.now()));

		labelIcon.setIcon(entry.getIcon());

		labelLine1.setText("<html><b>" + entry.getFirstLine() + "</b></html>");
		labelLine2.setText(entry.getSecondLine());
		labelLine3.setText("Created " + dateDifference);

		if (isSelected) {
			setBorder(highlightBorder);
			setBackground(highlightBackground);
		} else {
			setBorder(normalBorder);
			setBackground(Color.WHITE);
		}
		
		return this;
	}
}

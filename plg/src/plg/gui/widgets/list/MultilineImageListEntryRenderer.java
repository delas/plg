package plg.gui.widgets.list;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import plg.gui.widgets.RoundedPanel;

/**
 * This class describes the renderer of a {@link MultilineImageListEntry}.
 *
 * @author Andrea Burattin
 */
public class MultilineImageListEntryRenderer<E>
	extends RoundedPanel
	implements ListCellRenderer<E> {
	
	private static final long serialVersionUID = -1863880605178129502L;
	private Color highlightBackground;
	private Border highlightBorder;
	private Border normalBorder;
	private JLabel text;
	
	/**
	 * Default constructor
	 */
	public MultilineImageListEntryRenderer() {
		this(Color.LIGHT_GRAY,
				BorderFactory.createEmptyBorder(3, 3, 3, 3),
				BorderFactory.createEmptyBorder(3, 3, 3, 3));
	}
	
	/**
	 * Class constructor with some parameters to be set
	 * 
	 * @param highlightBackground
	 * @param highlightBorder
	 * @param normalBorder
	 */
	public MultilineImageListEntryRenderer(Color highlightBackground, Border highlightBorder, Border normalBorder) {
		super(15);
		
		this.highlightBackground = highlightBackground;
		this.highlightBorder = highlightBorder;
		this.normalBorder = normalBorder;
		this.text = new JLabel();
		
		setOpaque(true);
		setLayout(new BorderLayout());
		add(text, BorderLayout.CENTER);
		
		text.setIconTextGap(5);
		text.setFont(getFont().deriveFont(Font.PLAIN));
		text.setOpaque(false);
	}
	
	@Override
	public Component getListCellRendererComponent(
			JList<? extends E> list,
			E value,
			int index,
			boolean isSelected,
			boolean cellHasFocus) {
		
		MultilineImageListEntry entry = (MultilineImageListEntry) value;
		text.setIcon(entry.getIcon());
		text.setText("<html><b>" + entry.getFirstLine() + "&nbsp;</b><br>" + entry.getSecondLine() + "&nbsp;</html>");
		
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

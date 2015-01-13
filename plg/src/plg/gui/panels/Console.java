package plg.gui.panels;

import java.awt.Color;
import java.awt.Dimension;

public class Console extends PlgPanels {

	private static final long serialVersionUID = -8707628278434660233L;
	private static final int MINIMUM_HEIGHT = 200;

	public Console() {
		setPreferredSize(new Dimension(0, MINIMUM_HEIGHT));
		setBackground(Color.black);
	}
}

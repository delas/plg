package plg.gui.panels;

import java.awt.Color;
import java.awt.Dimension;

import plg.gui.config.ConfigurationSet;

public class ProcessesList extends MainWindowPanel {

	private static final long serialVersionUID = 3733893133192755973L;
	protected static final int WIDTH = 200;

	public ProcessesList(ConfigurationSet conf) {
		super(conf);
		setPreferredSize(new Dimension(WIDTH, 0));
		setBackground(Color.yellow);
	}
}

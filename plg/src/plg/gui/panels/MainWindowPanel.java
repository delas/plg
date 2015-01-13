package plg.gui.panels;

import javax.swing.JPanel;

import plg.gui.config.ConfigurationSet;

public abstract class MainWindowPanel extends JPanel {

	private static final long serialVersionUID = -1656824607024522518L;
	protected ConfigurationSet conf;
	
	public MainWindowPanel(ConfigurationSet conf) {
		this.conf = conf;
	}
}

package plg.gui.panels;

import javax.swing.JPanel;

import plg.gui.config.ConfigurationSet;

/**
 * This abstract class must be derived by all the panels that wants to belong
 * to the main window.
 * 
 * @author Andrea Burattin
 */
public abstract class MainWindowPanel extends JPanel {

	private static final long serialVersionUID = -1656824607024522518L;
	protected ConfigurationSet conf;
	
	/**
	 * Class constructor
	 * 
	 * @param conf
	 */
	public MainWindowPanel(ConfigurationSet conf) {
		this.conf = conf;
	}
}

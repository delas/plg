package plg.gui.panels;

import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;

import plg.gui.config.ConfigurationSet;

/**
 * This panel identifies the area which stores all the application
 * {@link Progress} notification areas.
 * 
 * @author Andrea Burattin
 */
public class ProgressStack extends MainWindowPanel {

	private static final long serialVersionUID = 7106245291844959536L;
	private List<Progress> progresses = new LinkedList<Progress>();

	/**
	 * 
	 * @param conf
	 */
	public ProgressStack(ConfigurationSet conf) {
		super(conf);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}
	
	/**
	 * This method returns a new {@link Progress} which can be used to notify
	 * the user about ongoing long operations
	 * 
	 * @return a new progress
	 */
	public Progress askForNewProgress() {
		Progress p = new Progress();
		progresses.add(p);
		add(p);
		return p;
	}
}

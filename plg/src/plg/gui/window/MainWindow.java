package plg.gui.window;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import plg.gui.config.ConfigurationSet;
import plg.gui.controller.ApplicationController;
import plg.gui.panels.Console;
import plg.gui.panels.ProcessesList;
import plg.gui.panels.SingleProcessVisualizer;
import plg.gui.panels.ProgressStack;
import plg.gui.widgets.MainToolbar;

/**
 * This class contains the main window of PLG
 *
 * @author Andrea Burattin
 */
public class MainWindow extends JPanel {

	private static final long serialVersionUID = 6505528181152676523L;
	
	// the actual configuration
	private ConfigurationSet conf;
	
	// main window components
	private MainToolbar mainWindowToolbar = null;
	private ProcessesList generatedProcessesList = null;
	private SingleProcessVisualizer singleProcessVisualizer = null;
	private ProgressStack progressStack = null;
	private Console debugConsole = null;

	/**
	 * Main window class constructor
	 */
	public MainWindow(ApplicationController controller) {
		this.conf = controller.getConfiguration(this.getClass().getCanonicalName());
		
		// place the components of the window
		placeComponents();
	}
	
	/**
	 * Method to get the window console
	 * 
	 * @return
	 */
	public Console getConsole() {
		return debugConsole;
	}
	
	/**
	 * Method to get the stacked progress notifications area
	 * 
	 * @return
	 */
	public ProgressStack getProgressStack() {
		return progressStack;
	}
	
	/**
	 * Method to get the toolbar
	 * 
	 * @return
	 */
	public MainToolbar getToolbar() {
		return mainWindowToolbar;
	}
	
	/**
	 * Method to get the main process visualizer
	 * 
	 * @return
	 */
	public SingleProcessVisualizer getSingleProcessVisualizer() {
		return singleProcessVisualizer;
	}
	
	/**
	 * Method to get the processes list
	 * 
	 * @return
	 */
	public ProcessesList getProcessesList() {
		return generatedProcessesList;
	}
	
	/**
	 * This method places all the components into their right place
	 */
	protected void placeComponents() {
		// set the main layout
		setLayout(new BorderLayout());
		
		// main window container
		JPanel mainWindowContainer = new JPanel();
		mainWindowContainer.setLayout(new GridBagLayout());
		add(mainWindowContainer, BorderLayout.CENTER);
		
		// add the list of generated processes
		generatedProcessesList = new ProcessesList(conf.getChild(ProcessesList.class.getCanonicalName()));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.fill = GridBagConstraints.VERTICAL;
		mainWindowContainer.add(generatedProcessesList, c);
		
		// add the current process visualizer
		singleProcessVisualizer = new SingleProcessVisualizer(conf.getChild(SingleProcessVisualizer.class.getCanonicalName()));
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(3, 0, 3, 3);
		c.fill = GridBagConstraints.BOTH;
		mainWindowContainer.add(singleProcessVisualizer, c);
		
		// add the notifications and debug console
		progressStack = new ProgressStack(conf.getChild(ProgressStack.class.getCanonicalName()));
		debugConsole = new Console(conf.getChild(Console.class.getCanonicalName()));
		JPanel south = new JPanel(new BorderLayout());
		south.add(progressStack, BorderLayout.NORTH);
		south.add(debugConsole, BorderLayout.SOUTH);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.SOUTH;
		mainWindowContainer.add(south, c);
		
		// insert the toolbar
		mainWindowToolbar = new MainToolbar();
		add(mainWindowToolbar, BorderLayout.NORTH);
	}
}

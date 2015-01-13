package plg.gui.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import plg.gui.config.ConfigurationSet;
import plg.gui.controller.ApplicationController;
import plg.gui.panels.Console;
import plg.gui.panels.MainWindowPanel;
import plg.gui.panels.ProcessesList;
import plg.gui.panels.SingleProcessVisualizer;
import plg.gui.widgets.MainToolbar;

/**
 * 
 *
 * @author Andrea Burattin
 */
public class MainWindow extends JFrame {

	private static final long serialVersionUID = 6505528181152676523L;

	// main window configuration keys
	private static final String KEY_SIZE_WIDTH = "KEY_SIZE_WIDTH";
	private static final String KEY_SIZE_HEIGHT = "KEY_SIZE_HEIGHT";
	private static final String KEY_POSITION_X = "KEY_POSITION_X";
	private static final String KEY_POSITION_Y = "KEY_POSITION_Y";
	
	// default configuration values
	private static final int DEFAULT_WIDTH = 1366;
	private static final int DAFAULT_HEIGHT = 768;
	private static final int MINIMUM_WIDTH = 800;
	private static final int MINIMUM_HEIGHT = 600;
	private static final String FRAME_TITLE = "PLG - Processes and Logs Generator";
	
	// application controller
	private ApplicationController controller = null;
	
	// the actual configuration
	private ConfigurationSet conf;
	
	// main window components
	private JToolBar mainWindowToolbar = null;
	private MainWindowPanel generatedProcessesList = null;
	private MainWindowPanel singleProcessVisualizer = null;
	private MainWindowPanel debugConsole = null;

	/**
	 * Main window class constructor
	 */
	public MainWindow(ApplicationController controller) {
		this.controller = controller;
		this.conf = controller.getConfiguration(this.getClass().getCanonicalName());
		
		// register closing action..
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exitApplication();
			}
			@Override
			public void windowClosed(WindowEvent e) {
				windowClosing(e);
			}
		});
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				saveWindowState();
			}
			@Override
			public void componentResized(ComponentEvent e) {
				saveWindowState();
			}
		});
		
		// restore window position and size
		restoreWindowState();
		
		// set minimum dimensions
		setMinimumSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
		
		// place the components of the window
		placeComponents();
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
		
		// insert the toolbar
		mainWindowToolbar = new MainToolbar();
		add(mainWindowToolbar, BorderLayout.NORTH);
		
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
		c.fill = GridBagConstraints.BOTH;
		mainWindowContainer.add(singleProcessVisualizer, c);
		
		// add the debug console
		debugConsole = new Console(conf.getChild(Console.class.getCanonicalName()));
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.SOUTH;
		mainWindowContainer.add(debugConsole, c);
	}
	
	/**
	 * Method to restore the current state of the window
	 */
	protected void restoreWindowState() {
		int default_x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (DEFAULT_WIDTH / 2);
		int default_y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (DAFAULT_HEIGHT / 2);
		
		int width = Math.min(conf.getInteger(KEY_SIZE_WIDTH, DEFAULT_WIDTH), Toolkit.getDefaultToolkit().getScreenSize().width);
		int height = Math.min(conf.getInteger(KEY_SIZE_HEIGHT, DAFAULT_HEIGHT), Toolkit.getDefaultToolkit().getScreenSize().height);
		this.setSize(width, height);
		
		int x = Math.max(0, conf.getInteger(KEY_POSITION_X, default_x));
		int y = Math.max(0, conf.getInteger(KEY_POSITION_Y, default_y));
		this.setLocation(x, y);
		
		this.setTitle(FRAME_TITLE);
	}
	
	/**
	 * Method to get the current window state and save it into the configuration
	 * 
	 * @author Christian W. Guenther (christian@deckfour.org)
	 */
	protected void saveWindowState() {
		Point p = getLocation();
		conf.setInteger(KEY_POSITION_X, p.x);
		conf.setInteger(KEY_POSITION_Y, p.y);
		conf.setInteger(KEY_SIZE_WIDTH, getWidth());
		conf.setInteger(KEY_SIZE_HEIGHT, getHeight());
	}

	/**
	 * Method executed when the application is exited
	 * 
	 * @author Christian W. Guenther (christian@deckfour.org)
	 */
	protected void exitApplication() {
		try {
			controller.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}

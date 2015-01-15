package plg.gui.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import plg.gui.config.ConfigurationSet;
import plg.gui.controller.ApplicationController;

/**
 * This class contains the main frame of PLG
 *
 * @author Andrea Burattin
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = -1040942077404079636L;

	// main window configuration keys
	private static final String KEY_SIZE_WIDTH = "SIZE_WIDTH";
	private static final String KEY_SIZE_HEIGHT = "SIZE_HEIGHT";
	private static final String KEY_POSITION_X = "POSITION_X";
	private static final String KEY_POSITION_Y = "POSITION_Y";
	private static final String KEY_WINDOW_MAXIMIZED = "WINDOW_MAXIMIZED";
	
	// default configuration values
	private static final String FRAME_TITLE = "PLG - Processes and Logs Generator";
	private static final int DEFAULT_WIDTH = 1366;
	private static final int DAFAULT_HEIGHT = 768;
	private static final int MINIMUM_WIDTH = 800;
	private static final int MINIMUM_HEIGHT = 600;
	
	// application controller
	private ApplicationController controller = null;
	// the actual configuration
	private ConfigurationSet conf;

	/**
	 * 
	 * @param controller
	 */
	public MainFrame(ApplicationController controller) {
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
		
		// set the application icon
		try {
			Image curImage = ImageIO.read(new File("resources/icons/application-icon.png"));
			setIconImage(curImage);
		} catch (IOException e) { }
		
		// restore window position and size
		restoreWindowState();
		
		// set minimum dimensions
		setMinimumSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
		
		// add here the main window
		setLayout(new BorderLayout());
		add(controller.getMainWindow());
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
		
		if (conf.getBoolean(KEY_WINDOW_MAXIMIZED, false)) {
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
	}
	
	/**
	 * Method to get the current window state and save it into the configuration
	 * 
	 * @author Christian W. Guenther (christian@deckfour.org)
	 */
	protected void saveWindowState() {
		Point p = getLocation();
		if (getExtendedState() != JFrame.MAXIMIZED_BOTH) {
			conf.setInteger(KEY_POSITION_X, p.x);
			conf.setInteger(KEY_POSITION_Y, p.y);
			conf.setInteger(KEY_SIZE_WIDTH, getWidth());
			conf.setInteger(KEY_SIZE_HEIGHT, getHeight());
		}
		conf.setBoolean(KEY_WINDOW_MAXIMIZED, (getExtendedState() == JFrame.MAXIMIZED_BOTH));
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

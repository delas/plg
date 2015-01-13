package plg.gui.window;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;

import plg.gui.config.ConfigurationSet;
import plg.gui.config.UIConfiguration;

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
	private static final String KEY_TITLE = "KEY_TITLE";
	
	// the actual configuration
	private ConfigurationSet conf;

	/**
	 * Main window class constructor
	 */
	public MainWindow() {
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
		conf = UIConfiguration.master().getChild(this.getClass().getCanonicalName());
		restoreWindowState();
		
	}

	/**
	 * Method to restore the current state of the window
	 */
	protected void restoreWindowState() {
		int default_width = 1366;
		int defauil_height = 768;
		
		int default_x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (default_width / 2);
		int default_y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (defauil_height / 2);
		
		int width = Math.min(conf.getInteger(KEY_SIZE_WIDTH, default_width), Toolkit.getDefaultToolkit().getScreenSize().width);
		int height = Math.min(conf.getInteger(KEY_SIZE_HEIGHT, defauil_height), Toolkit.getDefaultToolkit().getScreenSize().height);
		this.setSize(width, height);
		
		int x = Math.max(0, conf.getInteger(KEY_POSITION_X, default_x));
		int y = Math.max(0, conf.getInteger(KEY_POSITION_Y, default_y));
		this.setLocation(x, y);
		
		this.setTitle(conf.get(KEY_TITLE, "PLG - Processes and Logs Generator"));
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
		conf.set(KEY_TITLE, this.getTitle());
	}

	/**
	 * Method executed when the application is exited
	 * 
	 * @author Christian W. Guenther (christian@deckfour.org)
	 */
	protected void exitApplication() {
		try {
			UIConfiguration.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}

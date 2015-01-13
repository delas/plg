package plg.gui.controller;

import java.io.IOException;

import plg.gui.config.ConfigurationSet;
import plg.gui.config.UIConfiguration;
import plg.gui.window.MainFrame;
import plg.gui.window.MainWindow;

/**
 * This class represents the application controller, and is in charge of
 * managing the entire application workflow.
 *
 * @author Andrea Burattin
 */
public class ApplicationController {

	private static ApplicationController controller = new ApplicationController();
	
	private MainFrame mainFrame;
	private MainWindow mainWindow;
	private ConfigurationSet configuration;
	
	public static ApplicationController instance() {
		return controller;
	}
	
	private ApplicationController() {
		configuration = UIConfiguration.master();
		mainWindow = new MainWindow(this);
		mainFrame = new MainFrame(this);
	}
	
	public MainWindow getMainWindow() {
		return mainWindow;
	}
	
	public MainFrame getMainFrame() {
		return mainFrame;
	}
	
	public void close() throws IOException {
		UIConfiguration.save();
	}
	
	public ConfigurationSet getGeneralConfiguration() {
		return configuration;
	}
	
	public ConfigurationSet getConfiguration(String root) {
		return configuration.getChild(root);
	}
}

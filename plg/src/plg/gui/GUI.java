package plg.gui;

import java.util.Arrays;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import plg.gui.controller.ApplicationController;
import plg.gui.remote.RemoteLogger;
import plg.utils.CPUUtils;
import plg.utils.Logger;

/**
 * Main application class
 * 
 * @author Andrea Burattin
 */
public class GUI {

	public static void main(String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		// command line arguments parsing
		List<String> arguments = Arrays.asList(args);
		RemoteLogger.instance().checkNewVersion(!arguments.contains("--no-version-check"));
		
		// gui startup
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		ApplicationController.instance().getMainFrame().setVisible(true);
		
		// initialization logging
		Logger.instance().debug("Application started!");
		Logger.instance().debug("You have " + CPUUtils.CPUAvailable() + " CPU(s) available");
	}
}

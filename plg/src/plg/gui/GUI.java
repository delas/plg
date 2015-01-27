package plg.gui;

import plg.gui.controller.ApplicationController;
import plg.utils.Logger;

/**
 * Main application class
 * 
 * @author Andrea Burattin
 */
public class GUI {

	public static void main(String args[]) {
		ApplicationController.instance().getMainFrame().setVisible(true);
		Logger.instance().debug("Application started!");
	}
}

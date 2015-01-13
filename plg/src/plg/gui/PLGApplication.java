package plg.gui;

import plg.gui.controller.ApplicationController;

public class PLGApplication {

	public static void main(String args[]) {
		ApplicationController.instance().getMainFrame().setVisible(true);
	}
}

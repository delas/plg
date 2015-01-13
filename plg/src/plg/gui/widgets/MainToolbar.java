package plg.gui.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import plg.gui.controller.ApplicationController;
import plg.gui.controller.ConsoleController;

public class MainToolbar extends JToolBar {

	private static final long serialVersionUID = -2290088626676975817L;

	JToggleButton showConsole = new JToggleButton("Show console");
	
	public MainToolbar() {
		setFloatable(false);
		
		add(new JButton("test"));
		add(Box.createHorizontalGlue());
		add(showConsole);
		
		showConsole.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ApplicationController.instance().console().setConsoleVisibility(showConsole.isSelected());
			}
		});
	}
	
	public void setShowConsoleSelected(boolean visible) {
		showConsole.setSelected(visible);
	}
}

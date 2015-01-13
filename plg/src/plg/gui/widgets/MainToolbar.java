package plg.gui.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import plg.gui.controller.ApplicationController;

public class MainToolbar extends JToolBar {

	private static final long serialVersionUID = -2290088626676975817L;

	JButton newProcess = new JButton("New Process");
	JToggleButton showConsole = new JToggleButton("Show Console");
	
	public MainToolbar() {
		setFloatable(false);
		
		add(newProcess);
		add(Box.createHorizontalGlue());
		add(showConsole);
		
		registerListeners();
	}
	
	public void setShowConsoleSelected(boolean visible) {
		showConsole.setSelected(visible);
	}
	
	private void registerListeners() {
		newProcess.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ApplicationController.instance().processes().randomProcess();
			}
		});
		
		showConsole.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ApplicationController.instance().console().setConsoleVisibility(showConsole.isSelected());
			}
		});
	}
}

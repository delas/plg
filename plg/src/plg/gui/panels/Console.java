package plg.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;

import plg.gui.config.ConfigurationSet;
import plg.gui.controller.ApplicationController;
import plg.gui.controller.ConsoleController;
import plg.utils.Logger;

/**
 * This class contains the main program console. All output of the
 * {@link Logger} class is captured into this widget.
 *
 * @author Andrea Burattin
 */
public class Console extends MainWindowPanel  {

	private static final long serialVersionUID = -8707628278434660233L;
	
	
	protected static final int HEIGHT = 200;
	
	private JTextPane console = new JTextPane();
	private StyledDocument log = console.getStyledDocument();

	/**
	 * Basic class constructor
	 * 
	 * @param conf
	 */
	public Console(final ConfigurationSet conf) {
		super(conf);
		
		this.console.setEditable(false);
		this.console.setBackground(Color.black);
		
		setPreferredSize(new Dimension(0, HEIGHT));
		setBackground(Color.black);
		
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(console);
		add(scrollPane, BorderLayout.CENTER);
		
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				ApplicationController.instance().console().setConsoleVisibility();
//			}
//		});
	}
	
	public StyledDocument getStyledDocument() {
		return log;
	}
	
	@Override
	public void setVisible(boolean visible) {
		
		super.setVisible(visible);
	}
}

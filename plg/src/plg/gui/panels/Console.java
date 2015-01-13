package plg.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

import plg.gui.config.ConfigurationSet;
import plg.utils.Logger;

/**
 * This class contains the main program console. All output of the
 * {@link Logger} class is captured into this window.
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
	}
	
	/**
	 * This method returns the document associated to the console
	 * 
	 * @return
	 */
	public StyledDocument getStyledDocument() {
		return log;
	}
	
	/**
	 * This method reset to the proper position the caret of the console.
	 */
	public void resetCaret() {
		console.setCaretPosition(console.getText().length());
	}
}

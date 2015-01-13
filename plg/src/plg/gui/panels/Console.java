package plg.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.PrintStream;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import plg.gui.config.ConfigurationSet;
import plg.utils.Logger;

/**
 * This class contains the main program console. All output of the
 * {@link Logger} class is captured into this widget.
 *
 * @author Andrea Burattin
 */
public class Console extends MainWindowPanel  {

	private static final long serialVersionUID = -8707628278434660233L;
	
	protected static final String KEY_CONSOLE_VISIBLE = "KEY_CONSOLE_VISIBLE";
	protected static final boolean DEFAULT_VISIBILITY = false;
	protected static final int HEIGHT = 200;
	
	private JTextPane console = new JTextPane();
	private StyledDocument log = console.getStyledDocument();
	private ConsolePrintStream consolePrintStream = new ConsolePrintStream();

	/**
	 * Basic class constructor
	 * 
	 * @param conf
	 */
	public Console(ConfigurationSet conf) {
		super(conf);
		
		this.console.setEditable(false);
		this.console.setBackground(Color.black);
		
		setPreferredSize(new Dimension(0, HEIGHT));
		setBackground(Color.black);
		setVisible(conf.getBoolean(KEY_CONSOLE_VISIBLE, DEFAULT_VISIBILITY));
		
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(console);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * This method returns the {@link PrintStream} associated to this console
	 * 
	 * @return the print stream of this console
	 */
	public PrintStream getConsolePrintStream() {
		return consolePrintStream;
	}
	
	@Override
	public void setVisible(boolean visible) {
		conf.setBoolean(KEY_CONSOLE_VISIBLE, visible);
		super.setVisible(visible);
	}
	
	/**
	 * This class describes a {@link PrintStream} which prints the data into the
	 * graphical console. At this point, only the {@link #println(String)} is
	 * provided.
	 *
	 * @author Andrea Burattin
	 */
	class ConsolePrintStream extends PrintStream {
		
		private SimpleAttributeSet infoStyle = new SimpleAttributeSet();
		private SimpleAttributeSet debugStyle = new SimpleAttributeSet();
		
		/**
		 * Basic class constructor
		 */
		public ConsolePrintStream() {
			super(System.out);
			
			infoStyle.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.green);
			debugStyle.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.green.darker().darker().darker());
			infoStyle.addAttribute(StyleConstants.CharacterConstants.FontFamily, "Monospaced");
			debugStyle.addAttribute(StyleConstants.CharacterConstants.FontFamily, "Monospaced");
		}
		
		/**
		 * This method prints the provided string as an information
		 * 
		 * @param message
		 */
		private void printInfo(String message) {
			try {
				log.insertString(0, message + "\n", infoStyle);
			} catch (BadLocationException e) { }
		}
		
		/**
		 * This method prints the provided string as a debug
		 * 
		 * @param message
		 */
		private void printDebug(String message) {
			try {
				log.insertString(0, message + "\n", debugStyle);
			} catch (BadLocationException e) { }
		}
		
		@Override
		public void println(String message) {
			if (message.contains(" - DEBUG - ")) {
				printDebug(message);
			} else {
				printInfo(message);
			}
		}
	}
}

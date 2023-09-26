package plg.gui.controller;

import java.awt.Color;
import java.io.PrintStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import plg.gui.config.ConfigurationSet;
import plg.gui.panels.Console;
import plg.gui.remote.REMOTE_MESSAGES;
import plg.gui.remote.RemoteLogger;
import plg.utils.Logger;

/**
 * This class represents the console controller, and is in charge of managing
 * the application console.
 * 
 * @author Andrea Burattin
 */
public class ConsoleController {

	protected static final String KEY_CONSOLE_VISIBLE = "CONSOLE_VISIBLE";
	protected static final boolean DEFAULT_VISIBILITY = false;
	
	private ApplicationController applicationController;
	private ConfigurationSet configuration;
	private Console console;
	private ConsolePrintStream consolePrintStream;
	
	/**
	 * Controller constructor
	 * 
	 * @param applicationController the main application controller
	 */
	protected ConsoleController(ApplicationController applicationController) {
		this.applicationController = applicationController;
		this.configuration = applicationController.getConfiguration(ConsoleController.class.getCanonicalName());
		this.console = applicationController.getMainWindow().getConsole();
		this.consolePrintStream = new ConsolePrintStream(console.getStyledDocument());
		
		// redirect the logger to the application console
		Logger.LOG_PRINT_STREAM = consolePrintStream;
		
		// set default console visibility
		setConsoleVisibility(configuration.getBoolean(KEY_CONSOLE_VISIBLE, DEFAULT_VISIBILITY));
	}
	
	/**
	 * This method configures the visibility of the console
	 * 
	 * @param visible whether the console has to be visible
	 */
	public void setConsoleVisibility(boolean visible) {
		configuration.setBoolean(KEY_CONSOLE_VISIBLE, visible);
		applicationController.getMainWindow().getToolbar().setShowConsoleSelected(visible);
		applicationController.getMainWindow().getConsole().setVisible(visible);
		
		// remote logging, if available
		RemoteLogger.instance().log(REMOTE_MESSAGES.CHANGED_CONSOLE_VISIBILITY).add("visibility", visible).send();
	}
	
	/**
	 * This method returns the {@link PrintStream} associated to this console
	 * 
	 * @return the print stream of this console
	 */
	public PrintStream getConsolePrintStream() {
		return consolePrintStream;
	}
	
	/**
	 * This class describes a {@link PrintStream} which prints the data into the
	 * graphical console. At this point, only the {@link #println(String)} is
	 * provided.
	 *
	 * @author Andrea Burattin
	 */
	class ConsolePrintStream extends PrintStream {
		
		private StyledDocument log;
		private SimpleAttributeSet infoStyle = new SimpleAttributeSet();
		private SimpleAttributeSet debugStyle = new SimpleAttributeSet();
		private SimpleAttributeSet fileStyle = new SimpleAttributeSet();
		
		/**
		 * Basic class constructor
		 */
		public ConsolePrintStream(StyledDocument log) {
			super(System.out);
			this.log = log;
			
			infoStyle.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.green);
			debugStyle.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.green.darker().darker().darker());
			fileStyle.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.darkGray.darker());
			
			infoStyle.addAttribute(StyleConstants.CharacterConstants.FontFamily, "Monospaced");
			debugStyle.addAttribute(StyleConstants.CharacterConstants.FontFamily, "Monospaced");
			fileStyle.addAttribute(StyleConstants.CharacterConstants.FontFamily, "Monospaced");
		}
		
		/**
		 * This method prints the provided string as an information
		 * 
		 * @param message
		 */
		private void printInfo(String message) {
			try {
				log.insertString(console.getStyledDocument().getLength(), message, infoStyle);
			} catch (BadLocationException e) { }
		}
		
		/**
		 * This method prints the provided string as a debug
		 * 
		 * @param message
		 */
		private void printDebug(String message) {
			try {
				log.insertString(console.getStyledDocument().getLength(), message, debugStyle);
			} catch (BadLocationException e) { }
		}
		
		/**
		 * This method prints the provided string as a file name and adds a new line
		 * 
		 * @param file
		 */
		private void printFile(String file) {
			try {
				log.insertString(console.getStyledDocument().getLength(), file + "\n", fileStyle);
			} catch (BadLocationException e) { }
		}
		
		@Override
		public void println(String message) {
			int fileStartingAt = message.lastIndexOf("(");
			String file = message.substring(fileStartingAt, message.length());
			message = message.substring(0, fileStartingAt);
			if (message.contains(" - DEBUG - ")) {
				printDebug(message);
			} else {
				printInfo(message);
			}
			printFile(file);
			console.resetCaret();
		}
	}
}

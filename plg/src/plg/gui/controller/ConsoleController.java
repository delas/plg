package plg.gui.controller;

import java.awt.Color;
import java.io.PrintStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import plg.gui.config.ConfigurationSet;
import plg.gui.panels.Console;
import plg.utils.Logger;

public class ConsoleController {

	protected static final String KEY_CONSOLE_VISIBLE = "CONSOLE_VISIBLE";
	protected static final boolean DEFAULT_VISIBILITY = false;
	
	private ApplicationController applicationController = null;
	private ConfigurationSet configuration = null;
	private Console console = null;
	private ConsolePrintStream consolePrintStream = null;
	
	protected ConsoleController(
			ApplicationController applicationController,
			ConfigurationSet configuration,
			Console console) {
		this.applicationController = applicationController;
		this.configuration = configuration;
		this.console = console;
		consolePrintStream = new ConsolePrintStream();
		
		// redirect the logger to the application console
		Logger.LOG_PRINT_STREAM = consolePrintStream;
		
		// set default console visibility
		setConsoleVisibility(configuration.getBoolean(KEY_CONSOLE_VISIBLE, DEFAULT_VISIBILITY));
	}
	
	public void setConsoleVisibility(boolean visible) {
		configuration.setBoolean(KEY_CONSOLE_VISIBLE, visible);
		applicationController.getMainWindow().getToolbar().setShowConsoleSelected(visible);
		applicationController.getMainWindow().getConsole().setVisible(visible);
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
				console.getStyledDocument().insertString(0, message + "\n", infoStyle);
			} catch (BadLocationException e) { }
		}
		
		/**
		 * This method prints the provided string as a debug
		 * 
		 * @param message
		 */
		private void printDebug(String message) {
			try {
				console.getStyledDocument().insertString(0, message + "\n", debugStyle);
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

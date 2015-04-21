package plg.gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidDataObject;
import plg.exceptions.InvalidProcessException;
import plg.exceptions.InvalidScript;
import plg.exceptions.UnsupportedPLGFileFormat;
import plg.gui.controller.ApplicationController;
import plg.gui.util.collections.ImagesCollection;

public class ErrorDialog extends GeneralDialog {

	private static final long serialVersionUID = 7240134489904377493L;
	
	private Exception exception;
	private JLabel head;
	private JTextArea details;

	public ErrorDialog(JFrame owner, Exception e) {
		super(owner, "Exception thrown", e.getMessage(), ApplicationController.instance().getConfiguration(EvolutionDialog.class.getCanonicalName()), false, true);
		this.exception = e;

		bodyPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0;
		c.insets = new Insets(0, 0, 10, 10);
		bodyPanel.add(new JLabel(ImagesCollection.ERROR_ICON), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 0, 10, 0);
		head = prepareFieldLabel("");
		head.setHorizontalAlignment(JLabel.LEFT);
		bodyPanel.add(head, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		
		details = new JTextArea("");
		details.setEditable(false);
		JScrollPane sp = new JScrollPane(details);
		bodyPanel.add(sp, c);
		
		pupulateDescriptions();
	}
	
	private void pupulateDescriptions() {
		if (exception instanceof IllegalSequenceException) {
			head.setText("<html>"
					+ "<b>Message:</b> " + exception.getMessage() + "<br>"
					+ "<b>Description:</b> An illegal sequence has been reported. "
					+ "Illegal sequences are reported in <em>Table 7.3</em> of the "
					+ "<a href=\"http://www.omg.org/cgi-bin/doc?formal/11-01-03.pdf\">BPMN 2.0 "
					+ "standard definition</a>.</html>");
			details.setText(getStackTrace());
		} else if (exception instanceof InvalidScript) {
			head.setText("<html>"
					+ "<b>Message:</b> " + exception.getMessage() + "<br>"
					+ "<b>Description:</b> there is an error in the Python script.</html>");
			details.setText(((InvalidScript) exception).getScript());
		}else if (exception instanceof UnsupportedPLGFileFormat) {
			head.setText("<html>"
					+ "<b>Message:</b> " + exception.getMessage() + "<br>"
					+ "<b>Description:</b> the provided PLG file format is not"
					+ "supported by the current implementation.</html>");
			details.setText(getStackTrace());
		} else if (exception instanceof InvalidDataObject) {
			head.setText("<html>"
					+ "<b>Message:</b> " + exception.getMessage() + "<br>"
					+ "<b>Description:</b> A provided data object is somehow illegal.</html>");
			details.setText(getStackTrace());
		} else if (exception instanceof InvalidProcessException) {
			head.setText("<html>"
					+ "<b>Message:</b> " + exception.getMessage() + "<br>"
					+ "<b>Description:</b> The process is somehow illegal.</html>");
			details.setText(getStackTrace());
		} else {
			head.setText("<html><b>Message:</b> " + exception.getMessage() + "</html>");
			details.setText(getStackTrace());
		}
	}
	
	private String getStackTrace() {
		String stackTrace = "";
		for (StackTraceElement ste : exception.getStackTrace()) {
			stackTrace += ste.toString() + "\n";
		}
		return stackTrace;
	}
	
	public static void main(String args[]) {
		JFrame f = new JFrame("test");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(800, 600);
		f.setVisible(true);
		
		ErrorDialog e = new ErrorDialog(f, new InvalidScript("Illegal sequence", "ASDFSDF"));
		e.setVisible(true);
	}
}

package plg.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import plg.gui.controller.ApplicationController;

/**
 * This class contains the dialog for the configuration of plain data objects
 *
 * @author Andrea Burattin
 */
public class PlainDataObjectDialog extends GeneralDialog {

	private static final long serialVersionUID = -4781877672157619819L;
	protected JTextField nameField = null;
	protected JTextField valueField = null;
	protected JButton okButton = null;

	public PlainDataObjectDialog(JFrame owner, String candidateDataObjectName) {
		super(owner,
			"Simple Data Object",
			"This dialog can be used to configure the name and value of the data object",
			ApplicationController.instance().getConfiguration(PlainDataObjectDialog.class.getCanonicalName()));
		
		// creates widgets
		nameField = new JTextField(candidateDataObjectName);
		valueField = new JTextField();
		okButton = new JButton("OK");
		
		// insert footer buttons
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		
				returnedValue = RETURNED_VALUES.SUCCESS;
				PlainDataObjectDialog.this.dispose();
			}
		});
		
		addFooterButton(okButton, true);
		
		// new process name
		nameField.setText(candidateDataObjectName);
		bodyPanel.add(prepareFieldLabel("Data object name"));
		bodyPanel.add(nameField);
		
		bodyPanel.add(prepareFieldLabel("Data object value"));
		bodyPanel.add(valueField);
		
		// layout everything
		layoutBody();
	}
	
	public String getDataObjectName() {
		return nameField.getText();
	}
	
	public void setDataObjectName(String name) {
		nameField.setText(name);
	}
	
	public String getDataObjectValue() {
		return valueField.getText();
	}
	
	public void setDataObjectValue(String value) {
		valueField.setText(value);
	}
}

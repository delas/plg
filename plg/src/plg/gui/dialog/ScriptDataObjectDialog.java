package plg.gui.dialog;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import plg.gui.controller.ApplicationController;
import plg.gui.util.SpringUtilities;
import plg.gui.util.collections.ScriptsCollection;
import plg.model.data.GeneratedDataObject;
import plg.model.data.IntegerDataObject;
import plg.model.data.StringDataObject;

/**
 * This dialog can be used to configure the script for the generation of the
 * {@link GeneratedDataObject} (either string or integer)
 * 
 * @author Andrea Burattin
 */
public class ScriptDataObjectDialog extends CodeDialog {

	private static final long serialVersionUID = -8309511450343816209L;
	private JTextField nameField;
	
	public ScriptDataObjectDialog(JFrame owner, String candidateDataObjectName, Class<?> type) {
		super(owner,
			"Script Data Object",
			"In the area below you can insert the Python script to generate the data object value",
			ApplicationController.instance().getConfiguration(ScriptDataObjectDialog.class.getCanonicalName()));
		
		setDataObjectName(candidateDataObjectName);
		if (type.equals(StringDataObject.class)) {
			setScript(ScriptsCollection.STRING_DATA_OBJECT);
		} else if (type.equals(IntegerDataObject.class)) {
			setScript(ScriptsCollection.INTEGER_DATA_OBJECT);
		}
	}
	
	@Override
	protected String standardScript() {
		return "";
	}
	
	@Override
	protected JPanel addToNorth() {
		JPanel p = new JPanel();
		nameField = new JTextField();
		
		p.setLayout(new SpringLayout());
		p.add(prepareFieldLabel("New data object name"));
		p.add(nameField);
		
		// lay out the panel
		SpringUtilities.makeCompactGrid(p,
				(p.getComponentCount() / 2), 2, // rows, cols
				0, 0, // initX, initY
				5, 10); //xPad, yPad
		
		return p;
	}
	
	public void setScript(String script) {
		codingArea.setText(script);
	}
	
	public void setDataObjectName(String name) {
		nameField.setText(name);
	}
	
	public String getDataObjectName() {
		return nameField.getText();
	}
}

package plg.gui.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import plg.gui.config.ConfigurationSet;

/**
 * This dialog is used to configure Python scripts within the project
 * 
 * @author Andrea Burattin
 */
public abstract class CodeDialog extends GeneralDialog {

	private static final long serialVersionUID = 9095798964694156164L;
	protected RSyntaxTextArea codingArea;
	protected JButton okButton = null;

	public CodeDialog(JFrame owner, String title, String help, ConfigurationSet configurationSet) {
		super(owner, title, help, configurationSet);
		
		addComponenets();
	}
	
	protected void addComponenets() {
		this.codingArea = new RSyntaxTextArea(18, 60);
		codingArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
		codingArea.setCodeFoldingEnabled(true);
		codingArea.setText(standardScript());
		
		bodyPanel.setLayout(new BorderLayout());
		
		JPanel northPanel = addToNorth();
		if (northPanel != null) {
			bodyPanel.add(northPanel, BorderLayout.NORTH);
		}
		bodyPanel.add(new RTextScrollPane(codingArea), BorderLayout.CENTER);
		
		// insert footer button
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				returnedValue = RETURNED_VALUES.SUCCESS;
				CodeDialog.this.dispose();
			}
		});
		addFooterButton(okButton, true);
	}
	
	public void setScript(String script) {
		codingArea.setText(script);
	}
	
	public String getScript() {
		return codingArea.getText();
	}
	
	protected JPanel addToNorth() {
		return null;
	}
	
	protected abstract String standardScript();
}

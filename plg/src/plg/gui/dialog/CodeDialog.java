package plg.gui.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import plg.gui.config.ConfigurationSet;

public abstract class CodeDialog extends GeneralDialog {

	private static final long serialVersionUID = 9095798964694156164L;
	protected RSyntaxTextArea codingArea;
	protected JButton okButton = null;

	public CodeDialog(JFrame owner, String title, String help, ConfigurationSet configurationSet) {
		super(owner, title, help, configurationSet);
		
		this.codingArea = new RSyntaxTextArea(20, 60);
		codingArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
		codingArea.setCodeFoldingEnabled(true);
		codingArea.setText(standardScript());
		
		bodyPanel.setLayout(new BorderLayout());
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
	
	protected String getFile(String fileName) {
		StringBuilder result = new StringBuilder("");
		File file = new File(System.class.getResource(fileName).getFile());
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	protected abstract String standardScript();
}

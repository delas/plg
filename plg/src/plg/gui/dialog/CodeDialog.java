package plg.gui.dialog;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import plg.gui.config.ConfigurationSet;

public abstract class CodeDialog extends GeneralDialog {

	private static final long serialVersionUID = 9095798964694156164L;
	protected RSyntaxTextArea codingArea;

	public CodeDialog(JFrame owner, String title, String help, ConfigurationSet configurationSet) {
		super(owner, title, help, configurationSet);
		
		this.codingArea = new RSyntaxTextArea(20, 60);
		codingArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
		codingArea.setCodeFoldingEnabled(true);
		codingArea.setText(standardScript());
		
		bodyPanel.setLayout(new BorderLayout());
		bodyPanel.add(new RTextScrollPane(codingArea), BorderLayout.CENTER);
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

package plg.gui.dialog;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import plg.gui.controller.ApplicationController;
import plg.gui.util.SpringUtilities;
import plg.gui.util.collections.ImagesCollection;

public class ErrorDialog extends GeneralDialog {

	private static final long serialVersionUID = 7240134489904377493L;

	public ErrorDialog(JFrame owner, Exception e) {
		super(owner, "Exception thrown", e.getMessage(), ApplicationController.instance().getConfiguration(EvolutionDialog.class.getCanonicalName()));

		String stackTrace = "";
		for (StackTraceElement ste : e.getStackTrace()) {
			stackTrace += ste.toString();
		}
		
		bodyPanel.add(new JLabel(ImagesCollection.ERROR_ICON));
		bodyPanel.add(new JLabel(e.getMessage()));
		bodyPanel.add(new JLabel());
		bodyPanel.add(new JTextArea(stackTrace));
		
		SpringUtilities.makeCompactGrid(bodyPanel,
				(bodyPanel.getComponentCount() / 2), 2, // rows, cols
				0, 0, // initX, initY
				5, 10); //xPad, yPad
		
	}
	
	public static void main(String args[]) {
		JFrame f = new JFrame("test");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(800, 600);
		f.setVisible(true);
		
		ErrorDialog e = new ErrorDialog(f, new Exception("sdfsadfsafsadf"));
		e.setVisible(true);
	}
}

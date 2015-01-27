package plg.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import plg.gui.config.ConfigurationSet;
import plg.model.Process;
import plg.visualizer.BPMNVisualizer;

public class SingleProcessVisualizer extends MainWindowPanel {

	private static final long serialVersionUID = -4811133888838143863L;
	private Process currentlyVisualizedProcess = null;
	private BPMNVisualizer visualizer = null;

	public SingleProcessVisualizer(ConfigurationSet conf) {
		super(conf);
		setBackground(Color.white);
		setLayout(new BorderLayout());
		
		generateProcessPlaceholder();
	}
	
	public void visualizeNewProcess(Process process) {
		this.currentlyVisualizedProcess = process;
		this.visualizer = new BPMNVisualizer(process);
		
		removeAll();
		add(visualizer, BorderLayout.CENTER);
		updateUI();
	}
	
	public void generateProcessPlaceholder() {
		JLabel noProcess = new JLabel("<html><div style=\"text-align: center;\">"
				+ "<span style=\"font-size: 30px\"><i>No process to display</i></span><br/><br/>"
				+ "<span style=\"font-size: 14px\">Start by creating a new process or importing an existing one.</span>"
				+ "</div></html>");
		noProcess.setHorizontalAlignment(SwingConstants.CENTER);
		noProcess.setForeground(Color.lightGray);
		noProcess.setFont(noProcess.getFont().deriveFont(Font.PLAIN));
		
		removeAll();
		add(noProcess, BorderLayout.CENTER);
		updateUI();
	}
}

package plg.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import plg.gui.config.ConfigurationSet;
import plg.model.Process;
import plg.visualizer.BPMNVisualizer;

public class SingleProcessVisualizer extends MainWindowPanel {

	private static final long serialVersionUID = -4811133888838143863L;
	private Process process = null;
	private BPMNVisualizer visualizer = null;

	public SingleProcessVisualizer(ConfigurationSet conf) {
		super(conf);
		setBackground(Color.white);
		setLayout(new BorderLayout());
		
		generateProcessPlaceholder();
	}
	
	public void visualizeNewProcess(Process process) {
		this.process = process;
		this.visualizer = new BPMNVisualizer(process);
		
		removeAll();
		add(visualizer, BorderLayout.CENTER);
		updateUI();
	}
	
	public void generateProcessPlaceholder() {
		JLabel noProcess = new JLabel("No process to display");
		noProcess.setHorizontalAlignment(SwingConstants.CENTER);
		noProcess.setForeground(Color.lightGray);
		noProcess.setFont(noProcess.getFont().deriveFont(40f).deriveFont(Font.ITALIC));
		
		removeAll();
		add(noProcess, BorderLayout.CENTER);
		updateUI();
	}
}

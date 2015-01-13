package plg.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;

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
	}
	
	public void visualizeNewProcess(Process process) {
		this.process = process;
		this.visualizer = new BPMNVisualizer(process);
		
		removeAll();
		add(visualizer, BorderLayout.CENTER);
		updateUI();
	}
}

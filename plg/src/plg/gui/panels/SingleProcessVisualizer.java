package plg.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import plg.gui.config.ConfigurationSet;
import plg.gui.dialog.Progress;
import plg.model.Process;
import plg.visualizer.BPMNVisualizer;

/**
 * This class contains the panel responsible of the visualization of a single
 * process.
 * 
 * @author Andrea Burattin
 */
public class SingleProcessVisualizer extends MainWindowPanel {

	private static final long serialVersionUID = -4811133888838143863L;
	private static JLabel noProcess = new JLabel("<html><div style=\"text-align: center;\">"
			+ "<span style=\"font-size: 30px\">No process to display</span><br/><br/>"
			+ "<span style=\"font-size: 14px\">Start by creating a new process or importing an existing one</span>"
			+ "</div></html>");
	
	static {
		noProcess.setHorizontalAlignment(SwingConstants.CENTER);
		noProcess.setForeground(Color.lightGray);
		noProcess.setFont(noProcess.getFont().deriveFont(Font.PLAIN));
	}
	
	private Process currentlyVisualizedProcess = null;
	private BPMNVisualizer visualizer = null;
	private Progress progress = new Progress();

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
		add(progress, BorderLayout.NORTH);
		add(visualizer, BorderLayout.CENTER);
		updateUI();
	}
	
	public Process getCurrentlyVisualizedProcess() {
		return currentlyVisualizedProcess;
	}
	
	public Progress getCurrentProgress() {
		return progress;
	}
	
	public void generateProcessPlaceholder() {
		removeAll();
		add(noProcess, BorderLayout.CENTER);
		updateUI();
	}
}

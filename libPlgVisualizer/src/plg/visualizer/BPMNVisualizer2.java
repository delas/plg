package plg.visualizer;

import java.awt.Color;

import org.processmining.plugins.graphviz.visualisation.DotPanel;

import plg.model.Process;
import plg.visualizer.model.DotModel;

/**
 * This widget is used to visualize a BPMN model.
 * 
 * @author Andrea Burattin
 */
public class BPMNVisualizer2 extends DotPanel {

	private static final long serialVersionUID = -8441909033110442685L;
	
	/**
	 * Class constructor
	 * 
	 * @param process the process graph to show
	 */
	public BPMNVisualizer2(Process process) {
		super(new DotModel(process));
		
		setOpaque(true);
		setBackground(Color.WHITE);
	}
}

package plg.visualizer.model.nodes;

import org.processmining.plugins.graphviz.dot.DotNode;

/**
 * 
 * @author Andrea Burattin
 */
public class DotTask extends DotNode {

	public DotTask(String label) {
		super(label, null);
		
		setSelectable(true);
		
		setOption("shape", "box");
		setOption("style", "rounded,filled");
		setOption("fillcolor", "#cedeef:#ffffff");
		setOption("gradientangle", "270");
		setOption("color", "#5a677b");
		setOption("width", "0.5");
		setOption("fontcolor", "#5a677b");
		setOption("fontname", "sans-serif");
		setOption("fontsize", "14.0");
		setOption("penwidth", "1");
	}
}

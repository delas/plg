package plg.visualizer.model.nodes;

import org.processmining.plugins.graphviz.dot.DotNode;

import plg.model.data.DataObject;

/**
 * 
 * @author Andrea Burattin
 */
public class DotDataObject extends DotNode {

	public DotDataObject(DataObject node) {
		super(node.getName(), null);

		setSelectable(true);

		setOption("shape", "note");
		setOption("fontsize", "10");
		setOption("width", "0.5");
		setOption("height", "0.5");
		setOption("style", "filled");
		setOption("fillcolor", "#ffffff");
		setOption("color", "#666666");
		setOption("fontcolor", "#666666");
		setOption("fontname", "sans-serif");
	}

}

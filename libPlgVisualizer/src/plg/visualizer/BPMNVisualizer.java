package plg.visualizer;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import plg.model.FlowObject;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.gateway.Gateway;
import plg.model.gateway.ParallelGateway;
import plg.model.sequence.Sequence;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

/**
 * This widget is used to visualize a BPMN model.
 * 
 * @author Andrea Burattin
 */
public class BPMNVisualizer extends JPanel {

	private static final long serialVersionUID = -8441909033110442685L;
	
	protected Process process;
	protected mxGraph graph;
	protected Object parent;
	protected mxStylesheet stylesheet;
	
	/**
	 * Class constructor
	 * 
	 * @param process the process graph to show
	 */
	public BPMNVisualizer(Process process) {
		this.process = process;
		this.graph = new mxGraph();
		this.parent = graph.getDefaultParent();
		this.stylesheet = graph.getStylesheet();
		
		setupMxGraph();
		mxGraphComponent graphComponent = updateGraph();
		graphComponent.setBorder(BorderFactory.createEmptyBorder());
		
		setLayout(new BorderLayout());
		add(graphComponent, BorderLayout.CENTER);
	}
	
	/**
	 * Method for the configuration of the graph stylesheets.
	 */
	private void setupMxGraph() {
		graph.setAutoSizeCells(true);
		
		Hashtable<String, Object> styleEventStart = new Hashtable<String, Object>();
		styleEventStart.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		styleEventStart.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);
		styleEventStart.put(mxConstants.STYLE_FILLCOLOR, "#5dbd5a");
		styleEventStart.put(mxConstants.STYLE_GRADIENTCOLOR, "#bafcc2");
		styleEventStart.put(mxConstants.STYLE_STROKECOLOR, "#20962f");
		styleEventStart.put(mxConstants.STYLE_FONTCOLOR, "#20962f");
		stylesheet.putCellStyle("EVENT_START", styleEventStart);
		
		Hashtable<String, Object> styleEventEnd = new Hashtable<String, Object>();
		styleEventEnd.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		styleEventEnd.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);
		styleEventEnd.put(mxConstants.STYLE_FILLCOLOR, "#e46e60");
		styleEventEnd.put(mxConstants.STYLE_GRADIENTCOLOR, "#ffc5c1");
		styleEventEnd.put(mxConstants.STYLE_STROKECOLOR, "#630000");
		styleEventEnd.put(mxConstants.STYLE_FONTCOLOR, "#630000");
		styleEventEnd.put(mxConstants.STYLE_STROKEWIDTH, 2);
		stylesheet.putCellStyle("EVENT_END", styleEventEnd);
		
		Hashtable<String, Object> styleTask = new Hashtable<String, Object>();
		styleTask.put(mxConstants.STYLE_SHAPE,mxConstants.SHAPE_RECTANGLE);
		styleTask.put(mxConstants.STYLE_ROUNDED, true);
		styleTask.put(mxConstants.STYLE_EDITABLE, false);
		styleTask.put(mxConstants.STYLE_MOVABLE, false);
		styleTask.put(mxConstants.STYLE_SPACING_TOP, 5);
		styleTask.put(mxConstants.STYLE_SPACING_RIGHT, 10);
		styleTask.put(mxConstants.STYLE_SPACING_BOTTOM, 5);
		styleTask.put(mxConstants.STYLE_SPACING_LEFT, 10);
		styleTask.put(mxConstants.STYLE_FILLCOLOR, "#cedeef");
		styleTask.put(mxConstants.STYLE_GRADIENTCOLOR, "ffffff");
		styleTask.put(mxConstants.STYLE_FONTCOLOR, "#5a677b");
		styleTask.put(mxConstants.STYLE_STROKECOLOR, "#5a677b");
		stylesheet.putCellStyle("TASK", styleTask);

		Hashtable<String, Object> styleGateway = new Hashtable<String, Object>();
		styleGateway.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RHOMBUS);
		styleGateway.put(mxConstants.STYLE_FILLCOLOR, "#ffff84");
		styleGateway.put(mxConstants.STYLE_GRADIENTCOLOR, "#ffffbd");
		styleGateway.put(mxConstants.STYLE_STROKECOLOR, "#a6a855");
		styleGateway.put(mxConstants.STYLE_FONTCOLOR, "#708041");
		styleGateway.put(mxConstants.STYLE_FONTSIZE, "16");
		styleGateway.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_MIDDLE);
		styleGateway.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
		stylesheet.putCellStyle("GATEWAY", styleGateway);
		
		Hashtable<String, Object> styleEdges = new Hashtable<String, Object>();
		styleEdges.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
		styleEdges.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
		styleEdges.put(mxConstants.STYLE_STROKECOLOR, "#5a677b");
		styleEdges.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION);
		styleEdges.put(mxConstants.STYLE_ROUNDED, true);
		styleEdges.put(mxConstants.STYLE_ORTHOGONAL, true);
		styleEdges.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
		stylesheet.setDefaultEdgeStyle(styleEdges);
	}
	
	/**
	 * This method updates the visualizer with the process graph provided.
	 * 
	 * @return
	 */
	private mxGraphComponent updateGraph() {
		mxGraphLayout layout = new mxHierarchicalLayout(graph, SwingConstants.WEST);
		
		graph.getModel().beginUpdate();
		Map<FlowObject, Object> components = new HashMap<FlowObject, Object>();
		try {
			for (StartEvent se : process.getStartEvents()) {
				Object obj = graph.insertVertex(parent, null, "Start", 20, 20, 20, 20, "EVENT_START");
				components.put(se, obj);
			}
			for (EndEvent ee : process.getEndEvents()) {
				Object obj = graph.insertVertex(parent, null, "End", 20, 20, 20, 20, "EVENT_END");
				components.put(ee, obj);
			}
			for (Task fo : process.getTasks()) {
				Object obj = graph.insertVertex(parent, null, fo.getName(), 20, 20, 80, 30, "TASK");
				graph.updateCellSize((mxCell) obj);
				components.put(fo, obj);
			}
			for (Gateway fo : process.getGateways()) {
				String label = "x";
				if (fo instanceof ParallelGateway) {
					label = "+";
				}
				Object obj = graph.insertVertex(parent, null, label, 20, 20, 30, 30, "GATEWAY");
				components.put(fo, obj);
			}
			for (Sequence s : process.getSequences()) {
				if (components.containsKey(s.getSource()) && components.containsKey(s.getSink())) {
					Object source = components.get(s.getSource());
					Object sink = components.get(s.getSink());
					graph.insertEdge(parent, null, null, source, sink);
				}
			}
			
			layout.execute(graph.getDefaultParent());
		} finally {
			graph.getModel().endUpdate();
		}
		mxGraphComponent c = new mxGraphComponent(graph);
		return c;
	}
}

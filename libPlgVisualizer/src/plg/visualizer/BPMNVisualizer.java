package plg.visualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import plg.model.Component;
import plg.model.FlowObject;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.IDataObjectOwner;
import plg.model.data.IntegerDataObject;
import plg.model.data.StringDataObject;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.gateway.Gateway;
import plg.model.gateway.ParallelGateway;
import plg.model.sequence.Sequence;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.mxPartitionLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.orthogonal.mxOrthogonalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxEdgeStyle;
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
		graphComponent.getViewport().setBackground(Color.WHITE);
		
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

		Hashtable<String, Object> styleDataObject = new Hashtable<String, Object>();
		styleDataObject.put(mxConstants.STYLE_SHAPE,mxConstants.SHAPE_RECTANGLE);
		styleDataObject.put(mxConstants.STYLE_ROUNDED, false);
		styleDataObject.put(mxConstants.STYLE_EDITABLE, false);
		styleDataObject.put(mxConstants.STYLE_MOVABLE, false);
		styleDataObject.put(mxConstants.STYLE_SPACING_TOP, 5);
		styleDataObject.put(mxConstants.STYLE_SPACING_RIGHT, 10);
		styleDataObject.put(mxConstants.STYLE_SPACING_BOTTOM, 5);
		styleDataObject.put(mxConstants.STYLE_SPACING_LEFT, 10);
		styleDataObject.put(mxConstants.STYLE_FILLCOLOR, "#ffffff");
		styleTask.put(mxConstants.STYLE_GRADIENTCOLOR, "ffffff");
		styleDataObject.put(mxConstants.STYLE_FONTCOLOR, "#888888");
		styleDataObject.put(mxConstants.STYLE_STROKECOLOR, "#888888");
		stylesheet.putCellStyle("DATA_OBJECT", styleDataObject);

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

		Map<String, Object> styleEdges = new Hashtable<String, Object>();
//		styleEdges.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
//		styleEdges.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
//		styleEdges.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION);
//		styleEdges.put(mxConstants.STYLE_EDGE, mxEdgeStyle.ElbowConnector);
//		styleEdges.put(mxConstants.STYLE_ORTHOGONAL, true);
//		styleEdges.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
		stylesheet.putCellStyle("EDGE", styleEdges);
		
		Map<String, Object> styleEdgesDobj = new Hashtable<String, Object>();
		styleEdgesDobj.put(mxConstants.STYLE_EDGE, mxEdgeStyle.EntityRelation);
		styleEdgesDobj.put(mxConstants.STYLE_STROKECOLOR, "#666666");
		styleEdgesDobj.put(mxConstants.STYLE_DASHED, true);
		stylesheet.putCellStyle("EDGE_DOBJ", styleEdgesDobj);
	}
	
	/**
	 * This method updates the visualizer with the process graph provided.
	 * 
	 * @return
	 */
	private mxGraphComponent updateGraph() {
		
		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try {
			Map<Component, Object> components = new HashMap<Component, Object>();
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
					graph.insertEdge(parent, s.getId(), null, source, sink, "EDGE");
				}
			}
			for (DataObject dobj : process.getDataObjects()) {
				String label = dobj.getName();
				if (dobj instanceof StringDataObject) {
					label += " (string)";
				} else if (dobj instanceof IntegerDataObject) {
					label += " (integer)";
				} else {
					label += " = \"" + dobj.getValue() + "\"";
				}
				Object obj = graph.insertVertex(parent, null, label, 20, 20, 30, 30, "DATA_OBJECT");
				graph.updateCellSize((mxCell) obj);
				components.put(dobj, obj);
				
				if (dobj.getObjectOwner() != null) {
					IDataObjectOwner owner = dobj.getObjectOwner();
					if (owner instanceof Sequence) {
						graph.insertEdge(parent, null, null, components.get(dobj), components.get(((Sequence) dobj.getObjectOwner()).getSink()), "EDGE_DOBJ");
					} else {
						graph.insertEdge(parent, null, null, components.get(owner), components.get(dobj), "EDGE_DOBJ");
					}
				}
			}
		} finally {
			graph.getModel().endUpdate();
		}
		

		mxGraphLayout layout = new mxHierarchicalLayout(graph, JLabel.WEST);
		layout.execute(graph.getDefaultParent());
		mxGraphComponent c = new mxGraphComponent(graph);
		return c;
	}
}

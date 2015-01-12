package plg.visualizer.prototype;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import plg.generator.process.ProcessGenerator;
import plg.generator.process.RandomizationConfiguration;
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
import com.mxgraph.layout.orthogonal.mxOrthogonalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

public class PlgVisualizerPrototype {

	static Process p = null;
	static mxGraph graph = new mxGraph();
	static Object parent = graph.getDefaultParent();
	static mxStylesheet stylesheet = graph.getStylesheet();
	static JPanel model = new JPanel(new BorderLayout());

	public static void main(String[] args) {
		JButton b1 = new JButton("Reload");
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshGUI();
			}
		});
		
		JButton b2 = new JButton("New process");
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				p = null;
				update();
				refreshGUI();
			}
		});
		JFrame f = new JFrame("Test Frame");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(800, 600);
		f.setLayout(new BorderLayout());
		f.add(b1, BorderLayout.NORTH);
		f.add(b2, BorderLayout.SOUTH);
		f.add(model, BorderLayout.CENTER);
		f.setVisible(true);
	}
	
	private static void refreshGUI() {
		if (p == null) {
			update();
		}
		
		graph = new mxGraph();
		graph.setAutoSizeCells(true);
		
		parent = graph.getDefaultParent();
		stylesheet = graph.getStylesheet();

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
		stylesheet.putCellStyle("GATEWAY", styleGateway);
		
		Hashtable<String, Object> styleEdges = new Hashtable<String, Object>();
		styleEdges.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
		styleEdges.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
		styleEdges.put(mxConstants.STYLE_STROKECOLOR, "#5a677b");
		styleEdges.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_SEGMENT);
		stylesheet.setDefaultEdgeStyle(styleEdges);
		
		model.removeAll();
		model.add(update());
		model.updateUI();
	}

	private static mxGraphComponent update() {
		if (p == null) {
			p = new Process("test");
			ProcessGenerator.randomizeProcess(p, RandomizationConfiguration.BASIC_VALUES.setDepth(3));
		}
		
		mxGraphLayout layout = new mxHierarchicalLayout(graph, SwingConstants.WEST);
		
		graph.getModel().beginUpdate();
		Map<FlowObject, Object> components = new HashMap<FlowObject, Object>();
		try {
			for (StartEvent se : p.getStartEvents()) {
				Object obj = graph.insertVertex(parent, null, "Start", 20, 20, 20, 20, "EVENT_START");
				components.put(se, obj);
			}
			for (EndEvent ee : p.getEndEvents()) {
				Object obj = graph.insertVertex(parent, null, "End", 20, 20, 20, 20, "EVENT_END");
				components.put(ee, obj);
			}
			for (Task fo : p.getTasks()) {
				Object obj = graph.insertVertex(parent, null, fo.getName(), 20, 20, 80, 30, "TASK");
				graph.updateCellSize((mxCell) obj);
				components.put(fo, obj);
			}
			for (Gateway fo : p.getGateways()) {
				String label = "×";
				if (fo instanceof ParallelGateway) {
					label = "+";
				}
				Object obj = graph.insertVertex(parent, null, label, 20, 20, 30, 30, "GATEWAY");
				components.put(fo, obj);
			}
			for (Sequence s : p.getSequences()) {
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
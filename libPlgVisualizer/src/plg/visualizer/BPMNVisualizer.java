package plg.visualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import plg.model.Component;
import plg.model.Displaceable;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.IDataObjectOwner;
import plg.model.data.IDataObjectOwner.DATA_OBJECT_DIRECTION;
import plg.model.data.IntegerDataObject;
import plg.model.data.StringDataObject;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.gateway.Gateway;
import plg.model.gateway.ParallelGateway;
import plg.model.sequence.Sequence;
import plg.visualizer.listeners.DataObjectListener;
import plg.visualizer.listeners.TaskListener;
import plg.visualizer.util.ImagesCollection;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
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
	
	protected Set<TaskListener> activityListeners = new HashSet<TaskListener>();
	protected Set<DataObjectListener> dataObjectListeners = new HashSet<DataObjectListener>();
	protected Process process;
	protected Map<mxCell, Component> cellsToComponents;
	protected Map<mxCell, Sequence> cellsToSequences;
	protected mxGraph graph;
	protected Object parent;
	protected mxStylesheet stylesheet;
	
	/**
	 * Class constructor
	 * 
	 * @param process the process graph to show
	 */
	public BPMNVisualizer(Process process) {
		updateProcess(process);
	}
	
	/**
	 * This method updates the represented process
	 * 
	 * @param process
	 */
	public void updateProcess(Process process) {
		this.process = process;
		this.cellsToComponents = new HashMap<mxCell, Component>();
		this.cellsToSequences = new HashMap<mxCell, Sequence>();
		this.graph = new mxGraph() {
//			public boolean isCellFoldable(Object cell, boolean collapse) {
//				return false;
//			}
		};
		this.parent = graph.getDefaultParent();
		this.stylesheet = graph.getStylesheet();
		
		setupMxGraph();
		redrawProcess();
		fit();
	}
	
	/**
	 * This method redraws the provided process
	 */
	public void redrawProcess() {
		if (process != null) {
			final mxGraphComponent graphComponent = updateGraph();
//			graphComponent.setBorder(BorderFactory.createEmptyBorder());
			graphComponent.getViewport().setBackground(Color.WHITE);
//			graphComponent.zoomAndCenter();
			
			setLayout(new BorderLayout());
			add(graphComponent, BorderLayout.CENTER);
	
			graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						mxCell cell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
						if (cell != null) {
							final Component component = cellsToComponents.get(cell);
							if (component != null && component instanceof Task) {
								JPopupMenu menu = generateContextMenu((Task) component);
								menu.show(graphComponent.getGraphControl(), e.getX(), e.getY());
							}
						}
					}
				}
			});
		}
	}
	
	/**
	 * This method register the provided {@link TaskListener}
	 * 
	 * @param listener the listener to register
	 */
	public void addTaskListener(TaskListener listener) {
		activityListeners.add(listener);
	}
	
	/**
	 * This method register the provided {@link DataObjectListener}
	 * 
	 * @param listener the listener to register
	 */
	public void addDataObjectListener(DataObjectListener listener) {
		dataObjectListeners.add(listener);
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
		styleTask.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
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
		
//		Hashtable<String, Object> styleTaskTime = new Hashtable<String, Object>();
//		styleTaskTime.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
//		styleTaskTime.put(mxConstants.STYLE_ROUNDED, true);
//		styleTaskTime.put(mxConstants.STYLE_EDITABLE, false);
//		styleTaskTime.put(mxConstants.STYLE_MOVABLE, false);
//		styleTaskTime.put(mxConstants.STYLE_SPACING_TOP, 5);
//		styleTaskTime.put(mxConstants.STYLE_SPACING_RIGHT, 10);
//		styleTaskTime.put(mxConstants.STYLE_SPACING_BOTTOM, 5);
//		styleTaskTime.put(mxConstants.STYLE_SPACING_LEFT, 10);
//		styleTaskTime.put(mxConstants.STYLE_FILLCOLOR, "#cedeef");
//		styleTaskTime.put(mxConstants.STYLE_GRADIENTCOLOR, "ffffff");
//		styleTaskTime.put(mxConstants.STYLE_FONTCOLOR, "#5a677b");
//		styleTaskTime.put(mxConstants.STYLE_STROKECOLOR, "#5a677b");
//		stylesheet.putCellStyle("TASK_TIME", styleTaskTime);
		
		Hashtable<String, Object> styleTimeIcon = new Hashtable<String, Object>();
		styleTimeIcon.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		styleTimeIcon.put(mxConstants.STYLE_FILLCOLOR, "#cedeef");
		stylesheet.putCellStyle("TIME_ICON", styleTimeIcon);

		Hashtable<String, Object> styleDataObject = new Hashtable<String, Object>();
		styleDataObject.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
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
//		styleEdges.put(mxConstants.STYLE_ROUNDED, true);
		styleEdges.put(mxConstants.STYLE_EDGE, mxEdgeStyle.ElbowConnector);
		stylesheet.putCellStyle("EDGE", styleEdges);
		
		Map<String, Object> styleEdgesDobj = new Hashtable<String, Object>();
		styleEdgesDobj.put(mxConstants.STYLE_EDGE, mxEdgeStyle.EntityRelation);
		styleEdgesDobj.put(mxConstants.STYLE_STROKECOLOR, "#666666");
		styleEdgesDobj.put(mxConstants.STYLE_DASHED, true);
		styleEdgesDobj.put(mxConstants.STYLE_ROUNDED, true);
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
		
		Map<Component, Object> components = new HashMap<Component, Object>();
		try {
//			Map<Component, Pair<Object, Object>> gatewaysInputOutput = new HashMap<Component, Pair<Object, Object>>();
			
			for (StartEvent se : process.getStartEvents()) {
				mxCell obj = (mxCell) graph.insertVertex(parent, null, "Start", 20, 20, 20, 20, "EVENT_START");
				components.put(se, obj);
				cellsToComponents.put(obj, se);
			}
			for (EndEvent ee : process.getEndEvents()) {
				mxCell obj = (mxCell) graph.insertVertex(parent, null, "End", 20, 20, 20, 20, "EVENT_END");
				components.put(ee, obj);
				cellsToComponents.put(obj, ee);
			}
			for (Task fo : process.getTasks()) {
				mxCell obj = (mxCell) graph.insertVertex(parent, fo.getId(), fo.getName(), 20, 20, 80, 30, "TASK");
				
				if (fo.getActivityScript() != null && !fo.getActivityScript().getScript().isEmpty()) {
					
					mxGeometry geo1 = new mxGeometry(0.5, 0.5, 5,5);
					geo1.setRelative(true);
					
					mxCell time = new mxCell(null, geo1, "TIME_ICON");
//					time.setVertex(true);
					
					graph.addCell(time, obj);
				}
				
				graph.updateCellSize(obj);
				components.put(fo, obj);
				cellsToComponents.put(obj, fo);
			}
			for (Gateway fo : process.getGateways()) {
				String label = "x";
				if (fo instanceof ParallelGateway) {
					label = "+";
				}
				mxCell obj = (mxCell) graph.insertVertex(parent, null, label, 20, 20, 30, 30, "GATEWAY");
				obj.setConnectable(false);
				
//				// incoming port
//				mxGeometry geo1 = new mxGeometry(0, 0.5, 3,3);
//				geo1.setRelative(true);
//				mxCell port1 = new mxCell(null, geo1, null);
//				port1.setVertex(true);
//				graph.addCell(port1, obj);
				
//				// outgoing port
//				mxGeometry geo2 = new mxGeometry(1.0, 0.5, 3,3);
//				geo2.setRelative(true);
//				mxCell port2 = new mxCell(null, geo2, null);
//				port2.setVertex(true);
//				graph.addCell(port2, obj);
				
//				gatewaysInputOutput.put(fo, new Pair<Object, Object>(port1, port2));
				components.put(fo, obj);
				cellsToComponents.put(obj, fo);
			}
			for (Sequence s : process.getSequences()) {
				if (components.containsKey(s.getSource()) && components.containsKey(s.getSink())) {
					Object source = components.get(s.getSource());
					Object sink = components.get(s.getSink());
					
//					if (s.getSource() instanceof Gateway) {
//						graph.insertEdge(parent, null, null, gatewaysInputOutput.get(s.getSource()).getFirst(), sink, "EDGE");
//					} else if (s.getSink() instanceof Gateway) {
//						graph.insertEdge(parent, null, null, source, gatewaysInputOutput.get(s.getSink()).getSecond(), "EDGE");
//					} else {
						mxCell edge = (mxCell) graph.insertEdge(parent, null, null, source, sink, "EDGE");
//					}
					cellsToSequences.put(edge, s);
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
				mxCell obj = (mxCell) graph.insertVertex(parent, null, label, 20, 20, 30, 30, "DATA_OBJECT");
				graph.updateCellSize((mxCell) obj);
				components.put(dobj, obj);
				
				if (dobj.getObjectOwner() != null) {
					IDataObjectOwner owner = dobj.getObjectOwner();
					DATA_OBJECT_DIRECTION direction = dobj.getDirectionOwner();
					if (direction == DATA_OBJECT_DIRECTION.REQUIRED) {
						graph.insertEdge(parent, null, null, components.get(dobj), components.get(owner), "EDGE_DOBJ");
					} else {
						graph.insertEdge(parent, null, null, components.get(owner), components.get(dobj), "EDGE_DOBJ");
					}
				}
				cellsToComponents.put(obj, dobj);
			}
		} finally {
			graph.getModel().endUpdate();
		}

		// do the layout
		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph, JLabel.WEST);
		layout.execute(graph.getDefaultParent());
		mxGraphComponent c = new mxGraphComponent(graph);
		
		// store the layout information
		for (mxCell cell : cellsToComponents.keySet()) {
			Component component = cellsToComponents.get(cell);
			if (component instanceof Displaceable) {
				mxGeometry g = cell.getGeometry();
				((Displaceable) component).setLocation((int) g.getX(), (int) g.getY());
				((Displaceable) component).setDimensions((int) g.getWidth(), (int) g.getHeight());
			}
		}
		for (mxCell cell : cellsToSequences.keySet()) {
			Sequence sequence = cellsToSequences.get(cell);
			for (mxPoint p : graph.getView().getState(cell).getAbsolutePoints()) {
				sequence.addPoint((int) p.getX(), (int) p.getY());
			}
		}
		
		return c;
	}
	
	public JPopupMenu generateContextMenu(final Task task) {
		// activity data objects
		JMenu dataObjActivity = new JMenu("Generated Data Object");
		dataObjActivity.setIcon(ImagesCollection.ICON_DATA_OBJ);
		boolean added = false;
		for (final DataObject obj : task.getDataObjects(DATA_OBJECT_DIRECTION.GENERATED)) {
			JMenu objMenu = new JMenu(obj.getName());
			JMenuItem objMenuEdit = new JMenuItem("Edit", ImagesCollection.ICON_DATA_OBJ_EDIT);
			JMenuItem objMenuDelete = new JMenuItem("Delete", ImagesCollection.ICON_DATA_OBJ_DELETE);
			
			objMenuEdit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (DataObjectListener l : dataObjectListeners) {
						l.editDataObjects(obj);
					}
				}
			});
			objMenuDelete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (DataObjectListener l : dataObjectListeners) {
						l.removeDataObjects(obj);
					}
				}
			});
			
			objMenu.add(objMenuEdit);
			objMenu.add(objMenuDelete);
			
			dataObjActivity.add(objMenu);
			added = true;
		}
		
		JMenuItem addDataObjPlain = new JMenuItem("Plain data object");
		JMenuItem addDataObjScriptInteger = new JMenuItem("Script data object (integer)");
		JMenuItem addDataObjScriptString = new JMenuItem("Script data object (string)");
		addDataObjPlain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (DataObjectListener l : dataObjectListeners) {
					l.addDataObjects(task, DATA_OBJECT_DIRECTION.GENERATED, DataObject.class);
				}
			}
		});
		addDataObjScriptInteger.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (DataObjectListener l : dataObjectListeners) {
					l.addDataObjects(task, DATA_OBJECT_DIRECTION.GENERATED, IntegerDataObject.class);
				}
			}
		});
		addDataObjScriptString.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (DataObjectListener l : dataObjectListeners) {
					l.addDataObjects(task, DATA_OBJECT_DIRECTION.GENERATED, StringDataObject.class);
				}
			}
		});
		if (added) {
			dataObjActivity.addSeparator();
		}
		JMenu addDataObjMenu = new JMenu("New...");
		addDataObjMenu.setIcon(ImagesCollection.ICON_DATA_OBJ_NEW);
		addDataObjMenu.add(addDataObjPlain);
		addDataObjMenu.add(addDataObjScriptString);
		addDataObjMenu.add(addDataObjScriptInteger);
		dataObjActivity.add(addDataObjMenu);
		
		// incoming data objects
		JMenu dataObjIncoming = new JMenu("Required Data Object");
		dataObjIncoming.setIcon(ImagesCollection.ICON_DATA_OBJ);
		added = false;
		for (final DataObject obj : task.getDataObjects(DATA_OBJECT_DIRECTION.REQUIRED)) {
			JMenu objMenu = new JMenu(obj.getName());
			JMenuItem objMenuEdit = new JMenuItem("Edit", ImagesCollection.ICON_DATA_OBJ_EDIT);
			JMenuItem objMenuDelete = new JMenuItem("Delete", ImagesCollection.ICON_DATA_OBJ_DELETE);
			
			objMenuEdit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (DataObjectListener l : dataObjectListeners) {
						l.editDataObjects(obj);
					}
				}
			});
			objMenuDelete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (DataObjectListener l : dataObjectListeners) {
						l.removeDataObjects(obj);
					}
				}
			});
			
			objMenu.add(objMenuEdit);
			objMenu.add(objMenuDelete);
			
			dataObjIncoming.add(objMenu);
			added = true;
		}
		
		JMenuItem addIncomingDataObjPlain = new JMenuItem("Plain data object");
		JMenuItem addIncomingDataObjScriptInteger = new JMenuItem("Script data object (integer)");
		JMenuItem addIncomingDataObjScriptString = new JMenuItem("Script data object (string)");
		addIncomingDataObjPlain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (DataObjectListener l : dataObjectListeners) {
					l.addDataObjects(task, DATA_OBJECT_DIRECTION.REQUIRED, DataObject.class);
				}
			}
		});
		addIncomingDataObjScriptInteger.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (DataObjectListener l : dataObjectListeners) {
					l.addDataObjects(task, DATA_OBJECT_DIRECTION.REQUIRED, IntegerDataObject.class);
				}
			}
		});
		addIncomingDataObjScriptString.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (DataObjectListener l : dataObjectListeners) {
					l.addDataObjects(task, DATA_OBJECT_DIRECTION.REQUIRED, StringDataObject.class);
				}
			}
		});
		if (added) {
			dataObjIncoming.addSeparator();
		}
		JMenu addIncomingDataObjMenu = new JMenu("New...");
		addIncomingDataObjMenu.setIcon(ImagesCollection.ICON_DATA_OBJ_NEW);
		addIncomingDataObjMenu.add(addIncomingDataObjPlain);
		addIncomingDataObjMenu.add(addIncomingDataObjScriptString);
		addIncomingDataObjMenu.add(addIncomingDataObjScriptInteger);
		dataObjIncoming.add(addIncomingDataObjMenu);
		
		// time menus
		JMenuItem duration = new JMenuItem("Activity time", ImagesCollection.ICON_TIME);
		duration.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (TaskListener l : activityListeners) {
					l.setTaskTime(task);
				}
			}
		});
		
		JPopupMenu menu = new JPopupMenu();
		menu.add(duration);
		menu.addSeparator();
		menu.add(dataObjActivity);
		menu.add(dataObjIncoming);
		
		return menu;
	}
	
	/**
	 * This method fits the graph to the actual available size
	 */
	public void fit() {
//		mxGraphView view = graph.getView();
//		int compLen = getWidth();
//		if (compLen == 0) {
//			compLen = getParent().getWidth() - 10;
//		}
//		int viewLen = (int) view.getGraphBounds().getWidth();
//		double scaleLen = Math.min((double) compLen / viewLen * view.getScale(), 1.0);
//		
//		int compHeight = getHeight();
//		if (compHeight == 0) {
//			compHeight = getParent().getHeight() - 10;
//		}
//		int viewHeight = (int) view.getGraphBounds().getHeight();
//		double scaleHeight = Math.min((double) compHeight / viewHeight * view.getScale(), 1.0);
//		
//		view.setScale(Math.min(scaleLen, scaleHeight));
	}
}

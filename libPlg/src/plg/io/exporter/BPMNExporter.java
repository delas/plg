package plg.io.exporter;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.camunda.bpm.model.xml.impl.util.IoUtil;

import plg.annotations.Exporter;
import plg.generator.IProgressVisualizer;
import plg.model.Component;
import plg.model.Displaceable;
import plg.model.Process;
import plg.model.sequence.Sequence;
import plg.utils.Logger;
import plg.utils.Pair;

/**
 * This class contains the exporter to BPMN XML 2.0 files.
 * 
 * @author Andrea Burattin
 */
@Exporter(
	name = "BPMN 2.0 XML file",
	fileExtension = "bpmn"
)
public class BPMNExporter extends FileExporter {

	private BpmnModelInstance modelInstance;
	private Definitions definitions;
	private BpmnDiagram bpmnDiagram;
	private BpmnPlane processPlane;
	private org.camunda.bpm.model.bpmn.instance.Process process;
	
	@Override
	public void exportModel(Process model, String filename, IProgressVisualizer progress) {
		progress.setMinimum(0);
		progress.setMaximum(model.getComponents().size());
		progress.setText("Exporting BPMN file...");
		progress.start();
		
		Logger.instance().info("Starting process exportation");
		createEmptyModel(model.getName());
		
		// export the nodes
		Logger.instance().debug("Nodes conversion");
		for (Component node : model.getComponents()) {
			BaseElement element = null;
			if (node instanceof plg.model.activity.Task) {
				element = createElement(process, "element" + node.getId(), Task.class);
				((Task) element).setName(((plg.model.activity.Task) node).getName());
			}
			if (node instanceof plg.model.event.StartEvent) {
				element = createElement(process, "element" + node.getId(), StartEvent.class);
				((StartEvent) element).setName("Start");
			}
			if (node instanceof plg.model.event.EndEvent) {
				element = createElement(process, "element" + node.getId(), EndEvent.class);
				((EndEvent) element).setName("End");
			}
			if (node instanceof plg.model.gateway.ParallelGateway) {
				element = createElement(process, "element" + node.getId(), ParallelGateway.class);
			}
			if (node instanceof plg.model.gateway.ExclusiveGateway) {
				element = createElement(process, "element" + node.getId(), ExclusiveGateway.class);
			}
			/*if (node instanceof plg.model.data.DataObject) {
				element = createElement(process, "element" + node.getId(), DataObject.class);
				((DataObject) element).setName(((plg.model.data.DataObject) node).getName());
			}*/

			// add DI information
			if (node instanceof Displaceable && !(node instanceof plg.model.data.DataObject)) {
				Displaceable nodeDisplacable = (Displaceable) node;
				
				BpmnShape taskShape = modelInstance.newInstance(BpmnShape.class);
				taskShape.setId("shape" + node.getId());
				taskShape.setBpmnElement(element);
				processPlane.getDiagramElements().add(taskShape);
	
				Bounds taskBounds = modelInstance.newInstance(Bounds.class);
				taskBounds.setWidth(nodeDisplacable.getDimensions().getFirst());
				taskBounds.setHeight(nodeDisplacable.getDimensions().getSecond());
				taskBounds.setX(nodeDisplacable.getLocation().getFirst());
				taskBounds.setY(nodeDisplacable.getLocation().getSecond());
				taskShape.setBounds(taskBounds);
				
				progress.inc();
			}
		}
		
		// export the sequences
		Logger.instance().debug("Sequences conversion");
		for (Component component : model.getComponents()) {
			if (component instanceof Sequence) {
				Sequence edge = (Sequence) component;
				
				FlowNode source = modelInstance.getModelElementById("element" + edge.getSource().getId());
				FlowNode target = modelInstance.getModelElementById("element" + edge.getSink().getId());
				
				SequenceFlow sequenceFlow = createSequenceFlow(process, source, target);
				sequenceFlow.setImmediate(true);
	
				BpmnEdge flowEdge = modelInstance.newInstance(BpmnEdge.class);
				flowEdge.setId("flowEdge" + source.getId() + "-" + target.getId());
				flowEdge.setBpmnElement(sequenceFlow);
				processPlane.getDiagramElements().add(flowEdge);
	
				// copy the bend points GEF computed, #584
				for (Pair<Integer, Integer> p : edge.getPoints()) {
					Waypoint waypoint = modelInstance.newInstance(Waypoint.class);
					waypoint.setX(p.getFirst());
					waypoint.setY(p.getSecond());
	
					flowEdge.getWaypoints().add(waypoint);
				}
				progress.inc();
			}
		}
		Logger.instance().debug("Sequences exported");
		progress.setIndeterminate(true);

		try {
			FileUtils.writeStringToFile(new File(filename), IoUtil.convertXmlDocumentToString(modelInstance.getDocument()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger.instance().info("Process exportation complete");
		
		progress.finished();
	}
	
	/**
	 * 
	 * @param processId
	 */
	private void createEmptyModel(String processId) {
		modelInstance = Bpmn.createEmptyModel();
		definitions = modelInstance.newInstance(Definitions.class);
		definitions.setTargetNamespace("http://plg.processmining.it/");
		modelInstance.setDefinitions(definitions);
		process = createElement(definitions, processId, org.camunda.bpm.model.bpmn.instance.Process.class);

		bpmnDiagram = modelInstance.newInstance(BpmnDiagram.class);
		bpmnDiagram.setId("diagram");
		bpmnDiagram.setName("diagram");
		bpmnDiagram.setDocumentation("bpmn diagram element");
		bpmnDiagram.setResolution(120.0);
		modelInstance.getDefinitions().addChildElement(bpmnDiagram);

		processPlane = modelInstance.newInstance(BpmnPlane.class);
		processPlane.setId("plane");
		processPlane.setBpmnElement(process);
		bpmnDiagram.setBpmnPlane(processPlane);
	}
	
	/**
	 * 
	 * @param process
	 * @param from
	 * @param to
	 * @return
	 */
	private SequenceFlow createSequenceFlow(org.camunda.bpm.model.bpmn.instance.Process process, FlowNode from, FlowNode to) {
		String id = from.getId() + "-" + to.getId();
		SequenceFlow sequenceFlow = createElement(process, id, SequenceFlow.class);
		process.addChildElement(sequenceFlow);
		sequenceFlow.setSource(from);
		from.getOutgoing().add(sequenceFlow);
		sequenceFlow.setTarget(to);
		to.getIncoming().add(sequenceFlow);
		return sequenceFlow;
	}
	
	/**
	 * 
	 * @param parentElement
	 * @param id
	 * @param elementClass
	 * @return
	 */
	private <T extends BpmnModelElementInstance> T createElement(BpmnModelElementInstance parentElement, String id, Class<T> elementClass) {
		T element = modelInstance.newInstance(elementClass);
		element.setAttributeValue("id", id, true);
		parentElement.addChildElement(element);
		return element;
	}
}

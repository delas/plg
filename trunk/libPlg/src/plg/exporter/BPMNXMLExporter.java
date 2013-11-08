package plg.exporter;

import java.io.File;
import java.io.IOException;

import org.deckfour.spex.SXDocument;
import org.deckfour.spex.SXTag;

import plg.model.Component;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.sequence.Sequence;

public class BPMNXMLExporter implements AbstractExporter {

	@Override
	public void exportModel(Process model, String filename) {
		try {
			File file = new File(filename);
			SXDocument doc = new SXDocument(file);
			
			SXTag definitions = doc.addNode("definitions");
			definitions.addAttribute("id", "process_" + model.getName());
			// oryx definitions
			definitions.addAttribute("typeLanguage", "http://www.w3.org/2001/XMLSchema");
			definitions.addAttribute("expressionLanguage", "http://www.w3.org/1999/XPath");
			definitions.addAttribute("targetNamespace", "http://www.omg.org/bpmn20");
			definitions.addAttribute("xmlns", "http://schema.omg.org/spec/BPMN/2.0");
			definitions.addAttribute("xmlns:bpmndi", "http://bpmndi.org");
			// yaoquiang definitions
//			definitions.addAttribute("xmlns", "http://www.omg.org/spec/BPMN/20100524/MODEL");
//			definitions.addAttribute("xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI");
//			definitions.addAttribute("xmlns:dc", "http://www.omg.org/spec/DD/20100524/DC");
//			definitions.addAttribute("xmlns:di", "http://www.omg.org/spec/DD/20100524/DI");
//			definitions.addAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
//			definitions.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
//			definitions.addAttribute("expressionLanguage", "http://www.w3.org/1999/XPath");
//			definitions.addAttribute("typeLanguage", "http://www.w3.org/2001/XMLSchema");
//			definitions.addAttribute("targetNamespace", "http://www.omg.org/bpmn20");
//			definitions.addAttribute("xsi:schemaLocation", "http://www.omg.org/spec/BPMN/20100524/MODEL http://bpmn.sourceforge.net/schemas/BPMN20.xsd");
			
			// process
			SXTag process = definitions.addChildNode("process");
			process.addAttribute("id", model.getName());
			
			SXTag lanes = process.addChildNode("laneSet");
			lanes.addAttribute("id", model.getName() + "_laneSet");
			SXTag lane = lanes.addChildNode("lane");
			lane.addAttribute("name", "DefaultLane");
			lane.addAttribute("id", model.getName() + "_lane1");
			for (Component c : model.getComponents()) {
				lane.addChildNode("flowElementRef").addTextNode(c.getComponentId());
			}
			
			for(StartEvent se : model.getStartEvents()) {
				SXTag seTag = process.addChildNode("startEvent");
				seTag.addAttribute("id", se.getComponentId());
			}
			for(Task t : model.getTasks()) {
				SXTag tTag = process.addChildNode("task");
				tTag.addAttribute("id", t.getComponentId());
				tTag.addAttribute("name", t.getName());
			}
			for(EndEvent ee : model.getEndEvents()) {
				SXTag eeTag = process.addChildNode("endEvent");
				eeTag.addAttribute("id", ee.getComponentId());
			}
			for(Sequence s : model.getSequences()) {
				SXTag sTag = process.addChildNode("sequenceFlow");
				sTag.addAttribute("id", s.getComponentId());
				sTag.addAttribute("sourceRef", s.getSource().getComponentId());
				sTag.addAttribute("targetRef", s.getSink().getComponentId());
			}
			
			
			// process diagram
			SXTag processDiagram = definitions.addChildNode("bpmndi:processDiagram");
			processDiagram.addAttribute("processRef", model.getName());
			processDiagram.addAttribute("id", model.getName() + "_gui");
			
			SXTag laneCompartment = processDiagram.addChildNode("bpmndi:laneCompartment");
			laneCompartment.addAttribute("isVisible", "false");
			laneCompartment.addAttribute("height", "0.0");
			laneCompartment.addAttribute("width", "0.0");
			laneCompartment.addAttribute("x", "0.0");
			laneCompartment.addAttribute("y", "0.0");
			laneCompartment.addAttribute("name", "DefaultLane");
			laneCompartment.addAttribute("id", model.getName() + "_lane1");
			
			Integer xPosition = 100;
			
			for(StartEvent se : model.getStartEvents()) {
				SXTag seTag = laneCompartment.addChildNode("bpmndi:eventShape");
				seTag.addAttribute("eventRef", se.getComponentId());
				seTag.addAttribute("height", "28.0");
				seTag.addAttribute("width", "28.0");
				seTag.addAttribute("x", xPosition.toString());
				seTag.addAttribute("y", "200.0");
				seTag.addAttribute("name", "");
				seTag.addAttribute("id", se.getComponentId() + "_gui");
				xPosition += 28 + 50;
			}
			for(Task t : model.getTasks()) {
				SXTag tTag = laneCompartment.addChildNode("bpmndi:activityShape");
				tTag.addAttribute("activityRef", t.getComponentId());
				tTag.addAttribute("height", "80.0");
				tTag.addAttribute("width", "100.0");
				tTag.addAttribute("y", "200.0");
				tTag.addAttribute("x", xPosition.toString());
				tTag.addAttribute("name", t.getName());
				tTag.addAttribute("id", t.getComponentId() + "_gui");
				xPosition += 100 + 50;
			}
			for(EndEvent ee : model.getEndEvents()) {
				SXTag eeTag = laneCompartment.addChildNode("bpmndi:eventShape");
				eeTag.addAttribute("eventRef", ee.getComponentId());
				eeTag.addAttribute("height", "28.0");
				eeTag.addAttribute("width", "28.0");
				eeTag.addAttribute("x", xPosition.toString());
				eeTag.addAttribute("y", "200.0");
				eeTag.addAttribute("name", "");
				eeTag.addAttribute("id", ee.getComponentId() + "_gui");
				xPosition += 28 + 50;
			}
			
			for(Sequence s : model.getSequences()) {
				SXTag sTag = processDiagram.addChildNode("bpmndi:sequenceFlowConnector");
				sTag.addAttribute("sequenceFlowRef", s.getComponentId());
				sTag.addAttribute("label", "");
				sTag.addAttribute("sourceRef", s.getSource().getComponentId() + "_gui");
				sTag.addAttribute("targetRef", s.getSink().getComponentId() + "_gui");
				sTag.addAttribute("id", s.getComponentId() + "_gui");
			}
			
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

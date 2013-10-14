package plg.exporter;

import java.io.File;
import java.io.IOException;

import org.deckfour.spex.SXDocument;
import org.deckfour.spex.SXTag;

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
			definitions.addAttribute("typeLanguage", "http://www.w3.org/2001/XMLSchema");
			definitions.addAttribute("expressionLanguage", "http://www.w3.org/1999/XPath");
			definitions.addAttribute("targetNamespace", "http://www.omg.org/bpmn20");
			definitions.addAttribute("xmlns", "http://schema.omg.org/spec/BPMN/2.0");
			definitions.addAttribute("xmlns:bpmndi", "http://bpmndi.org");
			
			SXTag process = definitions.addChildNode("process");
			process.addAttribute("id", model.getName());

			for(StartEvent se : model.getStartEvents()) {
				SXTag seTag = process.addChildNode("startEvent");
				seTag.addAttribute("id", "" + se.getComponentId());
			}
			for(Task t : model.getTasks()) {
				SXTag tTag = process.addChildNode("task");
				tTag.addAttribute("id", "" + t.getComponentId());
				tTag.addAttribute("name", "" + t.getName());
			}
			for(EndEvent ee : model.getEndEvents()) {
				SXTag eeTag = process.addChildNode("endEvent");
				eeTag.addAttribute("id", "" + ee.getComponentId());
			}
			for(Sequence s : model.getSequences()) {
				SXTag sTag = process.addChildNode("sequenceFlow");
				sTag.addAttribute("id", "" + s.getComponentId());
				sTag.addAttribute("sourceRef", "" + s.getSource().getComponentId());
				sTag.addAttribute("targetRef", "" + s.getSink().getComponentId());
			}
			
			SXTag processDiagram = definitions.addChildNode("bpmndi:processDiagram");
			processDiagram.addAttribute("processRef", model.getName());
			processDiagram.addAttribute("id", model.getName() + "_gui");
			
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

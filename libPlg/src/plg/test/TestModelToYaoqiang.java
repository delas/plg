package plg.test;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidDataObject;
import plg.exceptions.InvalidProcessException;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.gateway.Gateway;

public class TestModelToYaoqiang {
	public static void main(String[] args) throws IllegalSequenceException,
			InvalidProcessException, InvalidDataObject, FileNotFoundException,
			IOException, InterruptedException, ParserConfigurationException,
			TransformerException {

		Process p = generateProcess(); // new Process("test");
		// ProcessGenerator.randomizeProcess(p,
		// RandomizationConfiguration.BASIC_VALUES);
		System.out.println("Process randomization complete");

//		Map<String, FlowElement> yqMap = new HashMap<String, FlowElement>();
//		Definitions yqDefinitions = new Definitions();
//		BPMNProcess yqProcess = new BPMNProcess(yqDefinitions);
//		for (StartEvent se : p.getStartEvents()) {
//			FlowElement fe = new org.yaoqiang.model.bpmn.elements.events.StartEvent(se.getComponentId());
//			yqProcess.addFlowElement(fe);
//			yqMap.put(se.getComponentId(), fe);
//		}
//		for (EndEvent ee : p.getEndEvents()) {
//			FlowElement fe = new org.yaoqiang.model.bpmn.elements.events.EndEvent(ee.getComponentId());
//			yqProcess.addFlowElement(fe);
//			yqMap.put(ee.getComponentId(), fe);
//		}
//		for (Task t : p.getTasks()) {
//			FlowElement fe = new ManualTask(t.getComponentId());
//			yqProcess.addFlowElement(fe);
//			yqMap.put(t.getComponentId(), fe);
//		}
//		for (Gateway g : p.getGateways()) {
//			FlowElement fe = null;
//			if (g instanceof ParallelGateway) {
//				fe = new org.yaoqiang.model.bpmn.elements.gateways.ParallelGateway(g.getComponentId());
//			} else if (g instanceof ExclusiveGateway) {
//				fe = new org.yaoqiang.model.bpmn.elements.gateways.EventBasedGateway(g.getComponentId());
//			}
//			yqProcess.addFlowElement(fe);
//			yqMap.put(g.getComponentId(), fe);
//		}
//		for (Sequence s : p.getSequences()) {
//			SequenceFlow sf = new SequenceFlow(null);
//			sf.setSourceRef(s.getSource().getComponentId());
//			sf.setTargetRef(s.getSink().getComponentId());
//			yqProcess.addFlowElement(sf);
//		}
//
//		yqDefinitions.addRootElement(yqProcess);
//
//		System.out.println(yqDefinitions.getProcesses());
//
//		BPMNModelUtils.refreshTypes(yqDefinitions);
//		BPMNModelUtils.fillAllFlowNodeSequenceFlowRefs(yqDefinitions);
//
//		System.out.println(yqDefinitions.getItemDefinitions());
//
//		BPMNModelCodec yqCodec = new BPMNModelCodec();
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder = factory.newDocumentBuilder();
//		DOMImplementation impl = builder.getDOMImplementation();
//		Document doc = impl.createDocument(null, null, null);
//
//		yqCodec.encode(doc, BPMNModelUtils.getDefinitions(yqProcess));
//
//		TransformerFactory transformerFactory = TransformerFactory.newInstance();
//		Transformer transformer = transformerFactory.newTransformer();
//		DOMSource source = new DOMSource(doc);
//		// StreamResult result = new StreamResult(System.out);
//		StreamResult result = new StreamResult(new File("/home/delas/desktop/aaa.xml"));
//		transformer.transform(source, result);
	}
	
	private static Process generateProcess() throws IllegalSequenceException,
			InvalidProcessException {
		Process p = new Process("test");
		StartEvent start = p.newStartEvent();
		EndEvent end = p.newEndEvent();
		Gateway split = p.newParallelGateway();
		Gateway join = p.newParallelGateway();
		Gateway loop2_split = p.newExclusiveGateway();
		Gateway loop2_join = p.newExclusiveGateway();
		Task a = p.newTask("a");
		Task d = p.newTask("d");
		Task i = p.newTask("i");
		Task l = p.newTask("l");
		Task m = p.newTask("m");
		Task z = p.newTask("z");
		p.newSequence(start, a);
		p.newSequence(a, loop2_join);
		p.newSequence(loop2_join, m);
		p.newSequence(m, split);
		p.newSequence(split, d);
		p.newSequence(split, i);
		p.newSequence(d, join);
		p.newSequence(i, join);
		p.newSequence(join, z);
		p.newSequence(z, loop2_split);
		p.newSequence(loop2_split, l);
		p.newSequence(loop2_split, loop2_join);
		p.newSequence(l, end);
		p.check();
		return p;
	}
}

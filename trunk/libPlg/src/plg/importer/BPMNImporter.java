package plg.importer;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import plg.generator.scriptexecuter.IntegerScriptExecutor;
import plg.generator.scriptexecuter.StringScriptExecutor;
import plg.model.Component;
import plg.model.FlowObject;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.IntegerDataObject;
import plg.model.data.StringDataObject;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.gateway.ExclusiveGateway;
import plg.model.gateway.ParallelGateway;
import plg.model.sequence.Sequence;

public class BPMNImporter implements AbstractImporter {

	private static final Namespace ns = Namespace.getNamespace("http://schema.omg.org/spec/BPMN/2.0");
	private static final Pattern REGEX_SIMPLE = Pattern.compile("(\\S+)\\s*=\\s*(\\S+)");
	private static final Pattern REGEX_INTEGER_SCRIPT = Pattern.compile("(?i)\\s*(\\S+)\\s*\\(\\s*Integer\\s*\\)\\s*");
	private static final Pattern REGEX_STRING_SCRIPT = Pattern.compile("(?i)\\s*(\\S+)\\s*\\(\\s*String\\s*\\)\\s*");
	
	@Override
	public Process importModel(String filename) {
		HashMap<String, Component> inverseKey = new HashMap<String, Component>();
		HashMap<String, Set<Task>> dataObjectToTasks = new HashMap<String, Set<Task>>();
		
		Process p = null;
		try {
			FileInputStream input = new FileInputStream(filename);
			
			SAXBuilder builder = new SAXBuilder();
			Document document = (Document) builder.build(input);
			Element definitions = document.getRootElement();
			Element process = definitions.getChild("process", ns);
			p = new Process(process.getAttributeValue("id"));
			
			// Events (start and end)
			for (Element ss : process.getChildren("startEvent", ns)) {
				StartEvent s = new StartEvent(p);
				inverseKey.put(ss.getAttributeValue("id"), s);
			}
			for (Element es : process.getChildren("endEvent", ns)) {
				EndEvent e = new EndEvent(p);
				inverseKey.put(es.getAttributeValue("id"), e);
			}
			// Tasks
			for (Element ts : process.getChildren("task", ns)) {
				Task t = new Task(p, ts.getAttributeValue("name"));
				inverseKey.put(ts.getAttributeValue("id"), t);
				for (Element doa : ts.getChildren("dataOutputAssociation", ns)) {
					Set<Task> s = new HashSet<Task>();
					if (dataObjectToTasks.containsKey(doa.getChildText("targetRef", ns))) {
						s = dataObjectToTasks.get(doa.getChildText("targetRef", ns));
					}
					s.add(t);
					dataObjectToTasks.put(doa.getChildText("targetRef", ns), s);
				}
			}
			// Gateways
			for (Element gs : process.getChildren("exclusiveGateway", ns)) {
				ExclusiveGateway g = new ExclusiveGateway(p);
				inverseKey.put(gs.getAttributeValue("id"), g);
			}
			for (Element gs : process.getChildren("parallelGateway", ns)) {
				ParallelGateway g = new ParallelGateway(p);
				inverseKey.put(gs.getAttributeValue("id"), g);
			}
			// Sequences
			for (Element ss : process.getChildren("sequenceFlow", ns)) {
				FlowObject source = (FlowObject) inverseKey.get(ss.getAttributeValue("sourceRef"));
				FlowObject sink = (FlowObject) inverseKey.get(ss.getAttributeValue("targetRef"));
				new Sequence(p, source, sink);
			}
			// Data Objects
			for (Element ds : process.getChildren("dataObject", ns)) {
				String doId = ds.getAttributeValue("id");
				for(Task t : dataObjectToTasks.get(doId)) {
					parseDataObject(ds, t);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p;
	}
	
	private DataObject parseDataObject(Element dataObjectElement, FlowObject owner) {
		DataObject dataObject = null;
		String name = dataObjectElement.getAttributeValue("name");
		Matcher matcherSimple = REGEX_SIMPLE.matcher(name);
		Matcher matcherIntegerScript = REGEX_INTEGER_SCRIPT.matcher(name);
		Matcher matcherStringScript = REGEX_STRING_SCRIPT.matcher(name);
		
		if (matcherSimple.matches()) {
			dataObject = new DataObject(owner);
			dataObject.setName(matcherSimple.group(1));
			dataObject.setValue(matcherSimple.group(2));
		} else if (matcherIntegerScript.matches()) {
			String script = dataObjectElement.getChildText("documentation", ns);
			dataObject = new IntegerDataObject(owner, new IntegerScriptExecutor(script));
			dataObject.setName(matcherIntegerScript.group(1));
		} else if (matcherStringScript.matches()) {
			String script = dataObjectElement.getChildText("documentation", ns);
			dataObject = new StringDataObject(owner, new StringScriptExecutor(script));
			dataObject.setName(matcherStringScript.group(1));
		}
		
		return dataObject;
	}
}

package plg.test;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidDataObject;
import plg.exceptions.InvalidProcessException;
import plg.exporter.BPMNXMLExporter;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.TimeDataObject;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;

public class TestModel {

	public static void main(String[] args) throws IllegalSequenceException, InvalidProcessException, InvalidDataObject {
		Process p = new Process("test");
		
		StartEvent start = p.newStartEvent();
		EndEvent end = p.newEndEvent();
		
		Task a = p.newTask("a");
		Task b = p.newTask("b");
		Task c = p.newTask("c");
		
		TimeDataObject tdo = new TimeDataObject(a, 0, 100);
		System.out.println(tdo.getValue());
		
		DataObject a_d1 = new DataObject(a);
		a_d1.set("test_string", "test value");
		DataObject a_d2 = new DataObject(a);
		a_d2.set("test_integer", 1);

		p.newSequence(start, a);
		p.newSequence(a, b);
		p.newSequence(b, a);
		p.newSequence(b, c);
		p.newSequence(c, end);
		
		p.check();
		
		BPMNXMLExporter exporter = new BPMNXMLExporter();
		exporter.exportModel(p, "/home/delas/desktop/p.xml");
		
		System.out.println("ok");
	}
}

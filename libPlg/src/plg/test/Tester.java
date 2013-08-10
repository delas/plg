package plg.test;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidProcessException;
import plg.exporter.BPMNXMLExporter;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;

public class Tester {

	public static void main(String[] args) throws IllegalSequenceException, InvalidProcessException {
		Process p = new Process("test");
		
		StartEvent start = p.newStartEvent();
		EndEvent end = p.newEndEvent();
		
		Task a = p.newTask("a");
		Task b = p.newTask("b");
		Task c = p.newTask("c");

		p.newSequence(start, a);
		p.newSequence(a, b);
		p.newSequence(b, a);
		p.newSequence(b, c);
		p.newSequence(c, end);
		
		p.check();
		
		System.out.println("ok");
	}
}

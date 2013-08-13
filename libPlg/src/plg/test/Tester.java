package plg.test;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidProcessException;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
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
		
		System.out.println("ok");
	}
}

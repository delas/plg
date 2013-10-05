package plg.test;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidProcessException;
import plg.generator.log.LogGenerator;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.gateway.ExclusiveGateway;
import plg.model.gateway.Gateway;
import plg.model.gateway.ParallelGateway;

public class Test {

	public static void main(String[] args) throws InvalidProcessException, IllegalSequenceException {
		Process p = new Process("test");
		
		StartEvent start = p.newStartEvent();
		EndEvent end = p.newEndEvent();
		
		Task a = p.newTask("a");
		Gateway split = new ParallelGateway(p);
		Task b = p.newTask("b");
		Task c = p.newTask("c");
		Gateway join = new ParallelGateway(p);
		Task d = p.newTask("d");
		
		p.newSequence(start, a);
		p.newSequence(a, split);
		p.newSequence(split, b);
		p.newSequence(split, c);
		p.newSequence(b, join);
		p.newSequence(c, join);
		p.newSequence(join, d);
		p.newSequence(d, end);
		p.check();
		
		LogGenerator generator = new LogGenerator(p);
		
		for (int i = 0; i < 10; i++) {
			generator.generateProcessInstance();
			System.out.println("");
		}
		
	}

}

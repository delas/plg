package plg.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XMxmlSerializer;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlSerializer;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidProcessException;
import plg.exceptions.InvalidScript;
import plg.generator.log.LogGenerator;
import plg.generator.scriptexecuter.IntegerScriptExecutor;
import plg.generator.scriptexecuter.StringScriptExecutor;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.IntegerDataObject;
import plg.model.data.StringDataObject;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.gateway.ExclusiveGateway;
import plg.model.gateway.Gateway;
import plg.model.gateway.ParallelGateway;

public class LogGeneratorTest {

	public static void main(String[] args) throws InvalidProcessException, IllegalSequenceException, InvalidScript, FileNotFoundException, IOException {
		System.out.println("start");
		
		Process p = new Process("test");
		
		StartEvent start = p.newStartEvent();
		EndEvent end = p.newEndEvent();
		
		Task a = p.newTask("a");
		Gateway split = new ExclusiveGateway(p);
		Task b = p.newTask("b");
		Task c = p.newTask("c");
		Gateway join = new ExclusiveGateway(p);
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
		
		DataObject do0 = new StringDataObject(b, new StringScriptExecutor("" +
				"from random import randrange;\n" +
				"\n" +
				"def generate(caseId):" +
				"	return \"test-\" + str(randrange(5));"));
			do0.setName("string-test");
			
		DataObject do1 = new IntegerDataObject(b, new IntegerScriptExecutor("" +
			"from random import randrange;\n" +
			"\n" +
			"def generate(caseId):" +
			"	return randrange(500);"));
		do1.setName("cost");
		
		DataObject do3 = new StringDataObject(c, new StringScriptExecutor("" +
				"from random import randrange;\n" +
				"\n" +
				"def generate(caseId):" +
				"	return \"tester-\" + str(5+randrange(5));"));
			do3.setName("string-test");
			
		DataObject do4 = new IntegerDataObject(c, new IntegerScriptExecutor("" +
			"from random import randrange;\n" +
			"\n" +
			"def generate(caseId):" +
			"	return 500 + randrange(500);"));
		do4.setName("cost");
		
		LogGenerator generator = new LogGenerator(p);
		XLog log = generator.generateLog(1000);
		XSerializer serializer = new XMxmlSerializer();
		serializer.serialize(log, new FileOutputStream("/home/delas/desktop/log.mxml"));
		serializer = new XesXmlSerializer();
		serializer.serialize(log, new FileOutputStream("/home/delas/desktop/log.xes"));
		
		System.out.println("done");
	}

}

package plg.stream.test;

import plg.exceptions.IllegalSequenceException;
import plg.generator.log.SimulationConfiguration;
import plg.io.exporter.GraphvizBPMNExporter;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.IDataObjectOwner.DATA_OBJECT_DIRECTION;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.stream.configuration.StreamConfiguration;
import plg.stream.model.Streamer;

public class Tester {

	/*public static void main(String args[]) {
		System.out.println("start");

		XTrace t1 = XLogHelper.createTrace("t1");
		XLogHelper.insertEvent(t1, "a", new Date(115, 1, 1));
		XLogHelper.insertEvent(t1, "b", new Date(115, 1, 2));
		XLogHelper.insertEvent(t1, "c", new Date(115, 1, 5));

		XTrace t2 = XLogHelper.createTrace("t2");
		XLogHelper.insertEvent(t2, "a", new Date(115, 0, 1));
		XLogHelper.insertEvent(t2, "b", new Date(115, 0, 2));
		XLogHelper.insertEvent(t2, "c", new Date(115, 0, 5));

		XTrace t3 = XLogHelper.createTrace("t3");
		XLogHelper.insertEvent(t3, "a", new Date(115, 0, 1));
		XLogHelper.insertEvent(t3, "b", new Date(115, 0, 2));
		XLogHelper.insertEvent(t3, "c", new Date(115, 0, 5));
		
		StreamConfiguration sc = new StreamConfiguration();
		sc.maximumParallelInstances = 1;
		sc.timeFractionBeforeNewTrace = 1;
		StreamBuffer sb = new StreamBuffer(sc);
		sb.enqueueTrace(t1);
		sb.enqueueTrace(t2);
		sb.enqueueTrace(t3);
		
		StreamEvent se = null;
		do {
			se = sb.getEventToStream();
			if (se != null) {
				System.out.println(XLogHelper.getName(se.get(0)) + " " + XLogHelper.getName(se) + " - " + se.getDate());
				System.out.flush();
			}
		} while (se != null);
		
		System.out.println("complete");
	}*/
	
	public static void main(String args[]) throws IllegalSequenceException {
		System.out.println("start1");
		
		StreamConfiguration sc = new StreamConfiguration();
		sc.servicePort = 1234;
		sc.maximumParallelInstances = 10;
		sc.timeFractionBeforeNewTrace = 1;
		sc.markTraceBeginningEnd = true;
		
		Process p = new Process("test");
		StartEvent start = p.newStartEvent();
		Task A = p.newTask("A"); p.newSequence(start, A);
		Task B = p.newTask("B"); p.newSequence(A, B);
		EndEvent end = p.newEndEvent(); p.newSequence(B, end);
		DataObject do1 = new DataObject(p);
		do1.set("do1", "10");
		do1.setObjectOwner(A, DATA_OBJECT_DIRECTION.GENERATED);
		
//		ProcessGenerator.randomizeProcess(p, RandomizationConfiguration.BASIC_VALUES);
		
		GraphvizBPMNExporter e = new GraphvizBPMNExporter();
		e.exportModel(p, "C:\\Users\\Andrea\\Desktop\\model.dot");
		
		Streamer s = new Streamer(sc, p, new SimulationConfiguration());
		s.startStream();
	}
}

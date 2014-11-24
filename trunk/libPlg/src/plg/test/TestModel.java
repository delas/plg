package plg.test;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidDataObject;
import plg.exceptions.InvalidProcessException;
import plg.exporter.Exporter;
import plg.exporter.GraphvizExporter;
import plg.generator.process.ProcessGenerator;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.TimeDataObject;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;

public class TestModel {

	public static void main(String[] args) throws IllegalSequenceException, InvalidProcessException, InvalidDataObject {
		
		Process p = new Process("test");
		ProcessGenerator.randomizeProcess(p);
		
		Exporter exporter = new GraphvizExporter();
		exporter.exportModel(p, "/home/delas/desktop/export");
		
		System.out.println("ok");
	}
}

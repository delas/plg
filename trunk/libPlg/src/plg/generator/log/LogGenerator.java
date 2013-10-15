package plg.generator.log;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import plg.model.Component;
import plg.model.FlowObject;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.GeneratedDataObject;
import plg.model.data.IntegerDataObject;
import plg.model.data.StringDataObject;
import plg.model.data.TimeDataObject;
import plg.model.event.StartEvent;
import plg.model.gateway.ExclusiveGateway;
import plg.model.gateway.ParallelGateway;
import plg.utils.SetUtils;
import plg.utils.XLogHelper;

public class LogGenerator {

	private Process process;
	private Set<Component> observedComponents;
	
	public LogGenerator(Process process) {
		this.process = process;
	}
	
	public XLog generateLog(int noTraces) {
		XLog log = XLogHelper.generateNewXLog("tmp-process");
		for (int i = 0; i < noTraces; i++) {
			generateProcessInstance(log, "case_" + i);
		}
		return log;
	}
	
	public XTrace generateProcessInstance(XLog log, String caseId) {
		observedComponents = new HashSet<Component>();
		XTrace trace = XLogHelper.insertTrace(log, caseId);
		processFlowObject(SetUtils.getRandom(process.getStartEvents()), trace);
		return trace;
	}
	
	private void processFlowObject(FlowObject object, XTrace trace) {

		hookBeforeFlowObjectExecution(object, trace);
		hookAfterFlowObjectExecution(object, trace);
		
		observedComponents.add(object);
		
		if (object instanceof Task ||
				object instanceof ExclusiveGateway ||
				object instanceof StartEvent) {
			FlowObject next = SetUtils.getRandom(object.getOutgoingObjects());
			processFlowObject(next, trace);
		} else if (object instanceof ParallelGateway) {
			if (object.getOutgoingObjects().size() > 1) {
				for (FlowObject fo : SetUtils.randomizeSet(object.getOutgoingObjects())) {
					processFlowObject(fo, trace);
				}
			}
			if (object.getIncomingObjects().size() > 1) {
				boolean observedAll = true;
				for (FlowObject fo : object.getIncomingObjects()) {
					if (!observedComponents.contains(fo)) {
						observedAll = false;
						break;
					}
				}
				if (observedAll) {
					FlowObject next = SetUtils.getRandom(object.getOutgoingObjects());
					processFlowObject(next, trace);
				}
			}
		}
	}
	
	private void hookBeforeFlowObjectExecution(FlowObject object, XTrace trace) {
		if (object instanceof Task) {
			String caseId = XLogHelper.getName(trace);
			XEvent e = XLogHelper.insertEvent(trace, ((Task) object).getName(), new Date(1000 * 60 * 60 * trace.size()));
			for (DataObject dataObj : object.getDataObjects()) {
				if (dataObj instanceof IntegerDataObject) {
					((GeneratedDataObject) dataObj).generateInstance(caseId);
					XLogHelper.decorateElement(e, dataObj.getName(), (Integer) dataObj.getValue());
				} else if (dataObj instanceof StringDataObject) {
					((GeneratedDataObject) dataObj).generateInstance(caseId);
					XLogHelper.decorateElement(e, dataObj.getName(), (String) dataObj.getValue());
				} else if (dataObj instanceof DataObject) {
					XLogHelper.decorateElement(e, dataObj.getName(), (String) dataObj.getValue());
				}
			}
		}
	}
	
	private void hookAfterFlowObjectExecution(FlowObject object, XTrace trace) {
		
	}
}

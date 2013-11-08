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
import plg.model.event.StartEvent;
import plg.model.gateway.ExclusiveGateway;
import plg.model.gateway.ParallelGateway;
import plg.model.sequence.Sequence;
import plg.utils.SetUtils;
import plg.utils.XLogHelper;

/**
 * This class describes a general log generator
 * 
 * @author Andrea Burattin
 */
public class LogGenerator {

	private Process process;
	private Set<Component> observedComponents;
	
	/**
	 * Basic constructor for a log generator
	 * 
	 * @param process the process to use for the log generation
	 */
	public LogGenerator(Process process) {
		this.process = process;
	}
	
	/**
	 * This method returns an {@link XLog} object with the generated log
	 * 
	 * @param noTraces the number of traces to generate
	 * @return the generated log
	 */
	public XLog generateLog(int noTraces) {
		XLog log = XLogHelper.generateNewXLog("tmp-process");
		for (int i = 0; i < noTraces; i++) {
			generateProcessInstance(log, "case_" + i);
		}
		return log;
	}
	
	/**
	 * This method generate a single process instance. This method adds the
	 * generated trace into the given log.
	 * 
	 * @param log the log container
	 * @param caseId the case identifier of the new generated trace
	 * @return the generated trace
	 */
	private XTrace generateProcessInstance(XLog log, String caseId) {
		observedComponents = new HashSet<Component>();
		XTrace trace = XLogHelper.insertTrace(log, caseId);
		processFlowObject(SetUtils.getRandom(process.getStartEvents()), trace);
		
		for (DataObject dataObj : process.getDataObjects()) {
			if (dataObj.getObjectOwner() == null) {
				if (dataObj instanceof IntegerDataObject) {
					((GeneratedDataObject) dataObj).generateInstance(caseId);
					XLogHelper.decorateElement(trace, dataObj.getName(), (Integer) dataObj.getValue());
				} else if (dataObj instanceof StringDataObject) {
					((GeneratedDataObject) dataObj).generateInstance(caseId);
					XLogHelper.decorateElement(trace, dataObj.getName(), (String) dataObj.getValue());
				} else if (dataObj instanceof DataObject) {
					XLogHelper.decorateElement(trace, dataObj.getName(), (String) dataObj.getValue());
				}
			}
		}
		return trace;
	}
	
	/**
	 * This method processes each single process object. This method is
	 * responsible for the flow management.
	 * 
	 * @param object
	 * @param trace
	 */
	private void processFlowObject(FlowObject object, XTrace trace) {
		recordEventExecution(object, trace);
		
		observedComponents.add(object);
		
		if (object instanceof Task ||
				object instanceof ExclusiveGateway ||
				object instanceof StartEvent) {
			FlowObject next = SetUtils.getRandom(object.getOutgoingObjects());
			if (next instanceof Task) {
				Sequence s = object.getOwner().getSequence(object, next);
				if (s.getDataObjects().size() > 0) {
					recordEventAttributes(trace, trace.get(trace.size() - 1), s.getDataObjects());
				}
			}
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
	
	/**
	 * This method generates the {@link XEvent} for the given {@link Task}
	 * object
	 * 
	 * @param object
	 * @param trace
	 */
	private void recordEventExecution(FlowObject object, XTrace trace) {
		if (object instanceof Task) {
			XEvent event = XLogHelper.insertEvent(trace, ((Task) object).getName(), new Date(1000 * 60 * 60 * trace.size()));
			recordEventAttributes(trace, event, object.getDataObjects());
		}
	}
	
	/**
	 * This method decorates an {@link XEvent} with the provided data objects
	 * 
	 * @param trace
	 * @param event
	 * @param dataObjects
	 */
	private void recordEventAttributes(XTrace trace, XEvent event, Set<DataObject> dataObjects) {
		String caseId = XLogHelper.getName(trace);
		for (DataObject dataObj : dataObjects) {
			if (dataObj instanceof IntegerDataObject) {
				((GeneratedDataObject) dataObj).generateInstance(caseId);
				XLogHelper.decorateElement(event, dataObj.getName(), (Integer) dataObj.getValue());
			} else if (dataObj instanceof StringDataObject) {
				((GeneratedDataObject) dataObj).generateInstance(caseId);
				XLogHelper.decorateElement(event, dataObj.getName(), (String) dataObj.getValue());
			} else if (dataObj instanceof DataObject) {
				XLogHelper.decorateElement(event, dataObj.getName(), (String) dataObj.getValue());
			}
		}
	}
}

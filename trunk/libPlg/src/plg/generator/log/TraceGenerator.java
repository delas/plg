package plg.generator.log;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
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
import plg.utils.Pair;
import plg.utils.SetUtils;
import plg.utils.XLogHelper;

/**
 * This class represents an object that is able to generate a single process
 * trace. This class is fed to the {@link SimulationEngine} in order to simulate
 * an entire log.
 * 
 * @author Andrea Burattin
 * @see SimulationEngine
 */
class TraceGenerator implements Runnable {

	private Map<Component, Long> componentsObservationTime;
	private Map<Sequence, Integer> observationsCounter;
	private Set<Sequence> tokens;
	
	private Process process;
	private String caseId;
	private SimulationConfiguration parameters;
	private XTrace generatedTrace = null;
	
	/**
	 * Basic class constructor
	 * 
	 * @param process the process that is going to originate this trace
	 * @param caseId the name to provide to this trace
	 * @param parameters the configuration parameters for the simulation
	 */
	public TraceGenerator(Process process, String caseId, SimulationConfiguration parameters) {
		this.process = process;
		this.caseId = caseId;
		this.parameters = parameters;
		
		this.componentsObservationTime = new HashMap<Component, Long>();
		this.observationsCounter = new HashMap<Sequence, Integer>();
		this.tokens = new HashSet<Sequence>();
	}
	
	@Override
	public void run() {
		generatedTrace = generateProcessInstance();
	}
	
	/**
	 * This method returns the trace that has been generated
	 * 
	 * @return the trace that has been created, or <tt>null</tt> if the
	 * <tt>super.</tt>{@link #run()} method has never been called
	 */
	public XTrace getGeneratedTrace() {
		return generatedTrace;
	}
	
	/**
	 * This method generate a single process instance. This method adds the
	 * generated trace into the given log.
	 * 
	 * <p> If more than one {@link StartEvent} is available, the simulator picks
	 * one randomly.
	 * 
	 * @param log the log container
	 * @param caseId the case identifier of the new generated trace
	 * @return the generated trace
	 */
	private XTrace generateProcessInstance() {
		XTrace trace = XLogHelper.createTrace(caseId);
		processFlowObject(null, SetUtils.getRandom(process.getStartEvents()), trace, 0);
		
		XLogHelper.sort(trace);
		
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
	 * @param source
	 * @param object
	 * @param trace
	 * @param traceProgressiveTime
	 */
	private void processFlowObject(Sequence source, FlowObject object, XTrace trace, long traceProgressiveTime) {
		// store the execution time of the current element
		long executionDuration = recordEventExecution(object, trace, traceProgressiveTime);
		componentsObservationTime.put(object, traceProgressiveTime + executionDuration);

		// frequency update
		if (observationsCounter.containsKey(source)) {
			observationsCounter.put(source, observationsCounter.get(source) + 1);
		} else {
			observationsCounter.put(source, 1);
		}
		
		// different behavior depending of the elment type
		if (object instanceof Task ||
			object instanceof ExclusiveGateway ||
			object instanceof StartEvent) {
			// sequence or xor gateways
			
			// we can consume the provided token
			if (tokens != null) {
				tokens.remove(source);
			}
			
			// outgoing set population
			Set<Pair<FlowObject, Double>> outgoing = new HashSet<Pair<FlowObject, Double>>();
			for (FlowObject o : object.getOutgoingObjects()) {
				Sequence target = process.getSequence(object, o);
				if (observationsCounter.containsKey(target) &&
						observationsCounter.get(target) > parameters.getMaximumLoopCycles()) {
					// if the maximum number of cycles has been reached, then
					// we set the probability of the loop to 0
					outgoing.add(new Pair<FlowObject, Double>(o, 0.001));
				} else {
					outgoing.add(new Pair<FlowObject, Double>(o, 1.0));
				}
			}
			
			// outgoing element selection and token population
			FlowObject next = SetUtils.getRandomWeighted(outgoing);
			if (next instanceof Task) {
				Sequence s = object.getOwner().getSequence(object, next);
				if (s.getDataObjects().size() > 0) {
					recordEventAttributes(trace, s.getDataObjects(), trace.get(trace.size() - 1));
				}
			}
			Sequence sequenceToNext = process.getSequence(object, next);
			tokens.add(sequenceToNext);
			
			// firing of next element
			processFlowObject(sequenceToNext, next, trace, traceProgressiveTime + executionDuration);
			
		} else if (object instanceof ParallelGateway) {
			
			// in this case we have to distinguish between an AND split or an
			// AND join. to do that we count the numbner of incoming and
			// outgoing edges
			if (object.getOutgoingObjects().size() > 1) {
				
				// we can consume the provided token
				if (tokens != null) {
					tokens.remove(source);
				}
				
				// and split case
				for (FlowObject next : object.getOutgoingObjects()) {
					// we first have to add the token of the next activity
					tokens.add(process.getSequence(object, next));
				}
				for (FlowObject next : SetUtils.randomizeSet(object.getOutgoingObjects())) {
					// we can fire the flow on each branch
					Sequence sequenceToNext = process.getSequence(object, next);
					processFlowObject(sequenceToNext, next, trace, traceProgressiveTime);
				}
				
			} else if (object.getIncomingObjects().size() > 1) {
				
				// and join case
				boolean observedAll = true;
				Set<Long> incomingTimestamps = new HashSet<Long>();
				for (FlowObject fo : object.getIncomingObjects()) {
					Sequence sequenceToNext = process.getSequence(fo, object);
					if (!tokens.contains(sequenceToNext)) {
						// we have not yet completed this branch
						observedAll = false;
						break;
					} else {
						incomingTimestamps.add(componentsObservationTime.get(fo));
					}
				}
				if (observedAll) {
					// if we have observed all the tokens, then we can remove
					// all of them and continue our process execution
					for (FlowObject fo : object.getIncomingObjects()) {
						Sequence token = process.getSequence(fo, object);
						tokens.remove(token);
					}
					// we can add the token of the next activity and call the
					// procedure of the following element
					Long maxProgressive = Collections.max(incomingTimestamps);
					FlowObject next = SetUtils.getRandom(object.getOutgoingObjects());
					Sequence sequenceToNext = process.getSequence(object, next);
					tokens.add(sequenceToNext);
					processFlowObject(sequenceToNext, next, trace, maxProgressive);
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
	 * @param traceProgressiveTime
	 */
	private long recordEventExecution(FlowObject object, XTrace trace, long traceProgressiveTime) {
		if (object instanceof Task) {
			String caseId = XLogHelper.getName(trace);
			Task t = ((Task) object);
			XEvent event_start = XLogHelper.insertEvent(trace, t.getName(), new Date(traceProgressiveTime));
			XEvent event_complete = null;
			
			long duration = t.getDutarion(caseId) * 1000;
			if (duration > 0) {
				event_complete = XLogHelper.insertEvent(trace, t.getName(), new Date(traceProgressiveTime + duration));
				XLogHelper.decorateElement(event_start, "lifecycle:transition", "start");
				XLogHelper.decorateElement(event_complete, "lifecycle:transition", "complete");
			}
			
			recordEventAttributes(trace, object.getDataObjects(), event_start, event_complete);
			return duration + (t.getTimeAfter(caseId) * 1000);
		}
		
		return 0;
	}
	
	/**
	 * This method decorates an {@link XEvent} with the provided data objects
	 * 
	 * @param trace
	 * @param dataObjects
	 * @param events
	 */
	private void recordEventAttributes(XTrace trace, Set<DataObject> dataObjects, XEvent... events) {
		String caseId = XLogHelper.getName(trace);
		for (DataObject dataObj : dataObjects) {
			if (dataObj instanceof IntegerDataObject) {
				((GeneratedDataObject) dataObj).generateInstance(caseId);
				Integer value = (Integer) dataObj.getValue();
				for (XEvent event : events) {
					XLogHelper.decorateElement(event, dataObj.getName(), value);
				}
			} else if (dataObj instanceof StringDataObject) {
				((GeneratedDataObject) dataObj).generateInstance(caseId);
				String value = (String) dataObj.getValue();
				for (XEvent event : events) {
					XLogHelper.decorateElement(event, dataObj.getName(), value);
				}
			} else if (dataObj instanceof DataObject) {
				String value = (String) dataObj.getValue();
				for (XEvent event : events) {
					XLogHelper.decorateElement(event, dataObj.getName(), value);
				}
				
			}
		}
	}
}


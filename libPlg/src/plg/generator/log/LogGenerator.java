package plg.generator.log;

import java.util.HashSet;
import java.util.Set;

import plg.model.Component;
import plg.model.FlowObject;
import plg.model.Process;
import plg.model.activity.Task;
import plg.model.event.StartEvent;
import plg.model.gateway.ExclusiveGateway;
import plg.model.gateway.ParallelGateway;
import plg.utils.SetUtils;

public class LogGenerator {

	private Process process;
	private Set<Component> observedComponents;
	
	public LogGenerator(Process process) {
		this.process = process;
	}
	
	public void generateProcessInstance() {
		observedComponents = new HashSet<Component>();
		processFlowObject(SetUtils.getRandom(process.getStartEvents()));
	}
	
	private void processFlowObject(FlowObject object) {
		hookBeforeFlowObjectExecution(object);
		hookAfterFlowObjectExecution(object);
		observedComponents.add(object);
		
		if (object instanceof Task ||
				object instanceof ExclusiveGateway ||
				object instanceof StartEvent) {
			FlowObject next = SetUtils.getRandom(object.getOutgoingObjects());
			processFlowObject(next);
		} else if (object instanceof ParallelGateway) {
			if (object.getOutgoingObjects().size() > 1) {
				for (FlowObject fo : SetUtils.randomizeSet(object.getOutgoingObjects())) {
					processFlowObject(fo);
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
					processFlowObject(next);
				}
			}
		}
	}
	
	private void hookBeforeFlowObjectExecution(FlowObject object) {
		if (object instanceof Task) {
			System.out.println("start - " + object);
		}
	}
	
	private void hookAfterFlowObjectExecution(FlowObject object) {
//		System.out.println("complete - " + object);
	}
}

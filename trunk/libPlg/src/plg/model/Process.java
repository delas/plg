package plg.model;

import java.util.HashSet;
import java.util.Set;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidProcessException;
import plg.model.activity.Task;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.sequence.Sequence;

public class Process {

	private String name;
	private boolean valid = false;
	private Set<StartEvent> startEvents;
	private Set<Task> tasks;
	private Set<EndEvent> endEvents;
	private Set<Sequence> sequences;
	
	public Process(String name) {
		this.name = name;
		this.startEvents = new HashSet<StartEvent>();
		this.endEvents = new HashSet<EndEvent>();
		this.tasks = new HashSet<Task>();
		this.sequences = new HashSet<Sequence>();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void check() throws InvalidProcessException {
		if (startEvents.isEmpty()) {
			throw new InvalidProcessException("Invalid model: no start event given.");
		}
		if (endEvents.isEmpty()) {
			throw new InvalidProcessException("Invalid model: no end event given.");
		}
		
		for(StartEvent se : startEvents) {
			if (se.isIsolated()) {
				throw new InvalidProcessException("Invalid model: " + se + " is isolated.");
			}
			if (!se.canReachEndEvent()) {
				throw new InvalidProcessException("Invalid model: " + se + " cannot reach an end event.");
			}
		}
		for(EndEvent ee : endEvents) {
			if (ee.isIsolated()) {
				throw new InvalidProcessException("Invalid model: " + ee + " is isolated.");
			}
		}
		for(Task t : tasks) {
			if (t.isIsolated()) {
				throw new InvalidProcessException("Invalid model: " + t + " is isolated.");
			}
			if (!t.canReachEndEvent()) {
				throw new InvalidProcessException("Invalid model: " + t + " cannot reach an end event.");
			}
		}
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public void registerComponent(Component component) {
		if (component instanceof StartEvent) {
			startEvents.add((StartEvent) component);
			valid = false;
		} else if (component instanceof EndEvent) {
			endEvents.add((EndEvent) component);
			valid = false;
		} else if (component instanceof Task) {
			tasks.add((Task) component);
			valid = false;
		} else if (component instanceof Sequence) {
			sequences.add((Sequence) component);
			valid = false;
		}
	}
	
	public void removeComponent(Component component) {
		if (component instanceof FlowObject) {
			for(Sequence s : sequences) {
				if(s.getSource().equals(component) ||
						s.getSink().equals(component)) {
					removeComponent(s);
				}
			}
		}
		
		if (component instanceof StartEvent) {
			startEvents.remove((StartEvent) component);
			valid = false;
		} else if (component instanceof EndEvent) {
			endEvents.remove((EndEvent) component);
			valid = false;
		} else if (component instanceof Task) {
			tasks.remove((Task) component);
			valid = false;
		} else if (component instanceof Sequence) {
			sequences.remove((Sequence) component);
			valid = false;
		}
	}
	
	public Task newTask(String name) {
		return new Task(this, name);
	}
	
	public StartEvent newStartEvent() {
		return new StartEvent(this);
	}
	
	public EndEvent newEndEvent() {
		return new EndEvent(this);
	}
	
	public Sequence newSequence(FlowObject source, FlowObject sink) throws IllegalSequenceException {
		return new Sequence(this, source, sink);
	}
	
	public Set<StartEvent> getStartEvents() {
		return startEvents;
	}

	public Set<Task> getTasks() {
		return tasks;
	}

	public Set<EndEvent> getEndEvents() {
		return endEvents;
	}

	public Set<Sequence> getSequences() {
		return sequences;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Start Events\n");
		buffer.append("============\n");
		for(StartEvent se : startEvents) {
			buffer.append(se.toString() + "\n");
		}
		buffer.append("\nEnd Events\n");
		buffer.append("==========\n");
		for(EndEvent ee : endEvents) {
			buffer.append(ee.toString() + "\n");
		}
		buffer.append("\nTasks\n");
		buffer.append("=====\n");
		for(Task t : tasks) {
			buffer.append(t.toString() + "\n");
		}
		buffer.append("\nSequences\n");
		buffer.append("=========\n");
		for(Sequence s : sequences) {
			buffer.append(s.toString() + "\n");
		}
		return buffer.toString();
	}
}

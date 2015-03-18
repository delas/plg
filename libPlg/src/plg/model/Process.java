package plg.model;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import plg.exceptions.IllegalSequenceException;
import plg.exceptions.InvalidProcessException;
import plg.generator.scriptexecuter.IntegerScriptExecutor;
import plg.generator.scriptexecuter.StringScriptExecutor;
import plg.model.activity.Task;
import plg.model.data.DataObject;
import plg.model.data.IntegerDataObject;
import plg.model.data.StringDataObject;
import plg.model.event.EndEvent;
import plg.model.event.StartEvent;
import plg.model.gateway.ExclusiveGateway;
import plg.model.gateway.Gateway;
import plg.model.gateway.ParallelGateway;
import plg.model.sequence.Sequence;

/**
 * This class represents a process. A process, in this context, is a set of
 * start and end events, a set of tasks and a set of connections.
 * 
 * @author Andrea Burattin
 */
public class Process {

	private String id;
	private String name;
	private Boolean valid = null;
	private Set<Component> components;
	private Set<StartEvent> startEvents;
	private Set<Task> tasks;
	private Set<Gateway> gateways;
	private Set<EndEvent> endEvents;
	private Set<Sequence> sequences;
	private Set<DataObject> dataObjects;
	
	/**
	 * Process constructor. This constructor creates and empty process.
	 * 
	 * @param name the name of the new process
	 */
	public Process(String name) {
		this.id = new BigInteger(130, new Random()).toString(32);
		this.name = name;
		this.components = new HashSet<Component>();
		this.startEvents = new HashSet<StartEvent>();
		this.endEvents = new HashSet<EndEvent>();
		this.tasks = new HashSet<Task>();
		this.gateways = new HashSet<Gateway>();
		this.dataObjects = new HashSet<DataObject>();
		this.sequences = new HashSet<Sequence>();
	}
	
	/**
	 * Method to get the process id
	 * 
	 * @return the process id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Method to set the process id.
	 * 
	 * <p> <strong>ATTENTION:</strong> use this method only if you are
	 * <strong>absolutely aware</strong> of the possible consequences! Setting
	 * the wrong id can break everything! In most cases, automatically assigned
	 * id is fine.
	 * 
	 * @param id the id of the process
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Method to set the process name
	 * 
	 * @param name the name of the process
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Method to get the process name
	 * 
	 * @return the name of the process
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * This method verifies that the process is <em>valid</em>. In this context,
	 * a process is <em>valid</em> if it contains at least one start event, one
	 * end event, one task, and if from every start event and from every task,
	 * it is possible to reach an end event.
	 * 
	 * @throws InvalidProcessException exception thrown if the process is not
	 * valid
	 * @return <tt>true</tt> if the process is <em>valid</em>
	 */
	public boolean check() throws InvalidProcessException {
		if (startEvents.isEmpty()) {
			valid = false;
			throw new InvalidProcessException("Invalid model: no start event given.");
		}
		if (endEvents.isEmpty()) {
			valid = false;
			throw new InvalidProcessException("Invalid model: no end event given.");
		}
		
		for(StartEvent se : startEvents) {
			if (se.isIsolated()) {
				valid = false;
				throw new InvalidProcessException("Invalid model: " + se + " is isolated.");
			}
			if (!se.canReachEndEvent()) {
				valid = false;
				throw new InvalidProcessException("Invalid model: " + se + " cannot reach an end event.");
			}
		}
		for(EndEvent ee : endEvents) {
			if (ee.isIsolated()) {
				valid = false;
				throw new InvalidProcessException("Invalid model: " + ee + " is isolated.");
			}
		}
		for(Task t : tasks) {
			if (t.isIsolated()) {
				valid = false;
				throw new InvalidProcessException("Invalid model: " + t + " is isolated.");
			}
			if (!t.canReachEndEvent()) {
				valid = false;
				throw new InvalidProcessException("Invalid model: " + t + " cannot reach an end event.");
			}
		}
		for(Gateway g : gateways) {
			if (g.isIsolated()) {
				valid = false;
				throw new InvalidProcessException("Invalid model: " + g + " is isolated.");
			}
			if (!g.canReachEndEvent()) {
				valid = false;
				throw new InvalidProcessException("Invalid model: " + g + " cannot reach an end event.");
			}
		}
		valid = true;
		return true;
	}
	
	
	/**
	 * This method can be used to get the cached value returned by the
	 * {@link #check()} method.
	 * 
	 * @return <tt>true</tt> if the process is <em>valid</em>, <tt>false</tt>
	 * otherwise
	 */
	public boolean isValid() {
		if (valid == null) {
			try {
				valid = check();
			} catch (InvalidProcessException e) {
				valid = false;
			}
		}
		return valid;
	}
	
	/**
	 * This method is used to register every process components, to the
	 * corresponding process.
	 * 
	 * @param component the component to be registered
	 */
	public void registerComponent(Component component) {
		components.add(component);
		if (component instanceof StartEvent) {
			startEvents.add((StartEvent) component);
			valid = false;
		} else if (component instanceof EndEvent) {
			endEvents.add((EndEvent) component);
			valid = false;
		} else if (component instanceof Task) {
			tasks.add((Task) component);
			valid = false;
		} else if (component instanceof Gateway) {
			gateways.add((Gateway) component);
			valid = false;
		} else if (component instanceof Sequence) {
			sequences.add((Sequence) component);
			valid = false;
		} else if (component instanceof DataObject) {
			dataObjects.add((DataObject) component);
		}
	}
	
	/**
	 * This method searches a component registered into the current process with
	 * the provided component id. If no component is found, <tt>null</tt> is
	 * returned.
	 * 
	 * @param id the id of the component to be retrieved
	 * @return the searched component, or <tt>null</tt> if no component is found
	 */
	public Component searchComponent(String id) {
		for (Component c : components) {
			if (c.getId().equals(id)) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * This method can be used to remove a process component. If the given
	 * component is not registered, then nothing will be removed.
	 * 
	 * @param component the component to remove
	 */
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
		} else if (component instanceof Gateway) {
			gateways.remove((Gateway) component);
			valid = false;
		}
	}
	
	/**
	 * This method creates a new task registered to the current process.
	 * 
	 * @param name the new task name
	 * @return the newly created task
	 */
	public Task newTask(String name) {
		return new Task(this, name);
	}
	
	/**
	 * This method creates a new exclusive gateway registered to the current
	 * process.
	 * 
	 * @return the newly created exclusive gateway
	 */
	public ExclusiveGateway newExclusiveGateway() {
		return new ExclusiveGateway(this);
	}
	
	/**
	 * This method creates a new parallel gateway registered to the current
	 * process.
	 * 
	 * @return the newly created parallel gateway
	 */
	public ParallelGateway newParallelGateway() {
		return new ParallelGateway(this);
	}
	
	/**
	 * This method creates a new start event registered to the current process.
	 * 
	 * @return the newly created start event
	 */
	public StartEvent newStartEvent() {
		return new StartEvent(this);
	}
	
	/**
	 * This method creates a new end event registered to the current process.
	 * 
	 * @return the newly created end event
	 */
	public EndEvent newEndEvent() {
		return new EndEvent(this);
	}
	
	/**
	 * This method creates a new sequence registered to the current process.
	 * 
	 * @param source the source object of the sequence
	 * @param sink the destination object of the sequence
	 * @return the newly created sequence
	 */
	public Sequence newSequence(FlowObject source, FlowObject sink) throws IllegalSequenceException {
		Sequence s = getSequence(source, sink);
		if (s == null) {
			return new Sequence(this, source, sink);
		} else {
			return s;
		}
	}
	
	/**
	 * This method returns all the registered components
	 * 
	 * @return the set of all registered components
	 */
	public Set<Component> getComponents() {
		return components;
	}
	
	/**
	 * This method returns all the registered start events
	 * 
	 * @return the set of start event
	 */
	public Set<StartEvent> getStartEvents() {
		return startEvents;
	}

	/**
	 * This method returns all the registered tasks
	 *  
	 * @return the set of tasks
	 */
	public Set<Task> getTasks() {
		return tasks;
	}

	/**
	 * This method returns all the registered gateways
	 *  
	 * @return the set of gateways
	 */
	public Set<Gateway> getGateways() {
		return gateways;
	}

	/**
	 * This method returns all the registered end events
	 * 
	 * @return the set of end events
	 */
	public Set<EndEvent> getEndEvents() {
		return endEvents;
	}

	/**
	 * This method returns all the registered sequences
	 * 
	 * @return the set of sequences
	 */
	public Set<Sequence> getSequences() {
		return sequences;
	}

	/**
	 * This method returns all the registered data objects associated to the
	 * process
	 * 
	 * @return the set of data objects
	 */
	public Set<DataObject> getDataObjects() {
		return dataObjects;
	}
	
	/**
	 * This method can be used to retrieve the {@link Sequence} object that
	 * connects two flow objects. If such sequence is not reported into the
	 * current process model, <tt>null</tt> is returned.
	 * 
	 * @param source the source flow object
	 * @param sink the sink flow object
	 * @return the connecting sequence object
	 */
	public Sequence getSequence(FlowObject source, FlowObject sink) {
		for (Sequence s : sequences) {
			if(s.getSource().equals(source) && s.getSink().equals(sink)) {
				return s;
			}
		}
		return null;
	}
	
	/**
	 * This method returns the set of {@link Task}s that directly preceded
	 * the given {@link FlowObject}. If the provided flow object is already a
	 * task, then a set with only this object is returned.
	 * 
	 * @param object a flow object of the process
	 * @return a set of task that directly precedes the provided flow object
	 */
	public Set<Task> getPreviousTask(FlowObject object) {
		return getPreviousTask(object, true);
	}
	
	/**
	 * This method returns the set of {@link Task}s that directly preceded
	 * the given {@link FlowObject}.
	 * 
	 * @param object a flow object of the process
	 * @param allowFirst if this parameter is set to <tt>true</tt> then the
	 * procedure consider also the first element as a possible candidate
	 * @return a set of task that directly precedes the provided flow object
	 */
	public Set<Task> getPreviousTask(FlowObject object, boolean allowFirst) {
		HashSet<Task> toRet = new HashSet<Task>();
		
		if (allowFirst && object instanceof Task) {
			toRet.add((Task) object);
			return toRet;
		} else {
			Set<FlowObject> in = object.getIncomingObjects();
			if (in != null) {
				for(FlowObject fo : in) {
					toRet.addAll(getPreviousTask(fo, true));
				}
			}
		}
		return toRet;
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
	
	@Override
	public Object clone() {
		Process p = new Process(name);
		for (StartEvent e : getStartEvents()) {
			p.newStartEvent().setComponentId(e.componentId);
		}
		for (EndEvent e : getEndEvents()) {
			p.newEndEvent().setComponentId(e.componentId);
		}
		for (Gateway g : getGateways()) {
			if (g instanceof ParallelGateway) {
				p.newParallelGateway().setComponentId(g.componentId);
			} else if (g instanceof ExclusiveGateway) {
				p.newExclusiveGateway().setComponentId(g.componentId);
			}
		}
		for (Task t : getTasks()) {
			Task c = p.newTask(t.getName());
			c.setComponentId(t.componentId);
			if (t.getActivityScript() != null) {
				c.setActivityScript(new IntegerScriptExecutor(t.getActivityScript().getScript()));
			}
			for (DataObject d : t.getDataObjects()) {
				DataObject newDataObject = null;
				if (d instanceof IntegerDataObject) {
					newDataObject = new IntegerDataObject(p, new IntegerScriptExecutor(((IntegerDataObject) d).getScriptExecutor().getScript()));
				} else if (d instanceof StringDataObject) {
					newDataObject = new StringDataObject(p, new StringScriptExecutor(((StringDataObject) d).getScriptExecutor().getScript()));
				} else {
					newDataObject = new DataObject(p);
				}
				newDataObject.setComponentId(d.componentId);
				newDataObject.setName(d.getName());
				newDataObject.setValue(d.getValue());
				newDataObject.setObjectOwner(c);
			}
		}
		for (Sequence s : getSequences()) {
			try {
				FlowObject newSource = (FlowObject) p.searchComponent(s.getSource().getId());
				FlowObject newSink = (FlowObject) p.searchComponent(s.getSink().getId());
				Sequence newSequence = p.newSequence(newSource, newSink);
				newSequence.setComponentId(s.componentId);
				
				for (DataObject d : s.getDataObjects()) {
					DataObject newDataObject = null;
					if (d instanceof IntegerDataObject) {
						newDataObject = new IntegerDataObject(p, new IntegerScriptExecutor(((IntegerDataObject) d).getScriptExecutor().getScript()));
					} else if (d instanceof StringDataObject) {
						newDataObject = new StringDataObject(p, new StringScriptExecutor(((StringDataObject) d).getScriptExecutor().getScript()));
					} else {
						newDataObject = new DataObject(p);
					}
					newDataObject.setComponentId(d.componentId);
					newDataObject.setName(d.getName());
					newDataObject.setValue(d.getValue());
					newDataObject.setObjectOwner(newSequence);
				}
			} catch (IllegalSequenceException e) {
				e.printStackTrace();
			}
		}
		try {
			p.check();
		} catch (InvalidProcessException e1) {
			e1.printStackTrace();
		}
		return p;
	}
}

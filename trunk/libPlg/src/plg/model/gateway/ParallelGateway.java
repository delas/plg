package plg.model.gateway;

import plg.model.Process;

/**
 * This class represents a new parallel (AND) gateway
 * 
 * @author Andrea Burattin
 */
public class ParallelGateway extends Gateway {

	/**
	 * This constructor creates a new parallel gateway and register it to the
	 * given process owner
	 * 
	 * @param owner the process owner of the new exclusive gateway
	 */
	public ParallelGateway(Process owner) {
		super(owner);
	}

	@Override
	public String getComponentName() {
		return "Parallel Gateway";
	}
}

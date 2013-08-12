package plg.model.gateway;

import plg.model.Process;

/**
 * This class represents a new exclusive (XOR) gateway
 * 
 * @author Andrea Burattin
 */
public class ExclusiveGateway extends Gateway {

	/**
	 * This constructor creates a new exclusive gateway and register it to the
	 * given process owner
	 * 
	 * @param owner the process owner of the new exclusive gateway
	 */
	public ExclusiveGateway(Process owner) {
		super(owner);
	}

	@Override
	public String getComponentName() {
		return "Exclusive Gateway";
	}
}

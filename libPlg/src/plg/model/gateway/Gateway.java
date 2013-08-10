package plg.model.gateway;

import plg.model.FlowObject;
import plg.model.Process;

public abstract class Gateway extends FlowObject {

	public Gateway(Process owner) {
		super(owner);
	}
}

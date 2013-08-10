package plg.model.gateway;

import plg.model.Process;

public class ExclusiveGateway extends Gateway {

	public ExclusiveGateway(Process owner) {
		super(owner);
	}

	@Override
	public String getComponentName() {
		return "Exclusive Gateway";
	}
}

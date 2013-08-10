package plg.model.gateway;

import plg.model.Process;

public class ParallelGateway extends Gateway {

	public ParallelGateway(Process owner) {
		super(owner);
	}

	@Override
	public String getComponentName() {
		return "Parallel Gateway";
	}
}

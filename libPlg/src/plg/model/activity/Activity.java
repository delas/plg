package plg.model.activity;

import plg.model.FlowObject;
import plg.model.Process;

public abstract class Activity extends FlowObject {

	public Activity(Process owner) {
		super(owner);
	}
}

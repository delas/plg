package plg.model;

public abstract class Component {
	
	private static int componentIdProgress = 0;
	
	protected int componentId;
	protected Process owner;
	
	public Component(Process owner) {
		this.componentId = componentIdProgress++;
		this.owner = owner;
		this.owner.registerComponent(this);
	}
	
	public int getComponentId() {
		return componentId;
	}
	
	public abstract String getComponentName();
	
	@Override
	public String toString() {
		return "Component " + getComponentId() + " (type: " + getComponentName() + ")";
	}
	
	@Override
	public int hashCode() {
		return componentId;
	}
	
	@Override
	public boolean equals(Object compare) {
		if (compare instanceof Component) {
			return ((Component) compare).componentId == componentId;
		}
		return false;
	}
}

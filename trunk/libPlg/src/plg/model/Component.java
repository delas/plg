package plg.model;

/**
 * This class represents a general process components. A components is
 * everything that is associated to a business process. Every components has a
 * different identifier (within a process).
 * 
 * @author Andrea Burattin
 */
public abstract class Component {
	
	private static int componentIdProgress = 0;
	
	protected int componentId;
	protected Process owner;
	
	/**
	 * This method creates a new component and tries to register it to the
	 * given process owner. This constructor is also responsible for the
	 * generation of the component identifier.
	 * 
	 * @param owner the process owner of this component
	 */
	public Component(Process owner) {
		this.componentId = componentIdProgress++;
		this.owner = owner;
		this.owner.registerComponent(this);
	}
	
	/**
	 * This method returns the component identifier
	 * 
	 * @return the component id
	 */
	public String getComponentId() {
		return "" + componentId;
	}
	
	/**
	 * This method returns the component owner
	 * 
	 * @return the process owner of the component
	 */
	public Process getOwner() {
		return owner;
	}
	
	/**
	 * This method returns the name of the component (a string representation
	 * for the component type)
	 * 
	 * @return the component name
	 */
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

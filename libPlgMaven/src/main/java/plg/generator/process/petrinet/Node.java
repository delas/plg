package plg.generator.process.petrinet;

/**
 * This class represents a general (i.e., abstract class) node of a Petri net.
 * 
 * @author Andrea Burattin
 */
public abstract class Node {

	private static int componentIdProgress = 0;
	protected int componentId;
	protected String referenceId;
	protected PetriNet owner;
	
	/**
	 * Class constructor
	 * 
	 * @param owner the owner of this node
	 */
	protected Node(PetriNet owner) {
		this.componentId = componentIdProgress++;
		this.owner = owner;
	}
	
	/**
	 * This method returns the component identifier
	 * 
	 * @return the component id
	 */
	public String getComponentId() {
		return "" + componentId;
	}
	
	/**00
	 * This method sets the reference id of this node
	 * 
	 * @param referenceId the reference id of the original component
	 */
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	
	/**
	 * This method returns the reference id of this node
	 * 
	 * @return the id of the node
	 */
	public String getReferenceId() {
		return referenceId;
	}
	
	/**
	 * This method returns the Petri net owner of this node
	 * 
	 * @return the Petri net owner of the node
	 */
	public PetriNet getOwner() {
		return owner;
	}
}

package plg.generator.process.petrinet;

/**
 * This class represents a <em>transition</em> (either visible or invisible) of
 * a Petri net.
 * 
 * @author Andrea Burattin
 */
public class Transition extends Node {

	protected boolean isSilent = false;
	protected String label = "";
	
	/**
	 * Class constructor
	 * 
	 * @param owner the owner of this transition
	 */
	public Transition(PetriNet owner) {
		super(owner);
	}

	/**
	 * This method tells whether the current transition is invisible or not
	 * 
	 * @return <tt>true</tt> if the transition is invisible, <tt>false</tt>
	 * otherwise
	 */
	public boolean isSilent() {
		return isSilent;
	}

	/**
	 * This method sets if the current transition is silent or not
	 * 
	 * @param isSilent
	 */
	public void setSilent(boolean isSilent) {
		this.isSilent = isSilent;
	}

	/**
	 * This method returns the label of the transition
	 * 
	 * @return the label of the transition
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * This method sets the label of the transition
	 * 
	 * @param label the label of the transition
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return "Transition " + getComponentId() + " {Label = `" + label + "', isSilent = " + isSilent + "}";
	}
}

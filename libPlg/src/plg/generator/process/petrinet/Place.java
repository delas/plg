package plg.generator.process.petrinet;

/**
 * This class represents a <em>place</em> of a Petri net.
 * 
 * @author Andrea Burattin
 */
public class Place extends Node {

	protected int tokens = 0;
	
	/**
	 * Class constructor
	 * 
	 * @param owner the owner of this place
	 */
	public Place(PetriNet owner) {
		super(owner);
	}

	/**
	 * This method returns the number of tokens on this place
	 * 
	 * @return the number of token
	 */
	public int getTokens() {
		return tokens;
	}

	/**
	 * This method sets the number of tokens on this place
	 * 
	 * @param tokens the number of tokens to set
	 */
	public void setTokens(int tokens) {
		this.tokens = tokens;
	}
	
	@Override
	public String toString() {
		return "Place " + getComponentId();
	}
}

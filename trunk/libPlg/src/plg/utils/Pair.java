package plg.utils;

/**
 * This class represents a pair of two elements
 * 
 * @author Andrea Burattin
 */
public class Pair<F, S> {

	protected final S second;
	protected final F first;
	
	/**
	 * This constructor builds a new instance of the pair
	 * 
	 * @param first the first element of the pair
	 * @param second the second element of the pair
	 */
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}
	
	/**
	 * This method returns the first element of the pair
	 * 
	 * @return the first element
	 */
	public F getFirst() {
		return first;
	}
	
	/**
	 * This method returns the second element of the pair
	 * 
	 * @return the second element
	 */
	public S getSecond() {
		return second;
	}

	/**
	 * An utility method for the comparison of two elements
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private static boolean equals(Object x, Object y) {
		return ((x == null) && (y == null)) || ((x != null) && x.equals(y));
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object other) {
		return (other instanceof Pair) && equals(first, ((Pair<F, S>) other).first)
				&& equals(second, ((Pair<F, S>) other).second);
	}

	@Override
	public int hashCode() {
		if (first == null) {
			return second == null ? 0 : second.hashCode() + 1;
		} else {
			return second == null ? first.hashCode() + 2 : first.hashCode() * 17 + second.hashCode();
		}
	}

	@Override
	public String toString() {
		return "(" + first + "," + second + ")";
	}
}

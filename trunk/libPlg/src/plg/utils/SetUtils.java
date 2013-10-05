package plg.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Collection of utilities to handle {@link Set}s.
 * 
 * @author Andrea Burattin
 */
public class SetUtils {

	private static Random randomGenerator = new Random();
	
	/**
	 * This method returns a random object form a set.
	 * 
	 * @param set the given set
	 * @return a random object contained in the set, or <tt>null</tt> if the
	 * set is empty or a null object
	 */
	public static <E> E getRandom(Set<E> set) {
		if (set != null && set.size() > 0) {
			int item = randomGenerator.nextInt(set.size());
			int i = 0;
			for(E e : set) {
				if (i == item) {
					return e;
				}
				i = i + 1;
			}
		}
		return null;
	}
	
	/**
	 * This method convert a set into a randomly ordered {@link List}. The aim
	 * of this method is to generate an iterable collection that do not reflect
	 * any given order.
	 * 
	 * @param set the given set
	 * @return a randomly ordered list with the same components of the given
	 * set, or <tt>null</tt> if the set is null
	 */
	public static <E> List<E> randomizeSet(Set<E> set) {
		if (set == null) {
			return null;
		}
		List<E> list = new ArrayList<E>(set);
		Collections.shuffle(list);
		return list;
	}
}

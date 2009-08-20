package de.d3web.kernel.domainModel;

import java.util.Arrays;
import java.util.Iterator;

/**
 * This class represents the type of a heuristic decision tree
 * @author Norman Brümmer
 */
public class HDTType {

	private String name;
	public static final HDTType SOLUTION = new HDTType("SOLUTION");
	public static final HDTType NONE = new HDTType("NONE");
	public static final HDTType PROBLEMAREA = new HDTType("PROBLEMAREA");

	/**
	 *	Creates a new HDTType object
	 */
	private HDTType(String _name) {
		name = _name;
	}

	/**
	 *	@return a String representation of the HDTType
	 */
	public String toString() {
		return "HDTType: " + name;
	}
	
	/**
	 * This method is called immediately after an object of this class is deserialized.
	 * To avoid that several instances of a unique object are created, this method returns
	 * the current unique instance that is equal to the object that was deserialized.
	 * @author georg
	 */
	private Object readResolve() {
		Iterator iter = Arrays.asList(new HDTType[] {
			HDTType.SOLUTION,
			HDTType.NONE,
			HDTType.PROBLEMAREA,
		}).iterator();
		while (iter.hasNext()) {
			HDTType t = (HDTType) iter.next();
			if (t.name.equals(this.name)) {
				return t;
			}
		}
		return this;
	}
	
}

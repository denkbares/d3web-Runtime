package de.d3web.kernel.psMethods;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Helper class to provide explict information about
 * the knowledge to be stored in the ps-method knowledge
 * maps.
 * Creation date: (07.09.00 13:40:08)
 * @author Joachim Baumeister
 */
public class MethodKind implements java.io.Serializable {
	private java.lang.String kind;
	public final static MethodKind FORWARD = new MethodKind("FORWARD");
	public final static MethodKind BACKWARD = new MethodKind("BACKWARD");

	/**
	 * Insert the method's description here.
	 * Creation date: (07.09.00 13:40:46)
	 * @param theKind java.lang.String
	 */
	public MethodKind(String kind) {
		this.kind = kind;
	}

	/**
	 * @return true iff the String-value of these Objects are equal.
	 */
	public boolean equals(Object obj) {
		return kind.equals(obj.toString());
	}

	/**
	 * @return a string representation of this Object
	 */
	public String toString() {
		return kind;
	}
	
	
	/**
	 * This method is called immediately after an object of this class is deserialized.
	 * To avoid that several instances of a unique object are created, this method returns
	 * the current unique instance that is equal to the object that was deserialized.
	 * @author georg
	 */
	private Object readResolve() {
		Iterator iter = Arrays.asList(new MethodKind[] {
			MethodKind.FORWARD,
			MethodKind.BACKWARD,
		}).iterator();
		while (iter.hasNext()) {
			MethodKind m = (MethodKind) iter.next();
			if (m.kind.equals(this.kind)) {
				return m;
			}
		}
		return this;
	}
	
}

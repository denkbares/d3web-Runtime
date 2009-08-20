package de.d3web.kernel.domainModel;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Contains e.g. the information if a symptom is abstract knowledge or 
 * if it can be answered by a dialog (or both)
 * @author Joachim Baumeister
 */
public class DerivationType {
	/**
	 * used for SIs (derived knowledge)
	 */
	public final static DerivationType DERIVED = new DerivationType("DERIVED");
	
	/**
	 * used for Questions that can be answered in a dialog
	 */
	public final static DerivationType BASIC = new DerivationType("BASIC");
	
	/**
	 * used, when a symptom is DERIVED and BASIC
	 */
	public final static DerivationType MIXED = new DerivationType("MIXED");

	private java.lang.String name;

	private DerivationType(String newName) {
		setName(newName);
	}

	/**
	 * Compares the names of the DerivationTypes
	 * @return true, if names are equal
	 * @param dt DerivationType to compare with
	 */
	public boolean equals(DerivationType dt) {
		return (dt.getName().equals(getName()));
	}

	/**
	 * @return the String value of this derivation type
	 */
	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name of the DerivationType
	 */
	public String toString() {
		return getName();
	}
	
	
	/**
	 * This method is called immediately after an object of this class is deserialized.
	 * To avoid that several instances of a unique object are created, this method returns
	 * the current unique instance that is equal to the object that was deserialized.
	 * @author georg
	 */
	private Object readResolve() {
		Iterator iter = Arrays.asList(new DerivationType[] {
			DerivationType.BASIC,
			DerivationType.DERIVED,
			DerivationType.MIXED,
		}).iterator();
		while (iter.hasNext()) {
			DerivationType d = (DerivationType) iter.next();
			if (d.equals(this)) {
				return d;
			}
		}
		return this;
	}
}
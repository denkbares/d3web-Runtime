package de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation;

import java.util.Iterator;
import java.util.Set;

/**
 * This is the default strategy for selecting one out of a hole set of covering
 * strengths. It will choose the maximum of all strengths.
 * 
 * @author bruemmer
 * 
 */
public class DefaultStrengthSelectionStrategy implements StrengthSelectionStrategy {

	private static DefaultStrengthSelectionStrategy instance = null;

	private DefaultStrengthSelectionStrategy() {
	}

	public static DefaultStrengthSelectionStrategy getInstance() {
		if (instance == null) {
			instance = new DefaultStrengthSelectionStrategy();
		}
		return instance;
	}

	/**
	 * Selection method for this strategy
	 * 
	 * @return the maximum of the given strengths
	 */
	public Double selectStrength(Set strengths) {
		double maxStrength = Double.NEGATIVE_INFINITY;

		if (strengths != null) {
			Iterator iter = strengths.iterator();
			while (iter.hasNext()) {
				double strength = ((Double) iter.next()).doubleValue();
				if (strength > maxStrength) {
					maxStrength = strength;
				}
			}
		}
		return new Double(maxStrength);
	}

}

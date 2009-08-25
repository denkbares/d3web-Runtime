package de.d3web.kernel.psMethods.setCovering.strategies.transitivePropagation;

import java.util.Set;

/**
 * This interface describes a strategy for selecting one out of a set of
 * possible calculated covering strengths for paths from a diagnosis to a
 * finding
 * 
 * @author bruemmer
 * 
 */
public interface StrengthSelectionStrategy {
	public Double selectStrength(Set strengths);
}

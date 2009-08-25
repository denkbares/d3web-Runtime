package de.d3web.kernel.psMethods.setCovering;

import java.util.Arrays;

/**
 * This represents a Finding that can be observed. It will be generated from
 * XPSCase-values
 * 
 * @author bruemmer
 */
public class ObservableFinding extends Finding {

	public ObservableFinding() {
		super();
	}

	public void setAnswers(Object[] answers) {
		super.setAnswers(answers);
	}

	public boolean isLeaf() {
		return true;
	}

	/**
	 * Calculates the similarity of this finding and the other (given) Finding
	 * 
	 * @param otherFinding
	 *            PredictedFinding to compare to
	 * @return the similarity value of the comparison
	 */
	public double calculateSimilarity(Finding otherFinding) {

		// if the NamedObjects are different, their
		// similarity is 0.
		if (!getNamedObject().getId().equals(otherFinding.getNamedObject().getId())) {
			return 0;
		}

		if (otherFinding instanceof PredictedFinding) {
			return otherFinding.calculateSimilarity(this);
		}

		return super.calculateSimilarity(Arrays.asList(getAnswers()), Arrays.asList(otherFinding
				.getAnswers()));

	}

	public String verbalize() {
		return Arrays.asList(getAnswers()).toString();
	}

	public String toString() {
		return getNamedObject().getId() + "=" + Arrays.asList(getAnswers()).toString();
	}
}

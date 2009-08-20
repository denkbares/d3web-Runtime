package de.d3web.kernel.domainModel;

import java.util.logging.Logger;

/**
 * Stores the score of a diagnosis in context to
 * a problem-solving method. The score has meaning to
 * the state of a diagnosis.
 * @author joba, Chris - fixing an essential bug by returning new Scores for add and subtract.
 * @see Diagnosis
 * @see DiagnosisState
 */
public class DiagnosisScore implements Comparable {

	private double score;
	private Score aPrioriScore;

	/**
	 * Creates a new score given to a diagnosis with a default amount of 0 points.
	 */
	public DiagnosisScore() {
		setScore(0);
	}

	/**
	 * Initializes DiagnosisScore with a given APriori score
	 * @param Score the APriori-Score object
	 */
	public DiagnosisScore(Score theScore) {
		this();
		if (theScore != null) {
			setAPrioriScore(theScore);
		}
	}

	/**
	 * Add the score of the specified DiagnosisScore to a clone of
	 * this intance of DiagnosisScore.
	 * @param dScore de.d3web.kernel.domainModel.DiagnosisScore
	 * @return the result of the addition, the instance itself
	 */
	public DiagnosisScore add(DiagnosisScore theScore) {
		DiagnosisScore clone = cloneIt();
		clone.setScore(theScore.score + score);
		return clone;
	}

	/**
	 * Add the score of the specified DiagnosisScore to a clone of
	 * this intance of DiagnosisScore.
	 * @param dScore de.d3web.kernel.domainModel.DiagnosisScore
	 * @return the result of the addition, the instance itself
	 */
	public DiagnosisScore add(Score theScore) {
		DiagnosisScore clone = cloneIt();
		clone.setScore(score + theScore.getScore());
		return clone;
	}

	private DiagnosisScore cloneIt() {
		DiagnosisScore retValue = new DiagnosisScore();
		retValue.score = this.score;
		retValue.aPrioriScore = this.aPrioriScore;
		return retValue;
	}

	/**
	 * Neccessary implementation for the Comparable-interface
	 * Compares this DiagnosisScore to any other Object.
	 * @return 0, if the given Object is not instanceof DiagnosisScore, +1 if other score ist greater than this, -1 else.
	 */
	public int compareTo(Object o) {
		DiagnosisScore d;
		try {
			d = (DiagnosisScore) o;
		} catch (Exception e) {
			return 0;
		}
		double p = d.getScore();
		if (p > getScore())
			return +1;
		else if (p == getScore())
			return 0;
		else
			return -1;
	}

	private double computeExternalScore() {

		if (score <= 0) {
			return score;
		} else if (getAPrioriScore() == null) {
			return score;
		} else if (getAPrioriScore().aPrioriIsPositive()) {
			return score * getAPrioriScore().getAPriori();
		} else if (getAPrioriScore().aPrioriIsZeroOrNegative()) {
			return score + getAPrioriScore().getAPriori();
		} else {
			Logger.getLogger(this.getClass().getName()).warning(
				"there's something wrong in computeExternalScore");
			return score;
		}
	}

	/**
	 * Compares the internal scores of the
	 * two instances of DiagnosisScore. If the symbol is available
	 * for both, then these are checked as well.
	 * @return boolean the equality of the two int-values
	 * @param anotherScore de.d3web.kernel.domainModel.DiagnosisScore
	 */
	public boolean equals(DiagnosisScore diagnosisScore) {
		return (diagnosisScore.getScore() == getScore());

	}

	/**
	* Compares the internal scores of the
	* two instances of DiagnosisScore. If the symbol is available
	* for both, then these are checked as well.
	* @return boolean the equality of the two int-values
	* @param anotherScore de.d3web.kernel.domainModel.DiagnosisScore
	*/
	public boolean equals(Score theScore) {
		return (theScore.getScore() == getScore());
	}

	private Score getAPrioriScore() {
		return aPrioriScore;
	}

	public double getScore() {
		return computeExternalScore();
	}

	private void setAPrioriScore(Score newAPrioriScore) {
		aPrioriScore = newAPrioriScore;
	}

	/**
	 * Sets the specified score.
	 */
	public void setScore(double _score) {
		this.score = _score;
	}

	/**
	 * Subtracts the score of the specified DiagnosisScore from a clone of
	 * this intance of DiagnosisScore.
	 * @param dScore de.d3web.kernel.domainModel.DiagnosisScore
	 * @return the result of the addition, the instance itself
	 */
	public DiagnosisScore subtract(Score theScore) {
		DiagnosisScore clone = cloneIt();
		clone.setScore(score - theScore.getScore());
		return clone;
	}

	/**
	 * @return the String-representation of the score value
	 */
	public String toString() {
		return "" + getScore();
	}
}
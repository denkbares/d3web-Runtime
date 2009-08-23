package de.d3web.kernel.psMethods.compareCase.comparators;
import java.util.Collection;

import de.d3web.kernel.domainModel.qasets.Question;

/**
 * Class that describes a comparator result.
 * it contains weight, similarity, abnormality and some values
 * calculated by these parameters
 * Creation date: (04.08.2001 14:02:02)
 * @author: Norman Brümmer
 */
public class ComparatorResult {

	private Object[] queryQaPair = null;
	private Object[] storedQaPair = null;

	private double similarity = 0;
	private double maxPoints = 0;
	private double reachedPoints = 0;
	private double abnormality = 1;

	public ComparatorResult() {
		super();
		queryQaPair = new Object[2];
		storedQaPair = new Object[2];
	}

	/**
	 * Creation date: (11.08.01 16:10:44)
	 * @return double
	 */
	public double getAbnormality() {
		return abnormality;
	}

	public void setQueryQuestionAndAnswers(Question question, Collection answers) {
		queryQaPair[0] = question;
		queryQaPair[1] = answers;
	}

	public void setStoredQuestionAndAnswers(Question question, Collection answers) {
		storedQaPair[0] = question;
		storedQaPair[1] = answers;
	}

	public Question getQueryQuestion() {
		return (Question) queryQaPair[0];
	}

	public Question getStoredQuestion() {
		return (Question) storedQaPair[0];
	}

	public Collection getQueryAnswers() {
		return (Collection) queryQaPair[1];
	}

	public Collection getStoredAnswers() {
		return (Collection) storedQaPair[1];
	}

	/**
	 * Creation date: (05.08.2001 13:28:13)
	 * @return double
	 */
	public double getMaxPoints() {
		return maxPoints * abnormality;
	}

	/**
	 * Creation date: (05.08.2001 13:28:13)
	 * @return double
	 */
	public double getReachedPoints() {
		return reachedPoints * abnormality;
	}

	/**
	 * Creation date: (05.08.2001 13:28:13)
	 * @return double
	 */
	public double getSimilarity() {
		return similarity;
	}

	/**
	 * Creation date: (11.08.01 16:10:44)
	 * @param newAbnormality double
	 */
	public void setAbnormality(double newAbnormality) {
		abnormality = newAbnormality;
	}

	/**
	 * Creation date: (05.08.2001 13:28:13)
	 * @param newMaxPoints double
	 */
	public void setMaxPoints(double newMaxPoints) {
		maxPoints = newMaxPoints;
	}

	/**
	 * Creation date: (05.08.2001 13:28:13)
	 * @param newReachedPoints double
	 */
	public void setReachedPoints(double newReachedPoints) {
		reachedPoints = newReachedPoints;
	}

	/**
	 * Creation date: (05.08.2001 13:28:13)
	 * @param newSimilarity double
	 */
	public void setSimilarity(double newSimilarity) {
		similarity = newSimilarity;
	}

	/**
	 * Creation date: (05.08.2001 16:08:06)
	 * @return java.lang.String
	 */
	public String toString() {
		return (queryQaPair[0])
			+ ": "
			+ reachedPoints
			+ "="
			+ maxPoints
			+ "*"
			+ similarity
			+ " abnorm="
			+ abnormality
			+ "\n";
	}
}
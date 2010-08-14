/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.kernel.psmethods.comparecase.comparators;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Value;

/**
 * Class that describes a comparator result. it contains weight, similarity,
 * abnormality and some values calculated by these parameters Creation date:
 * (04.08.2001 14:02:02)
 * 
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
	 * 
	 * @return double
	 */
	public double getAbnormality() {
		return abnormality;
	}

	public void setQueryQuestionAndAnswers(Question question, Value value) {
		queryQaPair[0] = question;
		queryQaPair[1] = value;
	}

	public void setStoredQuestionAndAnswers(Question question, Value value) {
		storedQaPair[0] = question;
		storedQaPair[1] = value;
	}

	public Question getQueryQuestion() {
		return (Question) queryQaPair[0];
	}

	public Question getStoredQuestion() {
		return (Question) storedQaPair[0];
	}

	public Value getQueryValue() {
		return (Value) queryQaPair[1];
	}

	public Value getStoredValue() {
		return (Value) storedQaPair[1];
	}

	/**
	 * Creation date: (05.08.2001 13:28:13)
	 * 
	 * @return double
	 */
	public double getMaxPoints() {
		return maxPoints * abnormality;
	}

	/**
	 * Creation date: (05.08.2001 13:28:13)
	 * 
	 * @return double
	 */
	public double getReachedPoints() {
		return reachedPoints * abnormality;
	}

	/**
	 * Creation date: (05.08.2001 13:28:13)
	 * 
	 * @return double
	 */
	public double getSimilarity() {
		return similarity;
	}

	/**
	 * Creation date: (11.08.01 16:10:44)
	 * 
	 * @param newAbnormality double
	 */
	public void setAbnormality(double newAbnormality) {
		abnormality = newAbnormality;
	}

	/**
	 * Creation date: (05.08.2001 13:28:13)
	 * 
	 * @param newMaxPoints double
	 */
	public void setMaxPoints(double newMaxPoints) {
		maxPoints = newMaxPoints;
	}

	/**
	 * Creation date: (05.08.2001 13:28:13)
	 * 
	 * @param newReachedPoints double
	 */
	public void setReachedPoints(double newReachedPoints) {
		reachedPoints = newReachedPoints;
	}

	/**
	 * Creation date: (05.08.2001 13:28:13)
	 * 
	 * @param newSimilarity double
	 */
	public void setSimilarity(double newSimilarity) {
		similarity = newSimilarity;
	}

	/**
	 * Creation date: (05.08.2001 16:08:06)
	 * 
	 * @return java.lang.String
	 */
	@Override
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
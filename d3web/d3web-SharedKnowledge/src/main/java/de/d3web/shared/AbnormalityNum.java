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

package de.d3web.shared;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.NumValue;

/**
 * AbnormalityNum is for handling abnormality when working with QuestionNums
 * QuestionNums have no distinct values but can have as Answer an AnswerNum
 * encapsulating any double value
 * 
 * instead of a associative memory associating AnswerQuestions with abnormality
 * values, AbnormalityNum uses AbnormalityIntervals to associate ranges of
 * doubles with abnormality values
 * 
 * the addValue method takes an interval (defined by lower and upper boundary
 * and type) instead of an Answer (like Abnormalites addValue method) the
 * getValue method takes a double instead of an Answer
 */
public class AbnormalityNum extends AbstractAbnormality {

	private final List<AbnormalityInterval> intervals = new LinkedList<AbnormalityInterval>();

	public AbnormalityNum() {
	}

	/**
	 * checks if AbnormalityInterval interferes with any AbnormalityInterval
	 * added previously
	 * 
	 * @param ai AbnormalityInterval
	 * @return boolean, true if ai does not interfere with any
	 *         AbnormalityInterval in intervals
	 */
	private boolean checkIntervals(AbnormalityInterval ai) {
		Iterator<AbnormalityInterval> iter = intervals.iterator();
		while (iter.hasNext())
			if ((iter.next()).intersects(ai)) return false;
		return true;
	}

	/**
	 * 
	 * @param lowerBoundary double
	 * @param upperBoundary double
	 * @param value double
	 * @param type String
	 * @throws AbnormalityIntervalException if any of these values is not sound
	 */
	public void addValue(
			double lowerBoundary,
			double upperBoundary,
			double value,
			boolean leftOpen,
			boolean rightOpen)
			throws NumericalInterval.IntervalException {

		AbnormalityInterval ai =
				new AbnormalityInterval(lowerBoundary, upperBoundary, value, leftOpen, rightOpen);
		addValue(ai);
	}

	public void addValue(AbnormalityInterval ai) {
		if (checkIntervals(ai)) intervals.add(ai);
		else throw new NumericalInterval.IntervalException(
				"new AbnormalityInterval overlaps one of the existing AbnormalityIntervals");
	}

	/**
	 * 
	 * @param answerValue double
	 * @return double, value of abnormality of the AbnormalityInterval which
	 *         contains answerValue, A0 if answerValue is not contained in any
	 *         AbnormalityInterval
	 */
	public double getValue(double answerValue) {
		Iterator<AbnormalityInterval> iter = intervals.iterator();
		while (iter.hasNext()) {
			AbnormalityInterval ai = iter.next();
			if (ai.contains(answerValue)) return ai.getValue();
		}
		return A0;
	}

	/**
	 * 
	 * @param answerValue double
	 * @return double, value of abnormality of the AbnormalityInterval which
	 *         contains answerValue, A0 if answerValue is not contained in any
	 *         AbnormalityInterval
	 */
	@Override
	public double getValue(Value ans) {
		if (ans instanceof NumValue) {
			double d = 0;
			Double D = (Double) ((NumValue) ans).getValue();
			if (D != null) d = D.doubleValue();
			return getValue(d);
		}
		return A0;
	}

	/**
	 * Returns the interval-list.
	 * 
	 * @return List with the Intervals.
	 */
	public List<AbnormalityInterval> getIntervals() {
		return intervals;
	}

	/**
	 * Sets the interval-list
	 * 
	 * @param newIntervals
	 */
	// Ochlast: Method not used
	//
	// public void setIntervals(List<AbnormalityInterval> newIntervals) throws
	// NumericalInterval.IntervalException {
	// intervals = new LinkedList<AbnormalityInterval>();
	// for (Iterator<AbnormalityInterval> iter = newIntervals.iterator();
	// iter.hasNext();) {
	// AbnormalityInterval ai = iter.next();
	// if (checkIntervals(ai)) intervals.add(ai);
	// else throw new NumericalInterval.IntervalException(
	// "new AbnormalityInterval overlaps one of the existing AbnormalityIntervals");
	// }
	// }

	/**
	 * Sets the AbnormalityInterval for the {@link QuestionNum}
	 * 
	 * @created 25.06.2010
	 * @param qnum QuestionNum
	 * @param i AbnormalityInterval
	 */
	public static void setAbnormality(QuestionNum qnum, AbnormalityInterval i) {
		AbnormalityNum abnormality = (AbnormalityNum) qnum.getKnowledge(
				PROBLEMSOLVER, METHOD_KIND);
		if (abnormality == null) {
			abnormality = new AbnormalityNum();
			qnum.addKnowledge(PROBLEMSOLVER, abnormality, METHOD_KIND);
		}
		abnormality.addValue(i);
	}

	/**
	 * Sets the abnormality for the defined interval.
	 * 
	 * @created 25.06.2010
	 * @param qnum QuestionNum
	 * @param lowerBoundary LowerBoundary
	 * @param upperBoundary UpperBoundary
	 * @param abnormality Abnormality
	 * @param leftOpen true if the lowerBoundary should be included in the
	 *        interval
	 * @param rightOpen true if the UpperBoundary should be included in the
	 *        interval
	 */
	public static void setAbnormality(QuestionNum qnum, double lowerBoundary,
			double upperBoundary,
			double abnormality,
			boolean leftOpen,
			boolean rightOpen) {
		AbnormalityInterval ai =
				new AbnormalityInterval(lowerBoundary, upperBoundary, abnormality, leftOpen,
						rightOpen);
		setAbnormality(qnum, ai);
	}
}

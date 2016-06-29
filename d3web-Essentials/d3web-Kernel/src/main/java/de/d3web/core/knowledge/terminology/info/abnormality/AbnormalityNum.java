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

package de.d3web.core.knowledge.terminology.info.abnormality;

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
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
public class AbnormalityNum implements Abnormality {

	private final List<AbnormalityInterval> intervals = new LinkedList<>();

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
		for (AbnormalityInterval interval : intervals) {
			if ((interval).intersects(ai)) {
				return false;
			}
		}
		return true;
	}

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
		if (checkIntervals(ai)) {
			intervals.add(ai);
		}
		else {
			throw new NumericalInterval.IntervalException(
					"new AbnormalityInterval overlaps one of the existing AbnormalityIntervals");
		}
	}

	/**
	 * 
	 * @param answerValue double
	 * @return double, value of abnormality of the AbnormalityInterval which
	 *         contains answerValue, A0 if answerValue is not contained in any
	 *         AbnormalityInterval
	 */
	public double getValue(double answerValue) {
		for (AbnormalityInterval ai : intervals) {
			if (ai.contains(answerValue)) return ai.getValue();
		}
		return AbnormalityUtils.getDefault();
	}

	/**
	 * 
	 * @param answerValue double
	 * @return double, value of abnormality of the AbnormalityInterval which
	 *         contains answerValue, A0 if answerValue is not contained in any
	 *         AbnormalityInterval
	 */
	@Override
	public double getValue(Value answerValue) {
		if (answerValue instanceof NumValue) {
			double doubleValue = 0;
			Double objectDoubleValue = (Double) answerValue.getValue();
			if (objectDoubleValue != null) doubleValue = objectDoubleValue;
			return getValue(doubleValue);
		}
		return AbnormalityUtils.getDefault();
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
	 * Sets the AbnormalityInterval for the {@link QuestionNum}
	 * 
	 * @created 25.06.2010
	 * @param qnum QuestionNum
	 * @param i AbnormalityInterval
	 */
	public static void setAbnormality(QuestionNum qnum, AbnormalityInterval i) {
		InfoStore infoStore = qnum.getInfoStore();
		AbnormalityNum abnormalitySlice = infoStore.getValue(BasicProperties.ABNORMALITIY_NUM);
		if (abnormalitySlice == null) {
			abnormalitySlice = new AbnormalityNum();
			infoStore.addValue(BasicProperties.ABNORMALITIY_NUM, abnormalitySlice);
		}
		abnormalitySlice.addValue(i);
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

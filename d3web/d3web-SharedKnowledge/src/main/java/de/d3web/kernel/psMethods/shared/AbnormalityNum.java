/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.kernel.psMethods.shared;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.persistence.xml.loader.NumericalIntervalsUtils;

/**
 * AbnormalityNum is for handling abnormality when working with QuestionNums
 * QuestionNums have no distinct values but can have as Answer an AnswerNum encapsulating any double value
 * 
 * instead of a associative memory associating AnswerQuestions with abnormality values, 
 * AbnormalityNum uses AbnormalityIntervals to associate ranges of doubles with abnormality values
 * 
 * the addValue method takes an interval (defined by lower and upper boundary and type) instead of an Answer (like Abnormalites addValue method)
 * the getValue method takes a double instead of an Answer
 */
public class AbnormalityNum extends AbstractAbnormality {

	private List intervals = new LinkedList();
	
	/**
	 * AbnormalityInterval has
	 * 
	 * lowerBoundary, upperBoundary, value & type
	 * type specifies if and which endpoints of the interval are included or not
	 * CLOSED_... means lowerBoundary is included, OPEN_... means lowerBoundary is not included
	 * ..._CLOSED and ..._OPEN analogous
	 * 
	 * Abnormality offers
	 * 
	 * interferesWith(AbnormalityInterval) to check if two intervals overlap or connects
	 * contains(double) to check if a double lies between upperBoundary and upperBoundary
	 * isRightOpen() to check if the upperBoundary is not included
	 * isLeftOpen() ...
	 */
	public class AbnormalityInterval extends NumericalInterval {

		private double value;

		public AbnormalityInterval(
			double lowerBoundary,
			double upperBoundary,
			double value,
			boolean leftOpen,
			boolean rightOpen)
			throws NumericalInterval.IntervalException {
			super(lowerBoundary, upperBoundary, leftOpen, rightOpen);
			setValue(value);
		}

		public String toString() {
			return "AbnormalityInterval"
				+ " ("
				+ convertValueToConstantString(value)
				+ "): "
				+ (isLeftOpen() ? "(" : "[")
				+ getLeft()
				+ ", "
				+ getRight()
				+ (isRightOpen() ? ")" : "]");
		}

		/**
		 * @return
		 */
		public double getValue() {
			return value;
		}

		/**
		 * @param d
		 */
		public void setValue(double d) {
			value = d;
		}

	}

	public AbnormalityNum(){
	}

	/**
	 * checks if AbnormalityInterval interferes with any AbnormalityInterval added previously
	 * @param ai AbnormalityInterval
	 * @return boolean, true if ai does not interfere with any AbnormalityInterval in intervals
	 */
	private boolean checkIntervals(AbnormalityInterval ai) {
		Iterator iter = intervals.iterator();
		while (iter.hasNext()) if (((AbnormalityInterval) iter.next()).intersects(ai)) return false;
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
		if (checkIntervals(ai))
			intervals.add(ai);
		else
			throw new NumericalInterval.IntervalException("new AbnormalityInterval overlaps one of the existing AbnormalityIntervals");
	}
	
	/**
	 * 
	 * @param answerValue double
	 * @return double, value of abnormality of the AbnormalityInterval which contains answerValue, A0 if answerValue is not contained in any AbnormalityInterval
	 */
	public double getValue(double answerValue) {
		Iterator iter = intervals.iterator();
		while (iter.hasNext()) {
			AbnormalityInterval ai = (AbnormalityInterval) iter.next();
			if (ai.contains(answerValue)) return ai.value;
		}
		return A0;
	}

	/**
	 * 
	 * @param answerValue double
	 * @return double, value of abnormality of the AbnormalityInterval which contains answerValue, A0 if answerValue is not contained in any AbnormalityInterval
	 */
	public double getValue(Answer ans) {
		if (ans instanceof AnswerNum) {
			double d = 0;
			Double D = (Double) ((AnswerNum) ans).getValue(null);
			if (D != null) d = D.doubleValue();
			return getValue(d);
		}
		return A0;
	}

	/**
	 * Returns the interval-list.
	 * @return List with the Intervals.
	 */
	public List getIntervals(){
		return intervals;
	}

	/**
	 * Sets the interval-list
	 * @param newIntervals
	 */
	public void setIntervals(List newIntervals) throws NumericalInterval.IntervalException {
		intervals = new LinkedList();
		for (Iterator iter = newIntervals.iterator(); iter.hasNext();) {
			AbnormalityInterval ai = (AbnormalityInterval) iter.next();
			if (checkIntervals(ai)) intervals.add(ai);
			else throw new NumericalInterval.IntervalException("new AbnormalityInterval overlaps one of the existing AbnormalityIntervals");
		}
	}
	

	/**
	 * @see de.d3web.kernel.psMethods.shared.AbstractAbnormality#getXMLString()
	 */
	public String getXMLString() {
		return getXMLString(false);
	}

	public String getXMLString(boolean ignoreNullQuestion) {
		StringBuffer sb = new StringBuffer();
		sb.append(getXMLStringHeader());
	
		sb.append("<" + NumericalIntervalsUtils.GROUPTAG + ">\n");
	
		Iterator iter = intervals.iterator();
		while (iter.hasNext()) {
			AbnormalityInterval ai = (AbnormalityInterval) iter.next();
			sb.append(
				"<" + NumericalIntervalsUtils.TAG
				+ " " + NumericalIntervalsUtils.interval2lowerAttribute(ai)
				+ " " + NumericalIntervalsUtils.interval2upperAttribute(ai)
				+ " value=\"" + convertValueToConstantString(ai.value) + "\""
				+ " " + NumericalIntervalsUtils.interval2typeAttribute(ai)
				+ " />\n"
			);
		}
		
		sb.append("</" + NumericalIntervalsUtils.GROUPTAG + ">\n");
	
		sb.append(getXMLStringFooter());
		return sb.toString();
	}

	/**
	 * @param ai
	 * @return
	 */
	private String getTypeXMLString(AbnormalityInterval ai) {
		return "Left" + (ai.isLeftOpen() ? "Open" : "Closed") + "Right" + (ai.isRightOpen() ? "Open" : "Closed") + "Interval";
	}
	
}

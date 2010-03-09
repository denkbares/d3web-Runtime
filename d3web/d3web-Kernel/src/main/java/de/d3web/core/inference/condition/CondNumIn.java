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

package de.d3web.core.inference.condition;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerNum;

/**
 * Condition for numerical questions, where the question
 * needs to hold a value in the specified range. (Double values).
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author joba
 */
public class CondNumIn extends CondNum {

	private static final long serialVersionUID = -7671429466069982930L;
	private NumericalInterval _interval;

	/**
	 * Creates a new condition, where the value of a specified numerical 
	 * question needs to be within the specified minimum and maximum range.
	 * @param question the specified numerical question
	 * @param minValue the specified minimum value (Double)
	 * @param maxValue the specified maximum value (Double)
	 */
	public CondNumIn(QuestionNum question, Double minValue, Double maxValue) {
		this(question, new NumericalInterval(minValue.doubleValue(), maxValue.doubleValue(), false, false));
	}

	/**
	 * Creates a new condition, where the value of a specified numerical 
	 * question needs to be within the specified interval.
	 * @param question the specified numerical question
	 * @param theInterval the specified interval
	 */
	public CondNumIn(QuestionNum question, NumericalInterval theInterval) {
		super(question, null);
		_interval = theInterval;
	}

	@Override
	public boolean eval(XPSCase theCase) throws NoAnswerException, UnknownAnswerException {
		checkAnswer(theCase);
		AnswerNum answer = (AnswerNum) getQuestion().getValue(theCase).get(0);
		Double value = (Double) answer.getValue(theCase);
		if (value != null) {
			if (_interval != null) {
				return _interval.contains(value.doubleValue());
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Returns the maximum value to be allowed by this condition.
	 * @return the maximum value of the condition
	 */
	public Double getMaxValue() {
		return new Double(_interval.getRight());
	}

	/**
	 * Returns the minimum value required by this condition
	 * @return the minimum value of this condition
	 */
	public java.lang.Double getMinValue() {
		return new Double(_interval.getLeft());
	}

	@Override
	public String toString() {
		return "\u2190 CondNumIn question: "
			+ question.getId()
			+ " minValue: "
			+ getMinValue()
			+ " maxValue: "
			+ getMaxValue();
	}

	@Override
	public boolean equals(Object other) {
		if (!super.equals(other))
			return false;
		if (this.getInterval() != null)
			return this.getInterval().equals(((CondNumIn)other).getInterval());
		else
			return (((CondNumIn)other).getInterval() == null);
	}

	/**
	 * Returns the numerical interval of allowed values for
	 * this condition.
	 * @return the interval of allowed numerical values
	 */
	public NumericalInterval getInterval() {
		return _interval;
	}

	/**
	 * Sets the interval of allowed numerical values for
	 * this condition.
	 * @param inter the interval of allowed numerical values
	 */
	public void setInterval(NumericalInterval inter) {
		_interval = inter;
	}
	
	/**
	 * Returns a {@link String} print-out of the interval.
	 * @return a {@link String} representation of the specified interval
	 */
	public String getValue(){
		StringBuffer out = new StringBuffer();
		out.append(getInterval().isLeftOpen() ? "(" : "[");
		out.append(getInterval().getLeft()+", "+getInterval().getRight());
		out.append(getInterval().isRightOpen() ? ")" : "]");
		return out.toString();	
	}

	@Override
	public AbstractCondition copy() {
		return new CondNumIn((QuestionNum)getQuestion(),  getInterval());
	}
}
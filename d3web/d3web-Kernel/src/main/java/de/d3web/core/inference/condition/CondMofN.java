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
import java.util.List;

import de.d3web.core.session.XPSCase;
/**
 * Implements a Min/Max condition: A list of sub-conditions is given, where
 * <i>min</i> conditions must hold at least and <i>max</i> conditions must 
 * hold at most, to fulfill the min/max condition.
 * The composite pattern is used for this. This class is a "composite".
 * @author joba
 */
public class CondMofN extends NonTerminalCondition {

	private static final long serialVersionUID = 1L;
	private int min;
	private int max;

	/**
	 * Creates a new min/max condition with a list of sub-conditions
	 * with the specified number of minimal and maximal required conditions.
	 * @param terms the list of sub-conditions
	 * @param min the number of minimal required conditions
	 * @param max the number of maximal required conditions
	 */
	public CondMofN(List<AbstractCondition> terms, int min, int max) {
		super(terms);
		setMin(min);
		setMax(max);
	}

	@Override
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		int trueTillNow = 0;
		boolean wasNoAnswer = false;

		for (AbstractCondition condition : terms) {
			try {
				if (condition.eval(theCase)) {
					trueTillNow++;
					if (trueTillNow > getMax())
						return false;
				}
			} catch (NoAnswerException nax) {
				wasNoAnswer = true;
			} catch (UnknownAnswerException uax) {
				// do nothing, if all are Unknown, the return FALSE is appropriate
			}
		}
		if (trueTillNow >= getMin())
			return true;
		else if (wasNoAnswer)
			throw NoAnswerException.getInstance();
		else
			// if all answers were Unknown, then also false is appropriate
			return false;

	}
	
	/**
	 * Returns the maximum number of allowed sub-conditions to be true. 
	 * @return the maximum number of allowed true sub-conditions
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Returns the minimum number of required sub-conditions to be true. 
	 * @return the minimum number of required true sub-conditions
	 */
	public int getMin() {
		return min;
	}

	/**
	 * Sets the maximum number of allowed sub-conditions to be true. 
	 * @param max the maximum number of allowed true sub-conditions
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * Sets the minimum number of required sub-conditions to be true. 
	 * @param min the minimum number of required true sub-conditions
	 */
	public void setMin(int min) {
		if (min<0) min=0;
		this.min = min;
	}

	@Override
	public String toString() {
		String ret = "\u2190 CondMofN min="
				+ getMin()
				+ " max="
				+ getMax()
				+ " size="
				+ terms.size()+ " {";
		for (AbstractCondition condition : terms) {
			if (condition != null)
				ret += condition.toString();
		}
		ret += "}";
		return ret;
	}
	
	@Override
	public boolean equals(Object other){
		if (other instanceof CondMofN) {
			return (super.equals(other)) &&
			       (this.getMin() == ((CondMofN)other).getMin()) && 
				   (this.getMax() == ((CondMofN)other).getMax());
			
		}
		else 
			return false;
	}

	@Override
	protected AbstractCondition createInstance(List<AbstractCondition> theTerms, AbstractCondition o) {
		int min = ((CondMofN)o).getMin();
		int max = ((CondMofN)o).getMax();
		return new CondMofN(theTerms, min, max);
	}
}
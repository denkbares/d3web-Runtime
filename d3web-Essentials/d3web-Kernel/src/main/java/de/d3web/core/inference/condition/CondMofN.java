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

package de.d3web.core.inference.condition;

import java.util.Collection;

import de.d3web.core.session.Session;

/**
 * Implements a Min/Max condition: A list of sub-conditions is given, where
 * <i>min</i> conditions must hold at least and <i>max</i> conditions must hold
 * at most, to fulfill the min/max condition. The composite pattern is used for
 * this. This class is a "composite".
 * 
 * @author joba
 */
public class CondMofN extends NonTerminalCondition {

	private final int min;
	private final int max;

	/**
	 * Creates a new min/max condition with a list of sub-conditions with the
	 * specified number of minimal and maximal required conditions.
	 * 
	 * @param terms the list of sub-conditions
	 * @param min the number of minimal required conditions
	 * @param max the number of maximal required conditions
	 */
	public CondMofN(Collection<Condition> terms, int min, int max) {
		super(terms);
		this.min = Math.max(min, 0);
		this.max = max;
	}

	@Override
	public boolean eval(Session session)
			throws NoAnswerException, UnknownAnswerException {
		int trueTillNow = 0;
		boolean wasNoAnswer = false;

		for (Condition condition : getTerms()) {
			try {
				if (condition.eval(session)) {
					trueTillNow++;
					if (trueTillNow > getMax()) {
						return false;
					}
				}
			}
			catch (NoAnswerException nax) {
				wasNoAnswer = true;
			}
			catch (UnknownAnswerException uax) {
				// do nothing, if all are Unknown, the return FALSE is
				// appropriate
			}
		}
		if (trueTillNow >= getMin()) {
			return true;
		}
		else if (wasNoAnswer) {
			throw NoAnswerException.getInstance();
		}
		else {
			// if all answers were Unknown, then also false is appropriate
			return false;
		}

	}

	/**
	 * Returns the maximum number of allowed sub-conditions to be true.
	 * 
	 * @return the maximum number of allowed true sub-conditions
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Returns the minimum number of required sub-conditions to be true.
	 * 
	 * @return the minimum number of required true sub-conditions
	 */
	public int getMin() {
		return min;
	}

	@Override
	public String toString() {
		return "Between " + getMin() + " and " + getMax() + " true out of " + getTerms();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + max;
		result = prime * result + min;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CondMofN other = (CondMofN) obj;
		if (max != other.max) {
			return false;
		}
		return min == other.min;
	}
}
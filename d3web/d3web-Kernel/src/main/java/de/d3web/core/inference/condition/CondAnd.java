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

import java.util.List;

import de.d3web.core.session.Session;

/**
 * Implements an "and"-condition, where all sub-conditions have to be true. The
 * composite pattern is used for this. This class is a "composite".
 * 
 * @author Michael Wolber, joba
 */
public class CondAnd extends NonTerminalCondition {

	/**
	 * Creates a new AND-condition based on the conjunction of the specified
	 * terms ({@link Condition} instances).
	 * 
	 * @param terms a collection of {@link Condition} instances
	 */
	public CondAnd(List<Condition> terms) {
		super(terms);
	}

	/**
	 * Returns true, when <b>all</b> conjunctive elements are evaluated as true
	 * based on the findings given in the specified {@link Session}.
	 * 
	 * @param session the given {@link Session}
	 */
	@Override
	public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {
		boolean wasNoAnswer = false;
		boolean wasUnknownAnswer = false;

		for (Condition condition : terms) {
			try {
				if (!condition.eval(session)) {
					return false;
				}
			}
			catch (NoAnswerException nae) {
				wasNoAnswer = true;
			}
			catch (UnknownAnswerException uae) {
				wasUnknownAnswer = true;
			}
		}

		if (wasNoAnswer) {
			throw NoAnswerException.getInstance();
		}

		if (wasUnknownAnswer) {
			throw UnknownAnswerException.getInstance();
		}
		return true;
	}

	@Override
	public String toString() {
		String ret = "\u2190 CondAnd {";
		for (Condition condition : terms) {
			if (condition != null) {
				ret += condition.toString();
			}
		}
		ret += "}";
		return ret;
	}

	/**
	 * This method is used to simplify the copy action of a
	 * {@link NonTerminalCondition}. Do not use in the wild.
	 * 
	 */
	@Override
	protected Condition createInstance(List<Condition> theTerms, Condition o) {
		return new CondAnd(theTerms);
	}

}
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
 * Implements an "OR"-condition, where at least one sub-condition has to be
 * true. The composite pattern is used for this. This class is a "composite".
 * 
 * @author Michael Wolber, joba
 */
public class CondOr extends NonTerminalCondition {

	/**
	 * Creates a new OR-condition with a list of disjunctive sub-conditions.
	 */
	public CondOr(List<Condition> terms) {
		super(terms);
	}

	@Override
	public boolean eval(Session session)
			throws NoAnswerException, UnknownAnswerException {

		boolean wasNoAnswer = false;
		boolean wasUnknownAnswer = false;

		for (Condition condition : terms) {
			try {
				if (condition.eval(session)) return true;
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

		return false;
	}

	@Override
	public String toString() {
		String ret = "\u2190 CondOr {";
		for (Condition condition : terms) {
			if (condition != null) ret += condition.toString();
		}
		ret += "}";
		return ret;

	}

	@Override
	protected Condition createInstance(List<Condition> theTerms, Condition o) {
		return new CondOr(theTerms);
	}
}

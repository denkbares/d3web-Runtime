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

import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.Unknown;

/**
 * Abstract superclass to represent conditions, to be used for example in rules
 * ({@link Rule}) and set-covering relations. Every
 * condition holds a collection of objects, that are constrained in this
 * condition, and an eval method to evaluate this condition with respect to a
 * given {@link Session}.
 * 
 * Conditions must be immutable.
 * 
 * @author Joachim Baumeister, Christian Betz
 */
public interface Condition {

	/**
	 * Evaluates this condition with respect to the findings given in the
	 * specified {@link Session}. {@link NoAnswerException} and
	 * {@link UnknownAnswerException} exceptions are thrown in the case, when
	 * the condition cannot be strictly evaluated: If this condition is
	 * contained in a {@link CondNot} condition, then returning true/false is
	 * not appropriate.
	 * 
	 * @param session the given {@link Session}
	 * @return true/false for positive/negative evaluation; an appropriate
	 *         {@link Exception} otherwise
	 * @throws NoAnswerException when a required sub-condition of this condition
	 *         has a question with no answer currently set
	 * @throws UnknownAnswerException when a required sub-conditions contains a
	 *         question having an {@link Unknown} assigned
	 */
	boolean eval(Session session)
			throws NoAnswerException, UnknownAnswerException;

	/**
	 * Evaluates this condition exactly like {@link #eval(Session)}, but reports a missing or unknown answer as
	 * {@link ConditionCache.ConditionResult#UNDEFINED} or {@link ConditionCache.ConditionResult#UNKNOWN} instead
	 * of throwing {@link NoAnswerException} or {@link UnknownAnswerException}.
	 * <p>
	 * The default implementation delegates to {@link #eval(Session)}. Implementations may override this method to
	 * avoid exceptions as control flow, which are expensive for conditions without answers (and very expensive if
	 * a debugger or another JVMTI agent is attached). An override must always return the same result as the
	 * default implementation.
	 *
	 * @param session the given {@link Session}
	 * @return the result of the evaluation, never null
	 */
	default ConditionCache.ConditionResult evalToResult(Session session) {
		return evalToResult(this, session);
	}

	/**
	 * Evaluates the specified condition by calling {@link #eval(Session)} and converting the thrown exceptions into
	 * the respective {@link ConditionCache.ConditionResult}. This is the reference implementation of
	 * {@link #evalToResult(Session)}, e.g. for subclasses that override {@link #eval(Session)} of a class with an
	 * optimized {@link #evalToResult(Session)}.
	 *
	 * @param condition the condition to evaluate
	 * @param session   the given {@link Session}
	 * @return the result of the evaluation, never null
	 */
	static ConditionCache.ConditionResult evalToResult(Condition condition, Session session) {
		try {
			return condition.eval(session) ? ConditionCache.ConditionResult.TRUE : ConditionCache.ConditionResult.FALSE;
		}
		catch (NoAnswerException e) {
			return ConditionCache.ConditionResult.UNDEFINED;
		}
		catch (UnknownAnswerException e) {
			return ConditionCache.ConditionResult.UNKNOWN;
		}
	}

	/**
	 * Returns the collection of {@link Question} and {@link Solution}
	 * instances, that are constrained in this condition.
	 * 
	 * @return all used questions and diagnoses used in this condition
	 */
	Collection<? extends TerminologyObject> getTerminalObjects();

	/**
	 * Compares this condition with another condition.
	 * 
	 * @return true when a condition with the same content is given
	 * @param obj another condition to compare
	 */
	@Override
	boolean equals(Object obj);

	/**
	 * Returns the hash code of this condition.
	 */
	@Override
	int hashCode();
}
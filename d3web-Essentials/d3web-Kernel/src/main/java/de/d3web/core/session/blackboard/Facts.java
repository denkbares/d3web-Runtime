/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.core.session.blackboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.utils.Log;

public final class Facts {

	private Facts() { // suppress default constructor for noninstantiability
	}

	/**
	 * Merges the facts of solution ratings by the priority of the ratings. In
	 * addition it is considered, that the source solvers (e.g. user) have a
	 * higher priority as other solvers.
	 * <p>
	 * If the list of facts must contain only facts with diagnosis states as
	 * values, otherwise an IllegalArgumentException is thrown.
	 * 
	 * @param facts the facts to be merged
	 * @return the fact with the highest priority
	 * @throws IllegalArgumentException if a non-solution-state fact is
	 *         specified
	 */
	public static Fact mergeSolutionFacts(Fact[] facts) throws IllegalArgumentException {
		Fact maxFact = null;
		for (Fact fact : filterFactsForSourceSolvers(facts)) {
			Rating rating = (Rating) fact.getValue();
			// if any is excluded, it will win the race
			if (rating.hasState(State.EXCLUDED)) {
				return fact;
			}
			// otherwise maximize the rating
			if (maxFact == null
					|| rating.compareTo(maxFact.getValue()) > 0) {
				maxFact = fact;
			}
		}
		return maxFact;
	}

	/**
	 * Merges the facts of solution ratings by the priority of the ratings. In
	 * addition it is considered, that the source solvers (e.g. user) have a
	 * higher priority as other solvers.
	 * <p>
	 * If the list of facts must contain only facts with diagnosis states as
	 * values, otherwise an IllegalArgumentException is thrown.
	 * 
	 * @param facts the facts to be merged
	 * @return the fact with the highest priority
	 * @throws IllegalArgumentException if a non-solution-state fact is
	 *         specified
	 */
	public static Fact mergeSolutionFacts(List<Fact> facts) throws IllegalArgumentException {
		return mergeSolutionFacts(facts.toArray(new Fact[facts.size()]));
	}

	/**
	 * Merges the facts of indications by the priority of the ratings. In
	 * addition it is considered, that the source solvers (e.g. user) have a
	 * higher priority as other solvers.
	 * <p>
	 * If the list of facts must contain only facts with indication states as
	 * values, otherwise an IllegalArgumentException is thrown.
	 * 
	 * 
	 * @param facts the facts to be merged
	 * @return the fact with the highest priority
	 * @throws IllegalArgumentException if a non-indication-state fact is
	 *         specified
	 */
	public static Fact mergeIndicationFacts(Fact[] facts) throws IllegalArgumentException {
		Fact maxFact = null;
		for (Fact fact : filterFactsForSourceSolvers(facts)) {
			Indication factValue = (Indication) fact.getValue();
			// if any is excluded, it will win the race
			if (factValue.isContraIndicated()) {
				return fact;
			}
			// otherwise maximize the rating
			if (maxFact == null
					|| factValue.compareTo(maxFact.getValue()) > 0) {
				maxFact = fact;
			}
		}
		return maxFact;
	}

	/**
	 * Merges the facts of indications by the priority of the ratings. In
	 * addition it is considered, that the source solvers (e.g. user) have a
	 * higher priority as other solvers.
	 * <p>
	 * If the list of facts must contain only facts with indication states as
	 * values, otherwise an IllegalArgumentException is thrown.
	 * 
	 * 
	 * @param facts the facts to be merged
	 * @return the fact with the highest priority
	 * @throws IllegalArgumentException if a non-indication-state fact is
	 *         specified
	 */
	public static Fact mergeIndicationFacts(List<Fact> facts) throws IllegalArgumentException {
		return mergeIndicationFacts(facts.toArray(new Fact[facts.size()]));
	}

	/**
	 * This method is used if only a unique fact is expected by this solver.
	 * This method handles to print a warning if (unexpected) multiple facts are
	 * provided and returns the most current fact.
	 * 
	 * @param facts the facts to be merged
	 * @return the unique or most recent fact
	 */
	public static Fact mergeUniqueFact(Fact[] facts) {
		Fact[] filteredFacts = filterFactsForSourceSolvers(facts);
		// should usually not happen,
		// so warn and use last fact (most recent one)
		if (filteredFacts.length > 1) {
			String className = filteredFacts[0].getPSMethod().getClass().getName();
			Log.warning("Method "
					+ className
					+ ".mergeFacts(Fact[]) is called with multiple facts. "
					+ "This usually should not happen.");
		}
		return filteredFacts[filteredFacts.length - 1];
	}

	/**
	 * This method is used if answers to questions should be merged by sum up
	 * the values to a final one.
	 * <p>
	 * If there are any facts being set by source solvers, only these
	 * source-solver-facts are merged by the algorithm described below. In this
	 * case, all facts from non-source-solvers are ignored. Otherwise all facts
	 * are merged by the algorithm described below.
	 * <p>
	 * For the known answer types the result is:
	 * <ul>
	 * <li>AnswerYN: yes, if at least one value is yes, no otherwise
	 * <li>AnswerOC: the choice with the least ordinal number within the
	 * (ordered) list of choices of the question is used.
	 * <li>AnswerMC: all choices of all facts are combined
	 * <li>AnswerNum: adds the values of the facts
	 * <li>AnswerText: the text which is lexically ordered the most little one.
	 * <li>AnswerDate: the earliest of the dates
	 * </ul>
	 * 
	 * @param facts the facts to be merged
	 * @return the unique or most recent fact
	 */
	public static Fact mergeAnswerFacts(Fact[] facts) {
		Value resultValue = null;
		Question question = (Question) facts[0].getTerminologyObject();
		PSMethod psMethod = facts[0].getPSMethod();
		Fact[] filteredFacts = filterFactsForSourceSolvers(facts);
		if (filteredFacts.length == 1) return filteredFacts[0];
		for (Fact fact : filteredFacts) {
			Value value = fact.getValue();

			if (value instanceof Unknown) { // NOSONAR
				// handle unknown as first one!
				// unknown is never taken into the merge operation
				// only if no other value is available at the end,
				// unknown is used!
			}
			// otherwise calculate the sum of resultAnswer and answer
			else if (resultValue == null) {
				// if it is the first fact, use it as result.
				// this is independent to the type of answer.
				// use the following merge operations only for next ones
				resultValue = value;
			}
			else if (value instanceof NumValue) {
				// for numeric questions, add the values
				NumValue num1 = (NumValue) resultValue;
				NumValue num2 = (NumValue) value;
				Number d1 = (Number) num1.getValue();
				Number d2 = (Number) num2.getValue();
				double sum = d1.doubleValue() + d2.doubleValue();
				resultValue = new NumValue(sum); // deleteme:
													// AnswerFactory.createAnswerNum(sum);
			}
			else if (question instanceof QuestionMC) {
				Collection<ChoiceID> choices = new HashSet<>();
				// add current choices
				if (resultValue instanceof ChoiceValue) choices.add(((ChoiceValue) resultValue).getChoiceID());
				if (resultValue instanceof MultipleChoiceValue) choices.addAll(((MultipleChoiceValue) resultValue).getChoiceIDs());
				// add new choices
				if (value instanceof ChoiceValue) choices.add(((ChoiceValue) value).getChoiceID());
				if (value instanceof MultipleChoiceValue) choices.addAll(((MultipleChoiceValue) value).getChoiceIDs());
				// and create combined one
				resultValue = new MultipleChoiceValue(choices);
			}
			else {
				// otherwise use the lowest value
				if (resultValue == null || value.compareTo(resultValue) < 0) {
					resultValue = value;
				}
			}
		}
		if (resultValue == null) {
			resultValue = Unknown.getInstance();
		}

		return new DefaultFact(question, resultValue, psMethod, psMethod);
	}

	/**
	 * Filters the facts by looking at the kind of solvers. If there are any
	 * source solvers involved only the source solvers' facts are returned. The
	 * other facts will be ignored in that case. Otherwise all facts are
	 * returned.
	 * 
	 * @param facts the facts to be filtered
	 * @return the facts to be considered when merging
	 */
	private static Fact[] filterFactsForSourceSolvers(Fact[] facts) {
		boolean hasSource = false;
		boolean hasDerive = false;
		for (Fact fact : facts) {
			if (isSourceSolver(fact)) {
				hasSource = true;
			}
			else {
				hasDerive = true;
			}
			if (hasSource && hasDerive) break;
		}

		// only if we have a mixture of both kinds of solvers
		// filtering is required
		if (hasSource && hasDerive) {
			// so only use the source entries
			Collection<Fact> result = new ArrayList<>(facts.length);
			for (Fact fact : facts) {
				if (isSourceSolver(fact)) result.add(fact);
			}
			return result.toArray(new Fact[result.size()]);
		}
		// otherwise we can return the original fact array
		// because no filtering is required
		return facts;
	}

	private static boolean isSourceSolver(Fact fact) {
		// TODO: check for "source solvers" instead of user
		return (fact.getPSMethod() instanceof PSMethodUserSelected);
	}

	/**
	 * This method can be called by PSMethods if they do not create any new
	 * facts, and therefore not expecting to have their
	 * {@link PSMethod#mergeFacts(Fact[])} method called. This method created an
	 * IllegalStateException if there are any facts specified (what usually
	 * happens, because the {@link PSMethod#mergeFacts(Fact[])} is never called
	 * with an empty set of facts).
	 * 
	 * @param facts the facts that are not expected by the PSMethod
	 * @return an {@link IllegalStateException} if there are any facts, null
	 *         otherwise
	 */
	public static Fact mergeError(Fact[] facts) throws IllegalStateException {
		if (facts == null || facts.length == 0) return null;
		throw new IllegalStateException(
				"Invalid facts created for PSMethod "
						+ facts[0].getPSMethod());
	}
}

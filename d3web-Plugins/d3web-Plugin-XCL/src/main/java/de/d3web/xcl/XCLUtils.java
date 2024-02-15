/*
 * Copyright (C) 2014 denkbares GmbH
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
package de.d3web.xcl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.inference.condition.NonTerminalCondition;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;

/**
 * Provides static methods for XCLModels
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 22.02.2014
 */
public class XCLUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(XCLUtils.class);

	/**
	 * Returns a value for the question fitting to the coverings of the solution
	 * 
	 * @param solution specified {@link Solution}
	 * @param question specified {@link QuestionOC}
	 * @return fitting value or null
	 * @created 22.02.2014
	 */
	public static Value getFittingValue(Solution solution, QuestionOC question) {
		Collection<Value> fittingValues = getFittingValues(solution, question);
		if (!fittingValues.isEmpty()) {
			return fittingValues.iterator().next();
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all values for the question fitting to the coverings of the
	 * solution
	 * 
	 * @param solution specified {@link Solution}
	 * @param question specified {@link QuestionOC}
	 * @return fitting values (may be empty)
	 * @created 27.05.2014
	 */
	public static Collection<Value> getFittingValues(Solution solution, QuestionOC question) {
		return getFittingValues(solution, question, null);
	}

	/**
	 * Returns all values for the question fitting to the coverings of the
	 * solution. If a session is specified (not null), the values are also
	 * filtered for the ones are really capable to still get an positive
	 * coverage (e.g. some fitting values of an AND-combined conditions where
	 * one subcondition remains false will be omitted).
	 * 
	 * @param solution specified {@link Solution}
	 * @param question specified {@link QuestionOC}
	 * @param session the session to evaluate the condition for
	 * @return fitting values (may be empty)
	 * @created 27.05.2014
	 */
	public static Collection<Value> getFittingValues(Solution solution, QuestionChoice question, Session session) {
		XCLModel model = solution.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
		if (model == null) {
			return Collections.emptyList();
		}
		Set<Value> values = new HashSet<>();
		Set<XCLRelation> coveringRelations = model.getCoveringRelations(question);
		for (XCLRelation relation : coveringRelations) {
			// we assume that terminology objects cannot be used in
			// contradicting and normal relations
			if (relation.hasType(XCLRelationType.contradicted)) {
				Condition condition = relation.getConditionedFinding();
				Set<Value> forbiddenValues = new HashSet<>();
				fillForbiddenValues(question, condition, forbiddenValues);
				for (Choice c : question.getAllAlternatives()) {
					ChoiceValue value = new ChoiceValue(c);
					if (!forbiddenValues.contains(value)) {
						values.add(value);
					}
				}
				if (!values.isEmpty()) {
					return values;
				}
				// do not abort, normal answer is taken
				System.err.println("All values of " + question + " are used in contradicting relations.");
			}
			else {
				Set<Value> allowedValues = getValues(relation.getConditionedFinding(), question, session);
				if (!allowedValues.isEmpty()) {
					values.addAll(allowedValues);
					return values;
				}
			}
		}
		// nothing covered, return normal facts
		DefaultAbnormality abnormality = question.getInfoStore().getValue(BasicProperties.DEFAULT_ABNORMALITY);
		if (abnormality == null) {
			return Collections.emptyList();
		}
		for (Choice c : question.getAllAlternatives()) {
			ChoiceValue choiceValue = new ChoiceValue(c);
			if (abnormality.getValue(choiceValue) == Abnormality.A0) {
				values.add(choiceValue);
			}
		}
		return values;
	}

	private static void fillForbiddenValues(QuestionChoice question, Condition condition, Set<Value> forbiddenValues) {
		if (condition instanceof CondAnd) {
			for (Condition c : ((CondAnd) condition).getTerms()) {
				if (c.getTerminalObjects().contains(question)) {
					fillForbiddenValues(question, c, forbiddenValues);
				}
			}
		}
		else if (condition instanceof CondOr) {
			// CONDOR belonging to the same question are not supported
			for (Condition c : ((CondOr) condition).getTerms()) {
				if (c.getTerminalObjects().contains(question)) {
					fillForbiddenValues(question, c, forbiddenValues);
				}
			}
		}
		else {
			if (condition.getTerminalObjects().size() != 1) {
				throw new IllegalArgumentException();
			}
			forbiddenValues.addAll(getValues(condition, question));
		}
	}

	/**
	 * Returns the values, fitting to the specified condition. This method can
	 * only handle CondOr, CondEqual and CondAnd.
	 * <p/>
	 * For or conditions, it is assumed, that subconditions covering other
	 * questions evaluate to false, so e.G. no value for (F1=A AND (F1=B OR
	 * F2=C)) will be found.
	 * 
	 * @param condition specified {@link Condition}
	 * @return Set of fitting values
	 * @created 22.02.2014
	 */
	public static Set<Value> getValues(Condition condition, Question question) {
		return getValues(condition, question, null);
	}

	/**
	 * Returns the values, fitting to the specified condition. This method can
	 * only handle CondOr, CondEqual and CondAnd.
	 * <p/>
	 * For or conditions, it is assumed, that subconditions covering other
	 * questions evaluate to false, so e.G. no value for (F1=A AND (F1=B OR
	 * F2=C)) will be found.
	 * <p/>
	 * If the session is specified (not null), the session is used to check if
	 * parts of the condition can become true with any value of the specified
	 * question, e.g. if an AND condition does not already have some false
	 * subcondition. If not, this value of the question is omitted.
	 * 
	 * @param condition specified {@link Condition}
	 * @return Set of fitting values
	 * @created 22.02.2014
	 */
	public static Set<Value> getValues(Condition condition, Question question, Session session) {
		Set<Value> result = new HashSet<>();
		// return an empty set, if no value is covered
		if (!condition.getTerminalObjects().contains(question)) {
			return result;
		}
		if (condition instanceof CondOr) {
			// in case of OR check if the condition is already true,
			// then any value will create a "true" result (only possible for
			// choice questions)
			if (session != null && (question instanceof QuestionChoice)
					&& Conditions.isTrue(condition, session)) {
				for (Choice choice : ((QuestionChoice) question).getAllAlternatives()) {
					result.add(new ChoiceValue(choice));
				}
			}
			else {
				for (Condition c : ((CondOr) condition).getTerms()) {
					result.addAll(getValues(c, question, session));
				}
			}
		}
		else if (condition instanceof CondEqual) {
			if (question == ((CondEqual) condition).getQuestion()) {
				result.add(((CondEqual) condition).getValue());
			}
		}
		else if (condition instanceof CondAnd) {
			// in case of AND check if the condition is already false,
			// then no value can create a "true" result
			if (session != null && Conditions.isFalse(condition, session)) {
				// do nothing, result remains empty
				LOGGER.info("skip values due to false sub-condition of AND condition: " + condition);
			}
			else {
				List<Set<Value>> subSets = new ArrayList<>();
				for (Condition c : ((CondAnd) condition).getTerms()) {
					// if the subcondition coveres the question, collect its
					// values
					if (c.getTerminalObjects().contains(question)) {
						subSets.add(getValues(c, question, session));
					}
				}
				if (!subSets.isEmpty()) {
					result = subSets.get(0);
					for (int i = 1; i < subSets.size(); i++) {
						result.retainAll(subSets.get(i));
					}
				}
				if (result.isEmpty()) {
					LOGGER.warn("Cannot find value for " + question + " in " + condition);
				}
			}
		}
		else {
			LOGGER.warn("Condition of type " + condition.getClass() + " is not supported: "
					+ condition);
		}
		return result;
	}

	/**
	 * Returns all CondEqual instances of the specified condition as a set.
	 * 
	 * @param condition the condition to get the CondEquals from
	 * @return the (ordered) set of conditions
	 * @created 03.03.2014
	 */
	public static Set<CondEqual> getCondEquals(Condition condition) {
		Set<CondEqual> result = new LinkedHashSet<>();
		collectCondEqual(result, condition);
		return result;
	}

	/**
	 * Adds all CondEqual instances of the specified condition to the set
	 * 
	 * @param condEquals set to be filled
	 * @param condition specified condition
	 * @created 03.03.2014
	 */
	public static void collectCondEqual(Set<CondEqual> condEquals, Condition condition) {
		if (condition instanceof CondEqual) {
			condEquals.add((CondEqual) condition);
		}
		else if (condition instanceof NonTerminalCondition) {
			for (Condition subcondition : ((NonTerminalCondition) condition).getTerms()) {
				collectCondEqual(condEquals, subcondition);
			}
		}
	}

	/**
	 * Returns the sprint group of the specified session
	 * 
	 * @created 20.07.2014
	 * @param session
	 * @return List of Solutions being part of the sprint group
	 */
	public static List<Solution> getSprintGroup(Session session) {
		List<Solution> solutions = session.getBlackboard().getSolutions(State.ESTABLISHED);
		if (solutions.isEmpty()) {
			return session.getBlackboard().getSolutions(State.SUGGESTED);
		}
		else {
			return solutions;
		}
	}
}

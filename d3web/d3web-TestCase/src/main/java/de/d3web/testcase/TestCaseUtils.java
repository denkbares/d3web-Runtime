/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.testcase;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.empiricaltesting.Rating;
import de.d3web.empiricaltesting.ScoreRating;
import de.d3web.empiricaltesting.StateRating;
import de.d3web.scoring.HeuristicRating;
import de.d3web.testcase.model.Finding;
import de.d3web.testcase.model.TestCase;
import de.d3web.testcase.prefix.PrefixedTestCase;

/**
 * Provides basic static functions
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 24.01.2012
 */
public class TestCaseUtils {

	public static final String VALUE_OUT_OF_RANGE = "ValueOutOfRange";

	/**
	 * Applies the findings of the specified {@link TestCase} at the specified
	 * {@link Date} to the {@link Session}
	 *
	 * @param session  Session on which the Findings should be applied
	 * @param testCase specified TestCase
	 * @param date     specified Date
	 * @created 24.01.2012
	 */
	public static void applyFindings(Session session, TestCase testCase, Date date) {
		applyFindings(session, testCase, date, false);
	}

	/**
	 * Applies the findings of the specified {@link TestCase} at the specified
	 * {@link Date} to the {@link Session}
	 *
	 * @param session             Session on which the Findings should be applied
	 * @param testCase            specified TestCase
	 * @param date                specified Date
	 * @param skipValueOutOfRange if this is set to true, findings that try to set values outside the defined
	 *                            range of a question are ignored
	 * @created 26.11.2014
	 */
	@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
	public static void applyFindings(Session session, TestCase testCase, Date date, boolean skipValueOutOfRange) {
		Blackboard blackboard = session.getBlackboard();
		session.getPropagationManager().openPropagation(date.getTime());
		for (Finding finding : testCase.getFindings(date, session.getKnowledgeBase())) {
			List<String> errors = new LinkedList<String>();
			checkValues(errors, finding.getTerminologyObject(), finding.getValue());
			if (errors.isEmpty()) {
				if (skipValueOutOfRange) {
					NumericalInterval numericalInterval = finding.getTerminologyObject()
							.getInfoStore()
							.getValue(BasicProperties.QUESTION_NUM_RANGE);
					if (numericalInterval != null && finding.getValue() instanceof NumValue) {
						if (!numericalInterval.contains(((NumValue) finding.getValue()).getDouble())) continue;
					}
				}
				Fact fact = FactFactory.createUserEnteredFact(finding.getTerminologyObject(),
						finding.getValue());
				if (finding.getValue() instanceof Indication) {
					synchronized (session) {
						blackboard.addInterviewFact(fact);
						session.touch(date);
					}
				}
				else {
					synchronized (session) {
						blackboard.addValueFact(fact);
						session.touch(date);
					}
				}
			}
		}
		session.getPropagationManager().commitPropagation();
	}

	/**
	 * Returns all questions used in Findings
	 *
	 * @param testCase {@link TestCase} to examine
	 * @param kb       {@link KnowledgeBase}
	 * @return Collection of questions used in the specified {@link TestCase}
	 * @created 24.01.2012
	 */
	public static Collection<Question> getUsedQuestions(TestCase testCase, KnowledgeBase kb) {
		Collection<Question> questions = new LinkedHashSet<Question>();
		if (testCase instanceof PrefixedTestCase) {
			questions.addAll(getUsedQuestions(((PrefixedTestCase) testCase).getTestCase(), kb));
			questions.addAll(getUsedQuestions(((PrefixedTestCase) testCase).getPrefix(), kb));
		}
		else {
			for (Date date : testCase.chronology()) {
				for (Finding f : testCase.getFindings(date, kb)) {
					if (f.getTerminologyObject() instanceof Question) {
						questions.add((Question) f.getTerminologyObject());
					}
				}
			}
		}
		return questions;
	}

	/**
	 * Checks if the {@link Value} contains a choice which isn't in the kb
	 *
	 * @param errors if the value does not fit, an error is entered in this
	 *               collection
	 * @param object {@link TerminologyObject}
	 * @param value  {@link Value}
	 * @created 14.03.2012
	 */
	public static void checkValues(Collection<String> errors, TerminologyObject object, Value value) {
		if (object == null) {
			throw new NullPointerException();
		}
		if (value == null) {
			errors.add("The question \"" + object.getName() + "\" has no valid value.");
		}
		else if (object instanceof Question && value instanceof Unknown) { // NOSONAR
			// this is ok, no error
		}
		else if (object instanceof QuestionOC) {
			if (value instanceof ChoiceValue) {
				ChoiceValue cv = (ChoiceValue) value;
				if (cv.getChoice((QuestionChoice) object) == null) {
					errors.add("The question \"" + object.getName() + "\" has no choice \""
							+ cv.getAnswerChoiceID() + "\".");
				}
			}
			else {
				errors.add("The QuestionOC \"" + object.getName()
						+ "\" cannot be matched to \"" + value.toString() + "\".");
			}
		}
		else if (object instanceof QuestionMC) {
			if (value instanceof MultipleChoiceValue) {
				MultipleChoiceValue mcv = (MultipleChoiceValue) value;
				if (mcv.asChoiceList((QuestionChoice) object).contains(null)) {
					errors.add("The question \"" + object.getName()
							+ "\" does not contain all choices of " + mcv.toString() + ".");
				}
			}
			else {
				errors.add("The QuestionMC \"" + object.getName()
						+ "\" cannot be matched to \"" + value.toString()
						+ "\".");
			}
		}
		else if (object instanceof QuestionNum) {
			if (!(value instanceof NumValue)) {
				errors.add("The QuestionNum \"" + object.getName()
						+ "\" needs a numeric value instead of \"" + value.toString() + "\".");
			}
		}
		else if (object instanceof QuestionText) {
			if (!(value instanceof TextValue)) {
				errors.add("The QuestionText \"" + object.getName()
						+ "\" needs a text value instead of \"" + value.toString() + "\".");
			}
		}
		else if (object instanceof QuestionDate) {
			if (!(value instanceof DateValue)) {
				errors.add("The QuestionDate \"" + object.getName()
						+ "\" needs a date value instead of \"" + value.toString() + "\".");
			}
		}
	}

	/**
	 * Returns a state corresponding to the committed score.
	 *
	 * @param empiricalTestingRating Rating representing the score of a RatedSolution.
	 * @return DiagnosisState corresponding to the committed scored.
	 */
	public static de.d3web.core.knowledge.terminology.Rating toRating(Rating empiricalTestingRating) {

		if (empiricalTestingRating instanceof ScoreRating) {
			return new HeuristicRating(((ScoreRating) empiricalTestingRating).getRating());
		}
		else if (empiricalTestingRating instanceof StateRating) {
			return ((StateRating) empiricalTestingRating).getRating();
		}

		return null;
	}

	public static String getPrompt(TerminologyObject object, Value value) {
		if (value instanceof ChoiceValue && object instanceof QuestionChoice) {
			Choice choice = ((ChoiceValue) value).getChoice((QuestionChoice) object);
			return getPrompt(choice);
		}
		if (value instanceof MultipleChoiceValue && object instanceof QuestionChoice) {
			StringBuilder result = new StringBuilder();
			Collection<ChoiceID> choiceIDs = ((MultipleChoiceValue) value).getChoiceIDs();
			if (choiceIDs.isEmpty()) return "--";
			for (ChoiceID choiceID : choiceIDs) {
				Choice choice = choiceID.getChoice((QuestionChoice) object);
				result.append(", ").append(getPrompt(choice));
			}
			return result.toString().substring(2);
		}
		return value.toString();
	}

	public static String getPrompt(NamedObject object) {
		String prompt = object.getInfoStore().getValue(MMInfo.PROMPT, Locale.getDefault());
		if (prompt == null) {
			prompt = object.getName();
		}
		return prompt;
	}
}

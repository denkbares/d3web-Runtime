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

package de.d3web.casegeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.activation.UnsupportedDataTypeException;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;

/**
 * This class is used to compute a list of suitable answers for a given
 * question. Currently supported:
 * <UL>
 * <LI> {@link QuestionChoice}
 * <LI> {@link QuestionNum}
 * <LI> {@link QuestionText}
 * </UL>
 * 
 * @author joba
 */
public final class AnswerSelector {

	/**
	 * Number of numerical answers generated in case of a {@link QuestionNum}.<BR>
	 * Default value is 3 possible answers.
	 */
	public int numberOfNumericalValues = 3;

	private Map<Question, List<Value>> forbiddenAnswers = new HashMap<Question, List<Value>>();

	public final static NumericalInterval DEFAULT_INTERVAL = new NumericalInterval(0, 10);
	private final static List<Value> EMPTY_FORBIDDEN_LIST = Collections.emptyList();

	private static AnswerSelector instance = new AnswerSelector();

	private AnswerSelector() {
	}

	public static AnswerSelector getInstance() {
		return instance;
	}

	// /**
	// * Compute a list of {@link Finding} combinations that are possible for a
	// * specified collection of questions
	// * @param questions a specified collection of {@link Question} instances
	// * @return All {@link Finding} combinations allowed for the specified list
	// of questions
	// * @throws UnsupportedDataTypeException when an unsupported question type
	// is contained in the questions
	// */
	// public List<List<Finding>> determineAnswers(List<Question> questions)
	// throws UnsupportedDataTypeException {
	// List<List<Finding>> findingCombinations = new
	// LinkedList<List<Finding>>();
	// // initial set up: first question inserted
	// Question q = questions.remove(0);
	// for (Answer answer : determineAnswers(q)) {
	// List<Finding> l = new LinkedList<Finding>();
	// l.add(new Finding(q, answer));
	// findingCombinations.add(l);
	// }
	// // recursive combinations of the remaining questions and answers,
	// respectively
	// return determineAnswers(findingCombinations, questions);
	// }
	//
	// private List<List<Finding>> determineAnswers(List<List<Finding>>
	// findingCombinations, List<Question> questions) throws
	// UnsupportedDataTypeException {
	// if (questions.isEmpty()) {
	// return findingCombinations;
	// }
	// List<List<Finding>> newCombinations = new LinkedList<List<Finding>>();
	// Question question = questions.remove(0);
	// for (List<Finding> list : findingCombinations) {
	// for (Answer answer : determineAnswers(question)) {
	// List<Finding> newCombination = new LinkedList<Finding>(list);
	// newCombination.add(new Finding(question, answer));
	// newCombinations.add(newCombination);
	// }
	// }
	// findingCombinations = newCombinations;
	// return determineAnswers(findingCombinations, questions);
	// }

	/**
	 * Compute all allowed {@link Answer} instances for a specified
	 * {@link Question}.
	 * 
	 * @param question the specified question
	 * @return a list of allowed answer values for the specified question
	 * @throws UnsupportedDataTypeException when an unsupported question is
	 *         given in the parameter
	 */
	public List<? extends Value> determineValues(Question question) throws UnsupportedDataTypeException {
		if (question instanceof QuestionChoice) {
			return determineChoiceValues((QuestionChoice) question);
		}
		else if (question instanceof QuestionNum) {
			return determineNumValues((QuestionNum) question);
		}
		else if (question instanceof QuestionText) {
			return determineTextValues((QuestionText) question);
		}

		throw new UnsupportedDataTypeException();
	}

	private List<? extends Value> determineTextValues(QuestionText question) {
		List<Value> answers = new ArrayList<Value>(3);
		answers.add(new TextValue("test"));
		answers.add(new TextValue("."));
		answers.add(new TextValue(""));
		return answers;
	}

	private List<? extends Value> determineNumValues(QuestionNum question) {
		List<Value> answers = new LinkedList<Value>();
		NumericalInterval range = (NumericalInterval) question.getInfoStore().getValue(
				BasicProperties.QUESTION_NUM_RANGE);
		if (range == null) {
			range = DEFAULT_INTERVAL;
		}
		int indent = (int) ((range.getRight() - range.getLeft()) / (numberOfNumericalValues - 1));
		for (double i = range.getLeft(); i <= range.getRight(); i = i + indent) {
			answers.add(new NumValue(new Double(i)));
		}

		return answers;
	}

	private List<? extends Value> determineChoiceValues(QuestionChoice question) {
		List<Value> answers = new ArrayList<Value>();

		List<Value> answerCandidates = new ArrayList<Value>();
		// only add candidates, that are not forbidden
		if (forbiddenAnswers.get(question) == null) {
			forbiddenAnswers.put(question, EMPTY_FORBIDDEN_LIST);
		}
		for (Choice choice : question.getAllAlternatives()) {
			ChoiceValue choiceValue = new ChoiceValue(choice);
			if (!forbiddenAnswers.get(question).contains(choiceValue)) {
				answerCandidates.add(choiceValue);
			}
		}

		if (forbiddenAnswers.get(question) != null) {
			answerCandidates.removeAll(forbiddenAnswers.get(question));
		}

		// if the question is a QuestionOC add each possible answer to a value
		if (question instanceof QuestionOC) {
			for (Value value : answerCandidates) {
				answers.add(value);
			}

			// if the question is a QuestionMC get all possible answer
			// combinations
		}
		else if (question instanceof QuestionMC) {
			answers.addAll(ValueCombinator.getInstance().getAllPossibleCombinations(
					(QuestionMC) question));
		}

		return answers;
	}

	/**
	 * Return the {@link Map} storing the non-allowed answers for questions (in
	 * the key).
	 * 
	 * @return the forbidden list of answers
	 */
	public Map<Question, List<Value>> getForbiddenAnswers() {
		return forbiddenAnswers;
	}

	/**
	 * Sets the {@link Map} storing all non-allowed answers
	 * 
	 * @param forbiddedAnswers the forbidden answers
	 */
	public void setForbiddenAnswers(Map<Question, List<Value>> forbiddedAnswers) {
		this.forbiddenAnswers = forbiddedAnswers;
	}

}

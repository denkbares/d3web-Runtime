/*
 * Copyright (C) 2011 denkbares GmbH, Germany
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.interview.Form;
import de.d3web.interview.inference.PSMethodInterview;

/**
 * Implements a SequencerStrategy that creates a simple question by question
 * decision tree.
 * 
 * @author volker_belli
 * @created 20.04.2011
 */
public class DefaultBotStrategy implements BotStrategy {

	// some fields to control value creation for question
	private int numberOfNumericalValues = 3;
	private NumericalInterval defaultNumericalInterval = new NumericalInterval(0, 10);

	private Map<Question, List<Value>> forbiddenAnswers = Collections.emptyMap();
	private Map<Question, List<Value>> allowedAnswers = Collections.emptyMap();

	private int maxChoiceCombinations = -1;
	private Map<QuestionMC, Integer> maxChoiceCombinationsForQuestion = Collections.emptyMap();
	private Map<QuestionMC, Collection<Set<Choice>>> requiredChoiceCombinations = Collections.emptyMap();
	private Map<QuestionMC, Collection<Set<Choice>>> forbiddenChoiceCombinations = Collections.emptyMap();

	@Override
	public InterviewObject[] getNextSequenceItems(Session session) {
		Form nextForm = session.getSessionObject(
				session.getPSMethodInstance(PSMethodInterview.class)).nextForm();
		if (nextForm != null) {
			List<Question> activeQuestions = nextForm.getActiveQuestions();
			return activeQuestions.toArray(new InterviewObject[activeQuestions.size()]);
		}
		else {
			return new InterviewObject[] {};
		}
	}

	@Override
	public FactSet[] getNextSequenceAnswers(Session session, InterviewObject[] interviewItems) {
		// if there is no next question we return null to stop the sequence
		// generation for this interview branch
		Question nextQuestion = searchFirstActiveQuestion(session, interviewItems);
		if (nextQuestion == null) {
			return null;
		}
		// otherwise create the possible answers for this question
		List<? extends Value> determinedValues = determineValues(nextQuestion);
		// for each possible value we create a FactSet
		// we only answer one (the selected) question per FactSet
		FactSet[] result = new FactSet[determinedValues.size()];
		int index = 0;
		for (Value value : determinedValues) {
			Fact fact = FactFactory.createUserEnteredFact(nextQuestion, value);
			FactSet factSet = new FactSet();
			factSet.addValueFact(fact);
			result[index++] = factSet;
		}
		return result;
	}

	@Override
	public Solution[] getExpectedSolutions(Session session) {
		return computeExpectedSolutions(session);
	}

	public static Solution[] computeExpectedSolutions(Session session) {
		final Blackboard blackboard = session.getBlackboard();
		// collect all relevant solutions
		List<Solution> result = new LinkedList<Solution>();
		result.addAll(blackboard.getSolutions(State.ESTABLISHED));
		result.addAll(blackboard.getSolutions(State.SUGGESTED));
		result.addAll(blackboard.getSolutions(State.EXCLUDED));
		// create the sorted array (first by rating, then by name)
		Solution[] array = result.toArray(new Solution[result.size()]);
		Arrays.sort(array, new Comparator<Solution>() {

			@Override
			public int compare(Solution o1, Solution o2) {
				Rating r1 = blackboard.getRating(o1);
				Rating r2 = blackboard.getRating(o2);
				int result = r1.compareTo(r2);
				if (result != 0) {
					// if the ratings are different
					// sort it in reverse order
					return -result;
				}
				else {
					// otherwise sort by name in natural order
					return o1.getName().compareTo(o2.getName());
				}
			}
		});
		return array;
	}

	private Question searchFirstActiveQuestion(Session session, InterviewObject[] interviewItems) {
		for (InterviewObject item : interviewItems) {
			List<TerminologyObject> successors = KnowledgeBaseUtils.getSuccessors(item);
			for (TerminologyObject successor : successors) {
				if (successor instanceof Question) {
					// only consider unanswered questions
					// that are on the agenda as active
					Question question = (Question) successor;
					Fact fact = session.getBlackboard().getValueFact(question);
					if (fact == null && isRelevant(session, question)) {
						return question;
					}
				}
			}
		}
		return null;
	}

	private boolean isRelevant(Session session, InterviewObject object) {
		// if the object is indicated, it is relevant
		if (session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class)).isActive(
				object)) return true;
		// or if the question is direct child to a relevant qcontainer
		// and a qcontainer is relevant if the qcontainer itself or any ancestor
		// is indicated
		for (TerminologyObject parent : object.getParents()) {
			if ((parent instanceof QContainer) && isRelevant(session, (QContainer) parent)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isFinished(Session session) {
		return !session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class)).nextForm().isNotEmpty();
	}

	/**
	 * Compute all allowed {@link Answer} instances for a specified
	 * {@link Question}. This method will consider the {@link #allowedAnswers}
	 * and {@link #forbiddenAnswers} specified during construction of this
	 * strategy.
	 * 
	 * @param question the specified question
	 * @return a list of allowed answer values for the specified question
	 */
	public List<? extends Value> determineValues(Question question) {
		// if we have allowed answers, we will use them in the first
		List<? extends Value> list = allowedAnswers.get(question);
		if (list != null) {
			return list;
		}
		// otherwise we will create a set of answers and remove the forbidden
		// ones.
		if (question instanceof QuestionOC) {
			list = determineOCValues((QuestionOC) question);
		}
		else if (question instanceof QuestionMC) {
			list = determineMCValues((QuestionMC) question);
		}
		else if (question instanceof QuestionNum) {
			list = determineNumValues((QuestionNum) question);
		}
		else if (question instanceof QuestionText) {
			list = determineTextValues((QuestionText) question);
		}
		else if (question instanceof QuestionDate) {
			list = determineDateValues((QuestionDate) question);
		}
		else {
			list = Arrays.asList(UndefinedValue.getInstance());
		}
		removeForbiddenValues(question, list);
		return list;
	}

	/**
	 * Compute an example set of allowed {@link Value} instances for a specified
	 * {@link QuestionText}.
	 * 
	 * @param question the specified text question
	 * @return a list of allowed answer values for the specified question
	 */
	public List<? extends Value> determineTextValues(QuestionText question) {
		List<Value> answers = new ArrayList<Value>(3);
		answers.add(new TextValue("d3web"));
		answers.add(new TextValue("0123456789"));
		answers.add(new TextValue(""));
		return answers;
	}

	/**
	 * Compute an example set of allowed {@link Value} instances for a specified
	 * {@link QuestionDate}.
	 * 
	 * @param question the specified date question
	 * @return a list of allowed answer values for the specified question
	 */
	public List<? extends Value> determineDateValues(QuestionDate question) {
		List<Value> answers = new ArrayList<Value>(3);
		// now
		answers.add(new DateValue(new Date()));
		// Yesterday
		answers.add(new DateValue(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)));
		// mid of January, 1972
		answers.add(new DateValue(new Date(750 * 24 * 60 * 60 * 1000)));
		return answers;
	}

	/**
	 * Compute an example set of allowed {@link Value} instances for a specified
	 * {@link QuestionNum}.
	 * 
	 * @param question the specified numeric question
	 * @return a list of allowed answer values for the specified question
	 */
	public List<? extends Value> determineNumValues(QuestionNum question) {
		// first get the interval to create values in
		NumericalInterval range = question.getInfoStore().getValue(
				BasicProperties.QUESTION_NUM_RANGE);
		if (range == null) {
			range = defaultNumericalInterval;
		}
		// then create the values
		// we always create one for the minimum value, one for the maximum value
		// and numberOfNumericalValues-2 additional values in between
		List<Value> answers = new LinkedList<Value>();
		double indent = (range.getRight() - range.getLeft()) / (numberOfNumericalValues - 1);
		for (int i = 0; i < numberOfNumericalValues; i++) {
			answers.add(new NumValue(new Double(range.getLeft() + i * indent)));
		}

		return answers;
	}

	/**
	 * Compute an example set of allowed {@link Value} instances for a specified
	 * {@link QuestionOC}.
	 * 
	 * @param question the specified choice question
	 * @return a list of allowed answer values for the specified question
	 */
	public List<? extends Value> determineOCValues(QuestionOC question) {
		List<Choice> choices = question.getAllAlternatives();
		List<Value> result = new ArrayList<Value>(choices.size());
		// add each possible answer to a value
		for (Choice choice : choices) {
			ChoiceValue choiceValue = new ChoiceValue(choice);
			result.add(choiceValue);
		}
		return result;
	}

	/**
	 * Compute an example set of allowed {@link Value} instances for a specified
	 * {@link QuestionMC}.
	 * 
	 * @param question the specified choice question
	 * @return a list of allowed answer values for the specified question
	 */
	public List<? extends Value> determineMCValues(QuestionMC question) {
		List<Choice> choices = question.getAllAlternatives();
		List<MultipleChoiceValue> result = new LinkedList<MultipleChoiceValue>();
		int elements = (int) Math.pow(2, choices.size()) - 1;
		elements = Math.min(elements, getMaxCombinations(question));
		for (int i = 1; i <= elements; i++) {
			String binary = Integer.toBinaryString(i);
			List<Choice> mcValue = new LinkedList<Choice>();
			for (int j = 0; j < binary.length(); j++) {
				if (binary.charAt(binary.length() - 1 - j) == '1') {
					mcValue.add(choices.get(j));
				}
			}
			// if we have forbidden combinations,
			// do not use the value if all of them are in the value
			if (isForbiddenCombination(question, mcValue)) continue;
			if (!isRequiredCombination(question, mcValue)) continue;
			result.add(MultipleChoiceValue.fromChoices(mcValue));
		}
		return result;
	}

	private int getMaxCombinations(QuestionMC question) {
		Integer max = this.maxChoiceCombinationsForQuestion.get(question);
		if (max != null && max > 0) return max;
		if (this.maxChoiceCombinations > 0) return this.maxChoiceCombinations;
		return Integer.MAX_VALUE;
	}

	/**
	 * A combination is required if the following condition holds: "for each
	 * defined required combination: if it shares at least one choice with the
	 * examined combination, all of the examined choices are in that required
	 * combination."
	 * <p>
	 * natural speaking this means that is one choice is selected all other
	 * choices that are defined in matching required groups have to be also
	 * selected.
	 * <p>
	 * example: if choice "A" is checked and required groups are {"A","B"},
	 * {"C","D"}, {"D","E"} the method returns false, as "B" is required to be
	 * selected with "A". For choice "F" it return true, because "F" does not
	 * require any additional choice.
	 * 
	 * @created 21.04.2011
	 * @param question the question to be examined
	 * @param mcValue the value combination to be examined
	 * @return if the
	 */
	private boolean isRequiredCombination(QuestionMC question, List<Choice> mcValue) {
		Collection<Set<Choice>> requiredCombinations = this.requiredChoiceCombinations.get(question);
		if (requiredCombinations == null) return true;
		for (Set<Choice> combination : requiredCombinations) {
			// only check combinations that have common elements
			if (Collections.disjoint(combination, mcValue)) continue;
			// if all of the mc value are inside the combination it is ok
			if (combination.containsAll(mcValue)) continue;
			// otherwise we have a problem
			return false;
		}
		// if we have passed all combinations, we are fine
		return true;
	}

	/**
	 * A combination is forbidden if it covers any of the enumerated forbidden
	 * combinations.
	 * 
	 * @created 21.04.2011
	 * @param question the question of the value to be checked
	 * @param mcValue the value to be checked
	 * @return if the mcValue contains / covers a forbidden combination
	 */
	private boolean isForbiddenCombination(QuestionMC question, Collection<Choice> mcValue) {
		Collection<Set<Choice>> combinations = this.forbiddenChoiceCombinations.get(question);
		if (combinations == null) return false;
		for (Set<Choice> combination : combinations) {
			if (mcValue.containsAll(combination)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * removes the forbidden values for a specified question from the specified
	 * (mutable) list of values. After this operation the specified list has
	 * been altered to contain no forbidden question. the forbidden values are
	 * those ones specified during creation of this instance.
	 * 
	 * @created 21.04.2011
	 * @param question the question of the specified values
	 * @param values the list to remove the forbidden values from
	 */
	public void removeForbiddenValues(Question question, List<? extends Value> values) {
		// and if there are a list specified for this question
		List<Value> list = forbiddenAnswers.get(question);
		if (list == null) return;
		// remove it
		values.removeAll(list);
	}

	public void setNumberOfNumericalValues(int numberOfNumericalValues) {
		this.numberOfNumericalValues = numberOfNumericalValues;
	}

	public void setDefaultNumericlaInterval(NumericalInterval defaultNumericalInterval) {
		this.defaultNumericalInterval = defaultNumericalInterval;
	}

	public void setForbiddenAnswers(Map<Question, List<Value>> forbiddenAnswers) {
		this.forbiddenAnswers = forbiddenAnswers;
	}

	public void setAllowedAnswers(Map<Question, List<Value>> allowedAnswers) {
		this.allowedAnswers = allowedAnswers;
	}

	public void setMaxChoiceCombinations(int maxChoiceCombinations) {
		this.maxChoiceCombinations = maxChoiceCombinations;
	}

	public void setMaxChoiceCombinationsForQuestion(Map<QuestionMC, Integer> maxChoiceCombinationsForQuestion) {
		this.maxChoiceCombinationsForQuestion = maxChoiceCombinationsForQuestion;
	}

	public void setRequiredChoiceCombinations(Map<QuestionMC, Collection<Set<Choice>>> requiredChoiceCombinations) {
		this.requiredChoiceCombinations = requiredChoiceCombinations;
	}

	public void setForbiddenChoiceCombinations(Map<QuestionMC, Collection<Set<Choice>>> forbiddenChoiceCombinations) {
		this.forbiddenChoiceCombinations = forbiddenChoiceCombinations;
	}
}

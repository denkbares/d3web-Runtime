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

package de.d3web.core.manage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.QuestionZC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

/**
 * A facade controlling (all) operations on a knowledge base. Created on
 * 11.01.2005
 * 
 * @author baumeister
 */
public final class KnowledgeBaseManagement {

	private KnowledgeBase knowledgeBase;

	private KnowledgeBaseManagement(KnowledgeBase k) {
		knowledgeBase = k;
	}

	public static KnowledgeBaseManagement createInstance(KnowledgeBase k) {
		return new KnowledgeBaseManagement(k);
	}

	public static KnowledgeBaseManagement createInstance() {
		KnowledgeBase theKnowledge = createKnowledgeBase();
		return createInstance(theKnowledge);
	}

	public void clearKnowledgeBase() {
		this.knowledgeBase = createKnowledgeBase();
	}

	/**
	 * @return a newly creates knowledge base with one root Solution (P000) and
	 *         one root QContainer (Q000).
	 */
	private static KnowledgeBase createKnowledgeBase() {
		KnowledgeBase theK = new KnowledgeBase();

		// we don't use internal methods, because we need to set
		// the ID/Name/noParent manually.
		Solution p000 = new Solution(theK, "P000");
		theK.setRootSolution(p000);

		QContainer q000 = new QContainer(theK, "Q000");
		theK.setRootQASet(q000);

		return theK;
	}

	public Solution createSolution(String name, Solution parent) {
		Solution d = new Solution(knowledgeBase, name);
		addToParent(d, parent);
		return d;
	}

	/**
	 * Creates a new solution and adds the instance as child of the root of the
	 * solution hierarchy.
	 * 
	 * @param name The name of the new solution
	 * @return the newly created solution
	 */
	public Solution createSolution(String name) {
		return createSolution(name, knowledgeBase.getRootSolution());
	}

	/**
	 * Creates a new questionnaire with the specified name as a child of the
	 * root questionnaire hierarchy.
	 * 
	 * @param name the specified name of the questionnaire
	 * @return the newly created {@link QContainer}
	 */
	public QContainer createQContainer(String name) {
		return createQContainer(name, knowledgeBase.getRootQASet());
	}

	public QContainer createQContainer(String name, QASet parent) {
		if (parent instanceof Question) {
			throw new IllegalArgumentException("Parent is a question, only QContainers allowed");
		}
		QContainer q = new QContainer(knowledgeBase, name);
		addToParent(q, parent);
		return q;
	}

	public QuestionOC createQuestionOC(String name, QASet parent,
			Choice[] answers) {
		QuestionOC q = new QuestionOC(knowledgeBase, name);
		setChoiceProperties(q, parent, answers);
		return q;
	}

	public QuestionOC createQuestionOC(String name, QASet parent,
			String[] answers) {
		QuestionOC q = createQuestionOC(name, parent, new Choice[] {});
		q.setAlternatives(toList(createAnswers(q, answers)));
		return q;
	}

	public QuestionZC createQuestionZC(String name, QASet parent) {
		QuestionZC q = new QuestionZC(knowledgeBase, name);
		setChoiceProperties(q, parent, new Choice[] {});
		return q;
	}

	private void setChoiceProperties(QuestionChoice q, QASet parent, Choice[] answers) {
		addToParent(q, parent);
		q.setAlternatives(toList(answers));
	}

	public QuestionMC createQuestionMC(String name, QASet parent,
			Choice[] answers) {
		QuestionMC q = new QuestionMC(knowledgeBase, name);
		setChoiceProperties(q, parent, answers);
		return q;
	}

	public QuestionMC createQuestionMC(String name, QASet parent,
			String[] answers) {
		QuestionMC q = createQuestionMC(name, parent, new Choice[] {});
		q.setAlternatives(toList(createAnswers(q, answers)));
		return q;
	}

	public QuestionNum createQuestionNum(String name, QASet parent) {
		QuestionNum q = new QuestionNum(knowledgeBase, name);
		addToParent(q, parent);
		return q;
	}

	public QuestionYN createQuestionYN(String name, QASet parent) {
		return createQuestionYN(null, name, parent);
	}

	public QuestionYN createQuestionYN(String id, String name, QASet parent) {
		return createQuestionYN(name, null, null, parent);
	}

	public QuestionYN createQuestionYN(String name, String yesAlternativeText,
			String noAlternativeText, QASet parent) {
		QuestionYN q = null;
		if (yesAlternativeText != null && noAlternativeText != null) {
			q = new QuestionYN(knowledgeBase, name, yesAlternativeText, noAlternativeText);
		}
		else {
			q = new QuestionYN(knowledgeBase, name);
		}
		addToParent(q, parent);
		return q;
	}

	public QuestionDate createQuestionDate(String name, QASet parent) {
		QuestionDate q = new QuestionDate(knowledgeBase, name);
		addToParent(q, parent);
		return q;
	}

	public QuestionText createQuestionText(String name, QASet parent) {
		return createQuestionText(null, name, parent);
	}

	public QuestionText createQuestionText(String id, String name, QASet parent) {
		QuestionText q = new QuestionText(knowledgeBase, name);
		addToParent(q, parent);
		return q;
	}

	public Choice addChoiceAnswer(QuestionChoice question, String answerText) {
		Choice answer = AnswerFactory.createAnswerChoice(answerText);
		question.addAlternative(answer);
		return answer;
	}

	public Choice addChoiceAnswer(QuestionChoice question, String answerText, int pos) {
		Choice answer = AnswerFactory.createAnswerChoice(answerText);
		question.addAlternative(answer, pos);
		return answer;
	}

	private Choice[] createAnswers(QuestionChoice q, String[] answers) {
		Choice[] a = new Choice[answers.length];
		for (int i = 0; i < answers.length; i++) {
			a[i] = AnswerFactory.createAnswerChoice(
					answers[i]);
		}
		return a;
	}

	/**
	 * Arrays.asList creates immutable lists, therefore an own method :-(
	 * 
	 * @param answers
	 * @return
	 */
	private static List<Choice> toList(Choice[] answers) {
		if (answers == null) {
			return new LinkedList<Choice>();
		}
		ArrayList<Choice> l = new ArrayList<Choice>(answers.length);
		for (int i = 0; i < answers.length; i++) {
			l.add(answers[i]);
		}
		return l;
	}

	/**
	 * Returns the Solution object for which either the text or the id is equal
	 * to the specified name String
	 * 
	 * @param name a specified name string
	 * @return a Diagnosis object or null, if nothing found
	 */
	public Solution findSolution(String name) {
		// Uses hash for name in KB
		NamedObject ob = knowledgeBase.getManager().searchObjectForName(name);
		if (ob instanceof Solution) {
			return (Solution) ob;
		}
		TerminologyObject o = findNamedObject(name, knowledgeBase.getManager().getSolutions());
		if (o instanceof Solution) {
			return (Solution) o;
		}
		return null;
	}

	/**
	 * Returns the Question object for which either the text or the id is equal
	 * to the specified name String
	 * 
	 * @param name a specified name string
	 * @return a Question object or null, if nothing found
	 */
	public Question findQuestion(String name) {
		// Uses hash for name in KB
		NamedObject ob = knowledgeBase.getManager().searchObjectForName(name);
		if (ob instanceof Question) {
			return (Question) ob;
		}
		TerminologyObject o = findNamedObject(name, knowledgeBase.getManager().getQuestions());
		if (o instanceof Question) {
			return (Question) o;
		}
		return null;
	}

	/**
	 * Returns the QContainer object for which either the text or the id is
	 * equal to the specified name String
	 * 
	 * @param name a specified name string
	 * @return a QContainer object or null, if nothing found
	 */
	public QContainer findQContainer(String name) {
		// Uses hash for name in KB
		NamedObject ob = knowledgeBase.getManager().searchObjectForName(name);
		if (ob instanceof QContainer) {
			return (QContainer) ob;
		}
		TerminologyObject o = findNamedObject(name, knowledgeBase.getManager()
				.getQContainers());
		if (o instanceof QContainer) {
			return (QContainer) o;
		}
		return null;
	}

	private TerminologyObject findNamedObject(String name,
			Collection<? extends TerminologyObject> namedObjects) {
		// old iterating search method
		for (TerminologyObject o : namedObjects) {
			if (o != null && name != null
					&& (name.equals(o.getName()))) {
				return o;
			}
		}
		return null;
	}

	/**
	 * Retrieves the AnswerChoice object contained in the alternatives list of
	 * the specified question, that has the specified text as answer text
	 * 
	 * @param question the specified question
	 * @param answerText the requested answer text
	 * @return null, if no answer found for specified params
	 */
	public Choice findChoice(QuestionChoice question,
			String answerText) {
		if (question == null || question.getAllAlternatives() == null
				|| answerText == null) {
			return null;
		}
		for (Choice choice : question.getAllAlternatives()) {
			if (answerText.equals(choice.getName())) {
				return choice;
			}
		}
		return null;
	}

	public MultipleChoiceValue findMultipleChoiceValue(QuestionMC quesiton, List<String> valueNames) {
		List<Choice> choices = new ArrayList<Choice>(valueNames.size());
		for (String name : valueNames) {
			Choice choice = findChoice(quesiton, name);
			if (choice != null) {
				choices.add(choice);
			}
		}
		return MultipleChoiceValue.fromChoices(choices);
	}

	public Value findValue(Question question, String valueString) {
		if (valueString.equals(UndefinedValue.UNDEFINED_ID)) {
			return UndefinedValue.getInstance();
		}
		if (valueString.equals(Unknown.UNKNOWN_ID)) {
			return Unknown.getInstance();
		}

		// multiple choice question given
		if (question instanceof QuestionMC) {

			List<Choice> values = new LinkedList<Choice>();

			// if mc val is a "real" mc val, i.e. more than one answervals
			if (ChoiceID.isEncodedChoiceIDs(valueString)) {
				ChoiceID[] mcvals = ChoiceID.decodeChoiceIDs(valueString);
				for (ChoiceID val : mcvals) {
					Choice choice = val.getChoice((QuestionChoice) question);
					if (choice == null) {
						return null;
					}
					else {
						values.add(choice);
					}
				}
				return MultipleChoiceValue.fromChoices(values);
			}

			// else, if a single answer val should be set for a mc question
			else {
				Choice choice = findChoice((QuestionChoice) question, valueString);
				if (choice == null) {
					return null;
				}
				else {
					values.add(choice);
					return MultipleChoiceValue.fromChoices(values);
				}
			}
		}

		// choice question given (e.g., question yn, questionoc)
		else if (question instanceof QuestionChoice) {
			Choice choice = findChoice((QuestionChoice) question, valueString);
			if (choice == null) {
				return null;
			}
			else {
				return new ChoiceValue(choice);
			}
		}

		// num questions
		else if (question instanceof QuestionNum) {
			return new NumValue(Double.parseDouble(valueString));
		}

		// text questions
		else if (question instanceof QuestionText) {
			return new TextValue(valueString);
		}

		// date questions
		// HOTFIX 2010-09-09 Date questions currently only work with the
		// dateformat below.
		// TODO Need a better overall solution for date questions!
		else if (question instanceof QuestionDate) {
			try {
				final DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
				return new DateValue(format.parse(valueString));
			}
			catch (ParseException e) {
				return null;
				// throw new IllegalArgumentException(
				// "The committed String is not a correctly formatted date: " +
				// e.getMessage());
			}
		}
		else {
			return UndefinedValue.getInstance();
		}
	}

	private void addToParent(QASet theObject, QASet parent) {
		if (parent != null) {
			parent.addChild(theObject);
		}
		else {
			knowledgeBase.getRootQASet().addChild(theObject);
		}
	}

	private void addToParent(Solution theObject, Solution parent) {
		if (parent != null) {
			parent.addChild(theObject);
		}
		else {
			knowledgeBase.getRootSolution().addChild(theObject);
		}
	}

	public KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

	/**
	 * Sorts a given list of QContainer according to DFS
	 * 
	 * @param unsorted the unsorted list
	 */
	public void sortQContainers(List<QContainer> unsorted) {
		HashMap<TerminologyObject, Integer> qcontainerIndex = new HashMap<TerminologyObject, Integer>();
		reindex(knowledgeBase.getRootQASet(), qcontainerIndex, Integer.valueOf(0));
		Collections.sort(unsorted, new DFSTreeSortingComparator(qcontainerIndex));
	}

	/**
	 * Traverses the QASet hierarchy using a depth-first search and attaches an
	 * ordering number to each visited {@link QASet}.
	 */
	private void reindex(TerminologyObject qaset, Map<TerminologyObject, Integer> qcontainerIndex, Integer maxOrderingNumber) {
		qcontainerIndex.put(qaset, maxOrderingNumber);
		Integer maxOrdNum = maxOrderingNumber;

		for (TerminologyObject child : qaset.getChildren()) {
			maxOrdNum++;
			if (!qcontainerIndex.containsKey(child)) {
				reindex(child, qcontainerIndex, maxOrdNum);
			}
			else {
				continue;// terminate recursion in case of cyclic hierarchies
			}
		}
	}

	/**
	 * Private Comparator class that sorts a given QContaier map, where the
	 * QContainers have previously been traversed DFS and given the according
	 * index number
	 */

	private class DFSTreeSortingComparator implements Comparator<QContainer> {

		private final Map<TerminologyObject, Integer> index;

		public DFSTreeSortingComparator(
				Map<TerminologyObject, Integer> qasetIndex) {
			this.index = qasetIndex;
		}

		@Override
		public int compare(QContainer entry1, QContainer entry2) {
			int order1 = this.index.get(entry1);
			int order2 = this.index.get(entry2);
			return order1 - order2;
		}
	}

	/**
	 * Finds the {@link TerminologyObject} with the specified name. This method
	 * is case insensitive
	 * 
	 * @created 10.11.2010
	 * @param name Name of the {@link TerminologyObject}
	 * @return {@link TerminologyObject} with the specified name
	 */
	public TerminologyObject findTerminologyObjectByName(String name) {
		List<TerminologyObject> objects = new LinkedList<TerminologyObject>();
		objects.addAll(knowledgeBase.getManager().getQContainers());
		objects.addAll(knowledgeBase.getManager().getSolutions());
		objects.addAll(knowledgeBase.getManager().getQuestions());
		for (TerminologyObject object : objects) {
			if (object.getName().equalsIgnoreCase(name)) {
				return object;
			}
		}
		return null;
	}

}

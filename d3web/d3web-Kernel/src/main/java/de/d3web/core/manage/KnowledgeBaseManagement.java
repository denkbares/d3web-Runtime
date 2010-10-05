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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.IDObject;
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
	private int internalCounter = 0;

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
		Solution p000 = new Solution("P000");
		p000.setName("P000");
		theK.add(p000);
		theK.setRootSolution(p000);

		QContainer q000 = new QContainer("Q000");
		q000.setName("Q000");
		theK.add(q000);
		theK.setRootQASet(q000);

		return theK;
	}

	public Solution createSolution(String id, String name, Solution parent) {
		Solution d;
		if (id == null) {
			d = new Solution(findNewIDFor(Solution.class));
		}
		else {
			d = new Solution(id);
		}
		d.setName(name);
		addToParent(d, parent);
		knowledgeBase.add(d);
		return d;
	}

	public Solution createSolution(String name, Solution parent) {
		return createSolution(null, name, parent);
	}

	public Solution createSolution(String id, String name) {
		return createSolution(id, name, knowledgeBase.getRootSolution());
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

	public QContainer createQContainer(String id, String name) {
		return createQContainer(id, name, knowledgeBase.getRootQASet());
	}

	public QContainer createQContainer(String name, QASet parent) {
		return createQContainer(null, name, parent);
	}

	public QContainer createQContainer(String id, String name, QASet parent) {
		if (parent instanceof Question) {
			throw new IllegalArgumentException("Parent is a question, only QContainers allowed");
		}
		QContainer q;
		if (id == null) {
			q = new QContainer(findNewIDFor(QContainer.class));
		}
		else {
			q = new QContainer(id);
		}
		q.setName(name);
		addToParent(q, parent);
		knowledgeBase.add(q);
		return q;
	}

	public QuestionOC createQuestionOC(String name, QASet parent,
			Choice[] answers) {
		return createQuestionOC(null, name, parent, answers);
	}

	public QuestionOC createQuestionOC(String id, String name, QASet parent,
			Choice[] answers) {
		String questionID = (id == null ? findNewIDFor(Question.class) : id);
		QuestionOC q = new QuestionOC(questionID);
		setChoiceProperties(q, name, parent, answers);
		return q;
	}

	public QuestionOC createQuestionOC(String name, QASet parent,
			String[] answers) {
		return createQuestionOC(null, name, parent, answers);
	}

	public QuestionOC createQuestionOC(String id, String name, QASet parent,
			String[] answers) {
		QuestionOC q = createQuestionOC(id, name, parent, new Choice[] {});
		q.setAlternatives(toList(createAnswers(q, answers)));
		return q;
	}

	public QuestionZC createQuestionZC(String name, QASet parent) {
		return createQuestionZC(null, name, parent);
	}

	public QuestionZC createQuestionZC(String id, String name, QASet parent) {
		String questionID = (id == null ? findNewIDFor(Question.class) : id);
		QuestionZC q = new QuestionZC(questionID);
		setChoiceProperties(q, name, parent, new Choice[] {});
		return q;
	}

	private void setChoiceProperties(QuestionChoice q, String name,
			QASet parent, Choice[] answers) {
		q.setName(name);
		addToParent(q, parent);
		q.setAlternatives(toList(answers));
		knowledgeBase.add(q);
	}

	public QuestionMC createQuestionMC(String name, QASet parent,
			Choice[] answers) {
		return createQuestionMC(null, name, parent, answers);
	}

	public QuestionMC createQuestionMC(String id, String name, QASet parent,
			Choice[] answers) {
		String questionID = (id == null ? findNewIDFor(Question.class) : id);
		QuestionMC q = new QuestionMC(questionID);
		setChoiceProperties(q, name, parent, answers);
		return q;
	}

	public QuestionMC createQuestionMC(String name, QASet parent,
			String[] answers) {
		return createQuestionMC(null, name, parent, answers);
	}

	public QuestionMC createQuestionMC(String id, String name, QASet parent,
			String[] answers) {
		QuestionMC q = createQuestionMC(id, name, parent, new Choice[] {});
		q.setAlternatives(toList(createAnswers(q, answers)));
		return q;
	}

	public QuestionNum createQuestionNum(String name, QASet parent) {
		return createQuestionNum(null, name, parent);
	}

	public QuestionNum createQuestionNum(String id, String name, QASet parent) {
		String questionID = (id == null ? findNewIDFor(Question.class) : id);
		QuestionNum q = new QuestionNum(questionID);
		q.setName(name);
		addToParent(q, parent);
		knowledgeBase.add(q);
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
		return createQuestionYN(null, name, yesAlternativeText, noAlternativeText, parent);
	}

	public QuestionYN createQuestionYN(String id, String name, String yesAlternativeText,
			String noAlternativeText, QASet parent) {
		String questionID = (id == null ? findNewIDFor(Question.class) : id);
		QuestionYN q = null;
		if (yesAlternativeText != null && noAlternativeText != null) {
			q = new QuestionYN(questionID, yesAlternativeText, noAlternativeText);
		}
		else {
			q = new QuestionYN(questionID);
		}
		q.setName(name);
		addToParent(q, parent);
		knowledgeBase.add(q);
		return q;
	}

	public QuestionDate createQuestionDate(String name, QASet parent) {
		return createQuestionDate(null, name, parent);
	}

	public QuestionDate createQuestionDate(String id, String name, QASet parent) {
		String questionID = (id == null ? findNewIDFor(Question.class) : id);
		QuestionDate q = new QuestionDate(questionID);
		q.setName(name);
		addToParent(q, parent);
		knowledgeBase.add(q);
		return q;
	}

	public QuestionText createQuestionText(String name, QASet parent) {
		return createQuestionText(null, name, parent);
	}

	public QuestionText createQuestionText(String id, String name, QASet parent) {
		String questionID = (id == null ? findNewIDFor(Question.class) : id);
		QuestionText q = new QuestionText(questionID);
		q.setName(name);
		addToParent(q, parent);
		knowledgeBase.add(q);
		return q;
	}

	public Choice addChoiceAnswer(QuestionChoice question, String answerText) {
		String answerID = getNewAnswerAlternativeFor(question);
		Choice answer = AnswerFactory.createAnswerChoice(answerID,
				answerText);
		question.addAlternative(answer);
		return answer;
	}

	public Choice addChoiceAnswer(QuestionChoice question, String answerText, int pos) {
		String answerID = getNewAnswerAlternativeFor(question);
		Choice answer = AnswerFactory.createAnswerChoice(answerID,
				answerText);
		question.addAlternative(answer, pos);
		return answer;
	}

	private String getNewAnswerAlternativeFor(QuestionChoice question) {
		int maxCount = 0;
		for (Iterator<Choice> iter = question.getAllAlternatives().iterator(); iter
				.hasNext();) {
			Choice answer = iter.next();
			int position = answer.getId().lastIndexOf("a") + 1;
			if (position < answer.getId().length()) {
				String countStr = answer.getId().substring(position);
				int count = -1;
				try {
					count = Integer.parseInt(countStr);
				}
				catch (NumberFormatException e) {
					maxCount = question.getAllAlternatives().size();
					break;
				}
				if (count > maxCount) {
					maxCount = count;
				}
			}
		}
		return question.getId() + "a" + (maxCount + 1);
	}

	private Choice[] createAnswers(QuestionChoice q, String[] answers) {
		Choice[] a = new Choice[answers.length];
		for (int i = 0; i < answers.length; i++) {
			a[i] = AnswerFactory.createAnswerChoice(q.getId() + "a" + (i + 1),
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
		IDObject ob = knowledgeBase.searchObjectForName(name);
		if (ob instanceof Solution) {
			return (Solution) ob;
		}
		NamedObject o = findNamedObject(name, knowledgeBase.getSolutions());
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
		IDObject ob = knowledgeBase.searchObjectForName(name);
		if (ob instanceof Question) {
			return (Question) ob;
		}
		NamedObject o = findNamedObject(name, knowledgeBase.getQuestions());
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
		IDObject ob = knowledgeBase.searchObjectForName(name);
		if (ob instanceof QContainer) {
			return (QContainer) ob;
		}
		NamedObject o = findNamedObject(name, knowledgeBase
				.getQContainers());
		if (o instanceof QContainer) {
			return (QContainer) o;
		}
		return null;
	}

	private NamedObject findNamedObject(String name,
			Collection<? extends NamedObject> namedObjects) {
		// old iterating search method
		for (NamedObject o : namedObjects) {
			if (o != null && name != null
					&& (name.equals(o.getName()) || name.equals(o.getId()))) {
				return o;
			}
		}
		return null;
	}

	/**
	 * Retrieves the AnswerChoice object contained in the alternatives list of
	 * the specified question, that either has the specified text as answer text
	 * or as id or has an id that is a concatenation of questionId and the given
	 * text.
	 * 
	 * @param question the specified question
	 * @param answerText the requested answer text or id
	 * @return null, if no answer found for specified params
	 */
	public Choice findChoice(QuestionChoice question,
			String answerText) {
		if (question == null || question.getAllAlternatives() == null
				|| answerText == null) {
			return null;
		}
		for (Choice choice : question.getAllAlternatives()) {
			if (answerText.equals(choice.getName())
					|| answerText.equals(choice.getId())
					|| choice.getId().equals((question.getId() + answerText))) {
				return choice;
			}
		}
		return null;
	}

	public MultipleChoiceValue findMultipleChoiceValue(QuestionMC quesiton, List<String> valueNames) {
		List<ChoiceValue> choiceValues = new ArrayList<ChoiceValue>(valueNames.size());
		for (String name : valueNames) {
			Choice choice = findChoice(quesiton, name);
			if (choice != null) {
				choiceValues.add(new ChoiceValue(choice));
			}
		}
		return new MultipleChoiceValue(choiceValues);
	}

	public Value findValue(Question question,
			String valueString) { // NOSONAR ignore cyclomatic complexity
									// warning
		if (valueString.equals(UndefinedValue.UNDEFINED_ID)) {
			return UndefinedValue.getInstance();
		}
		if (valueString.equals(Unknown.UNKNOWN_ID)) {
			return Unknown.getInstance();
		}

		// multiple choice question given
		if (question instanceof QuestionMC) {

			List<ChoiceValue> values = new LinkedList<ChoiceValue>();

			// if mc val is a "real" mc val, i.e. more than one answervals
			if (valueString.contains(MultipleChoiceValue.ID_SEPARATOR)) {
				String[] mcvals = valueString.split(MultipleChoiceValue.ID_SEPARATOR);
				for (String val : mcvals) {
					Choice choice = findChoice((QuestionChoice) question, val);
					if (choice == null) {
						return null;
					}
					else {
						values.add(new ChoiceValue(choice));
					}
				}
				return new MultipleChoiceValue(values);
			}

			// else, if a single answer val should be set for a mc question
			else {
				Choice choice = findChoice((QuestionChoice) question, valueString);
				if (choice == null) {
					return null;
				}
				else {
					values.add(new ChoiceValue(choice));
					return new MultipleChoiceValue(values);
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

	public String findNewIDFor(Class<? extends IDObject> o) {
		if (o == Solution.class) {
			int idC = getMaxCountOf(knowledgeBase.getSolutions()) + 1;
			return "P" + idC;

		}
		else if (o == QContainer.class) {
			int idC = getMaxCountOf(knowledgeBase.getQContainers()) + 1;
			return "QC" + idC;

		}
		else if (o == Question.class) {
			int idC = getMaxCountOf(knowledgeBase.getQuestions()) + 1;
			return "Q" + idC;

		}
		else {
			internalCounter++;
			return "O" + internalCounter;
		}
	}

	public String createRuleID() {
		Collection<Rule> rules = new ArrayList<Rule>();
		for (KnowledgeSlice ks : knowledgeBase.getAllKnowledgeSlices()) {
			if (ks instanceof RuleSet) {
				RuleSet rs = (RuleSet) ks;
				rules.addAll(rs.getRules());
			}
		}
		int idC = getMaxCountOf(rules) + 1;
		return "R" + idC;
	}

	public String findNewIDForAnswerChoice(QuestionChoice qc) {
		int returnIDnumber = 1;
		String questionID = qc.getId();
		List<Choice> answerList = qc.getAllAlternatives();
		for (Choice a : answerList) {
			String answerID = a.getId();
			String beginning = questionID + "a";
			if (answerID.startsWith(beginning)) {
				String number = answerID.substring(beginning.length(), answerID
						.length());
				int numberInt = Integer.valueOf(number);
				if (numberInt >= returnIDnumber) {
					returnIDnumber = numberInt + 1;
				}
			}
		}
		return questionID + "a" + returnIDnumber;
	}

	/**
	 * Determines the max. number used as a suffix for the specified kbObjects
	 * (IDObject). For security reasons the methods _always_ runs through the
	 * given list of objects and checks their suffices.
	 * 
	 * @param kbObjects IDObject instances
	 * @return the maximum number used as suffix
	 */
	private int getMaxCountOf(Collection<?> kbObjects) {
		int maxCount = 0;
		for (Iterator<?> iter = kbObjects.iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (obj instanceof IDObject) {
				String id = ((IDObject) obj).getId();
				int suffix = getSuffix(id);
				if ((suffix != -1) && (suffix > maxCount)) {
					maxCount = suffix;
				}
			}
			if (obj instanceof Rule) {
				String id = ((Rule) obj).getId();
				int suffix = getSuffix(id);
				if ((suffix != -1) && (suffix > maxCount)) {
					maxCount = suffix;
				}
			}
		}
		return maxCount;
	}

	private int getSuffix(String id) {
		String number = "";
		for (int i = id.length() - 1; i > -1; i--) {
			if (isNumber(id.charAt(i))) {
				number = id.charAt(i) + number;
			}
		}
		if (number.length() > 0) {
			return Integer.parseInt(number);
		}
		return -1;
	}

	private boolean isNumber(char c) {
		try {
			Integer.parseInt(c + "");
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Sets ID, name, parent
	 * 
	 * @param theObject
	 * @param name
	 * @param parent
	 */
	// private void setBasicProperties(NamedObject theObject, String name) {
	// theObject.setText(name);
	// }

	/**
	 * Adds the specified object to the given parent. If parent==null then add
	 * the object to the corresponding root object.
	 * 
	 * @param theObject
	 * @param parent
	 */
	private void addToParent(NamedObject theObject, NamedObject parent) {
		if (parent != null) {
			parent.addChild(theObject);
		}
		else {
			if (theObject instanceof Solution) {
				knowledgeBase.getRootSolution().addChild(theObject);
			}
			else if (theObject instanceof QASet) {
				knowledgeBase.getRootQASet().addChild(theObject);
			}
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

}

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
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
 * Provides utilitymethods for {@link KnowledgeBase}
 * 
 * @author baumeister
 */
public final class KnowledgeBaseUtils {

	private KnowledgeBaseUtils() {
	}

	/**
	 * @return a newly creates knowledge base with one root Solution (P000) and
	 *         one root QContainer (Q000).
	 */
	public static KnowledgeBase createKnowledgeBase() {
		KnowledgeBase theK = new KnowledgeBase();

		// we don't use internal methods, because we need to set
		// the ID/Name/noParent manually.
		Solution p000 = new Solution(theK, "P000");
		theK.setRootSolution(p000);

		QContainer q000 = new QContainer(theK, "Q000");
		theK.setRootQASet(q000);

		return theK;
	}

	public static Choice addChoiceAnswer(QuestionChoice question, String answerText, int pos) {
		Choice answer = new Choice(answerText);
		question.addAlternative(answer, pos);
		return answer;
	}

	/**
	 * Retrieves the AnswerChoice object contained in the alternatives list of
	 * the specified question, that has the specified text as answer text
	 * 
	 * @param question the specified question
	 * @param answerText the requested answer text
	 * @return null, if no answer found for specified params
	 */
	public static Choice findChoice(QuestionChoice question, String answerText) {
		if (question == null
				|| question.getAllAlternatives() == null
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

	public static MultipleChoiceValue findMultipleChoiceValue(QuestionMC question, List<String> valueNames) {
		List<Choice> choices = new ArrayList<Choice>(valueNames.size());
		for (String name : valueNames) {
			Choice choice = findChoice(question, name);
			if (choice != null) {
				choices.add(choice);
			}
		}
		return MultipleChoiceValue.fromChoices(choices);
	}

	public static Value findValue(Question question, String valueString) {
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

	/**
	 * Sorts a given list of QContainer according to DFS
	 * 
	 * @param unsorted the unsorted list
	 */
	public static void sortQContainers(List<QContainer> unsorted) {
		if (unsorted.isEmpty()) {
			// empty list doesn't need be sorted
			return;
		}
		KnowledgeBase knowledgeBase = unsorted.get(0).getKnowledgeBase();
		HashMap<TerminologyObject, Integer> qcontainerIndex = new HashMap<TerminologyObject, Integer>();
		reindex(knowledgeBase.getRootQASet(), qcontainerIndex, Integer.valueOf(0));
		Collections.sort(unsorted, new DFSTreeSortingComparator(qcontainerIndex));
	}

	/**
	 * Traverses the QASet hierarchy using a depth-first search and attaches an
	 * ordering number to each visited {@link QASet}.
	 */
	private static void reindex(TerminologyObject qaset, Map<TerminologyObject, Integer> qcontainerIndex, Integer maxOrderingNumber) {
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

	private static class DFSTreeSortingComparator implements Comparator<QContainer> {

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
	public static TerminologyObject findTerminologyObjectByName(String name, KnowledgeBase knowledgeBase) {
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

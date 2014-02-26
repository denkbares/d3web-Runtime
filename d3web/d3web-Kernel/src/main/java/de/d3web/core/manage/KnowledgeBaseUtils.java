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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.d3web.core.knowledge.InfoStore;
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
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.utils.Triple;

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

	public static boolean isInLoop(TerminologyObject object) {
		return isInLoop(new HashSet<TerminologyObject>(), object);
	}

	private static boolean isInLoop(Set<TerminologyObject> visited, TerminologyObject object) {
		if (visited.contains(object)) return true;
		visited.add(object);
		for (TerminologyObject parent : object.getParents()) {
			boolean loop = isInLoop(visited, parent);
			if (loop) return true;
		}
		visited.remove(object);
		return false;
	}

	/**
	 * Collects all ancestors starting from the specified object. Duplicate
	 * objects will be contained only once at its first occurrence. The
	 * specified terminologyObject is always the first element of this list.
	 * 
	 * @created 26.02.2013
	 * @param terminologyObject the leaf of the sub-tree to be specified
	 * @return the ancestors of the given {@link TerminologyObject}
	 */
	public static List<TerminologyObject> getAncestors(TerminologyObject terminologyObject) {
		return getAncestors(terminologyObject, TerminologyObject.class);

	}

	/**
	 * Collects all tree ancestors of a specified type starting from the
	 * specified object. Duplicate objects will be contained only once at its
	 * first occurrence. The specified terminologyObject is always the first
	 * element of this list.
	 * 
	 * @created 26.02.2013
	 * @param <T> the type of the ancestors to be found
	 * @param terminologyObject the leaf where the search starts
	 * @param typeOf the class of the ancestors to be found
	 * @return the ancestors of the given {@link TerminologyObject}
	 */
	public static <T extends TerminologyObject> List<T> getAncestors(TerminologyObject parent, Class<T> typeOf) {
		List<T> result = new LinkedList<T>();
		Set<TerminologyObject> visited = new HashSet<TerminologyObject>();
		collectAncestors(parent, visited, result, typeOf);
		return Collections.unmodifiableList(result);
	}

	private static <T extends TerminologyObject> void collectAncestors(TerminologyObject terminologyObject, Set<TerminologyObject> visited, List<T> result, Class<T> typeOf) {
		// if not already visited, we add the object...
		if (visited.contains(terminologyObject)) return;
		visited.add(terminologyObject);

		// ...add the current item if matches
		if (typeOf.isInstance(terminologyObject)) {
			result.add(typeOf.cast(terminologyObject));
		}
		// ...and process its parents recursively
		for (TerminologyObject parent : terminologyObject.getParents()) {
			collectAncestors(parent, visited, result, typeOf);
		}
	}

	/**
	 * Collects all tree successors starting from the specified object. The
	 * objects are collected in a depth first order. Duplicate objects (having
	 * multiple parents within this sub-tree) will be contained only once at its
	 * first occurrence. The specified terminologyObject is always the first
	 * element of this list.
	 * 
	 * @created 20.04.2011
	 * @param terminologyObject the root of the sub-tree to be specified
	 * @return the depth-first search tree items
	 */
	public static List<TerminologyObject> getSuccessors(TerminologyObject terminologyObject) {
		return getSuccessors(terminologyObject, TerminologyObject.class);
	}

	/**
	 * Collects all tree successors of a specified type starting from the
	 * specified object. The objects are collected in a depth first order.
	 * Duplicate objects (having multiple parents within this sub-tree) will be
	 * contained only once at its first occurrence. The specified
	 * terminologyObject is always the first element of this list.
	 * 
	 * @created 04.05.2011
	 * @param <T> the type of the successors to be found
	 * @param terminologyObject the root of the sub-tree to be specified
	 * @param typeOf the class of the successors to be found
	 * @return the depth-first search tree items
	 */
	public static <T extends TerminologyObject> List<T> getSuccessors(TerminologyObject terminologyObject, Class<T> typeOf) {
		List<T> result = new LinkedList<T>();
		Set<TerminologyObject> visited = new HashSet<TerminologyObject>();
		collectSuccessors(terminologyObject, visited, result, typeOf);
		return Collections.unmodifiableList(result);
	}

	private static <T extends TerminologyObject> void collectSuccessors(TerminologyObject terminologyObject, Set<TerminologyObject> visited, List<T> result, Class<T> typeOf) {
		// if not already visited, we add the object...
		if (visited.contains(terminologyObject)) return;
		visited.add(terminologyObject);

		// ...add the current item if matches
		if (typeOf.isInstance(terminologyObject)) {
			result.add(typeOf.cast(terminologyObject));
		}
		// ...and process its children recursively
		for (TerminologyObject child : terminologyObject.getChildren()) {
			collectSuccessors(child, visited, result, typeOf);
		}
	}

	/**
	 * Retrieves the AnswerChoice object contained in the alternatives list of
	 * the specified question, that has the specified case sensitive text as
	 * answer text.
	 * 
	 * @param question the specified question
	 * @param answerText the requested answer text
	 * @return null, if no answer found for specified params
	 */
	public static Choice findChoice(QuestionChoice question, String answerText) {
		return findChoice(question, answerText, true);
	}

	/**
	 * Retrieves the AnswerChoice object contained in the alternatives list of
	 * the specified question, that has the specified text as answer text.
	 * 
	 * @param question the specified question
	 * @param answerText the requested answer text
	 * @param caseSensitive decides whether to search case sensitive or not
	 * @return null, if no answer found for specified params
	 */
	public static Choice findChoice(QuestionChoice question, String answerText, boolean caseSensitive) {
		if (question == null
				|| question.getAllAlternatives() == null
				|| answerText == null) {
			return null;
		}
		for (Choice choice : question.getAllAlternatives()) {
			if (answerText.equals(choice.getName())
					|| (!caseSensitive && answerText.equalsIgnoreCase(choice.getName()))) {
				return choice;
			}
		}
		return null;
	}

	public static MultipleChoiceValue findMultipleChoiceValue(QuestionMC question, List<String> valueNames) throws IllegalArgumentException {
		List<Choice> choices = new ArrayList<Choice>(valueNames.size());
		for (String name : valueNames) {
			Choice choice = findChoice(question, name);
			if (choice != null) {
				choices.add(choice);
			}
			else {
				throw new IllegalArgumentException("Unknown choice name: " + name);
			}
		}
		return MultipleChoiceValue.fromChoices(choices);
	}

	/**
	 * Creates a question value from a case sensitive string representation of
	 * the value. If the specified string does not represent a valid value of
	 * that question, null is returned. This may happen e.g. if the denoted
	 * choice is not available or for a numeric question if the string is not a
	 * valid double representation.
	 * <p>
	 * The method created the undefined value for "Ma_Undefined" or "UNDEFINED"
	 * (if there is no such choice). The method created the unknown for "MaU",
	 * "-?-" or "UNKNOWN" (if there is no such choice).
	 * 
	 * @created 23.09.2013
	 * @param question the question to create the value for
	 * @param valueString the string representation of the value
	 * @return the created value
	 */
	public static QuestionValue findValue(Question question, String valueString) {
		return findValue(question, valueString, true);
	}

	/**
	 * Creates a question value from a string representation of the value. You
	 * may specify if the values are matched case sensitive or not. If the
	 * specified string does not represent a valid value of that question, null
	 * is returned. This may happen e.g. if the denoted choice is not available
	 * or for a numeric question if the string is not a valid double
	 * representation.
	 * <p>
	 * The method created the undefined value for "Ma_Undefined" or "UNDEFINED"
	 * (if there is no such choice). The method created the unknown for "MaU",
	 * "-?-" or "UNKNOWN" (if there is no such choice).
	 * 
	 * @created 23.09.2013
	 * @param question the question to create the value for
	 * @param valueString the string representation of the value
	 * @return the created value
	 */
	public static QuestionValue findValue(Question question, String valueString, boolean caseSensitive) {
		if (question == null || valueString == null) {
			throw new NullPointerException("Question and value String must not be null.");
		}

		// multiple choice question given
		if (question instanceof QuestionMC) {
			List<Choice> values = new LinkedList<Choice>();

			// if mc val is a "real" mc val, i.e. more than one answervals
			if (ChoiceID.isEncodedChoiceIDs(valueString)) {
				ChoiceID[] mcvals = ChoiceID.decodeChoiceIDs(valueString);
				for (ChoiceID val : mcvals) {
					Choice choice = findChoice((QuestionChoice) question, val.getText(),
							caseSensitive);
					if (choice == null) {
						return findSpecialValue(valueString, caseSensitive);
					}
					else {
						values.add(choice);
					}
				}
				return MultipleChoiceValue.fromChoices(values);
			}

			// else, if a single answer val should be set for a mc question
			else {
				Choice choice = findChoice((QuestionChoice) question, valueString, caseSensitive);
				if (choice == null) {
					return findSpecialValue(valueString, caseSensitive);
				}
				else {
					values.add(choice);
					return MultipleChoiceValue.fromChoices(values);
				}
			}
		}

		// choice question given (e.g., question yn, questionoc)
		else if (question instanceof QuestionChoice) {
			Choice choice = findChoice((QuestionChoice) question, valueString, caseSensitive);
			if (choice == null) {
				return findSpecialValue(valueString, caseSensitive);
			}
			else {
				return new ChoiceValue(choice);
			}
		}

		// num questions
		else if (question instanceof QuestionNum) {
			try {
				return new NumValue(Double.parseDouble(valueString));
			}
			catch (NumberFormatException e) {
				return findSpecialValue(valueString, caseSensitive);
			}
		}

		// text questions
		else if (question instanceof QuestionText) {
			QuestionValue specialValue = findSpecialValue(valueString, caseSensitive);
			if (specialValue != null) return specialValue;
			return new TextValue(valueString);
		}

		// date questions
		else if (question instanceof QuestionDate) {
			try {
				return DateValue.createDateValue(valueString);
			}
			catch (IllegalArgumentException e) {
				return findSpecialValue(valueString, caseSensitive);
			}
		}
		else {
			return UndefinedValue.getInstance();
		}
	}

	private static QuestionValue findSpecialValue(String valueString, boolean caseSensitive) {
		if (caseSensitive) {
			if (valueString.equals(UndefinedValue.UNDEFINED_ID) || valueString.equals("UNDEFINED")) {
				return UndefinedValue.getInstance();
			}
			if (valueString.equals(Unknown.UNKNOWN_ID) || valueString.equals("-?-")
					|| valueString.equals("UNKNOWN")) {
				return Unknown.getInstance();
			}
		}
		else {
			if (valueString.equalsIgnoreCase(UndefinedValue.UNDEFINED_ID)
					|| valueString.equalsIgnoreCase("UNDEFINED")) {
				return UndefinedValue.getInstance();
			}
			if (valueString.equalsIgnoreCase(Unknown.UNKNOWN_ID) || valueString.equals("-?-")
					|| valueString.equalsIgnoreCase("UNKNOWN")) {
				return Unknown.getInstance();
			}
		}
		return null;
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
		reindex(knowledgeBase.getRootQASet(), qcontainerIndex);
		Collections.sort(unsorted, new DFSTreeSortingComparator(qcontainerIndex));
	}

	/**
	 * Traverses the QASet hierarchy using a depth-first search and attaches an
	 * ordering number to each visited {@link QASet}.
	 */
	private static void reindex(TerminologyObject qaset, Map<TerminologyObject, Integer> qcontainerIndex) {
		qcontainerIndex.put(qaset, qcontainerIndex.size());

		for (TerminologyObject child : qaset.getChildren()) {
			if (!qcontainerIndex.containsKey(child)) {
				reindex(child, qcontainerIndex);
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
	 * Extract all {@link Locale}s from a {@link KnowledgeBase} (and its
	 * containing {@link NamedObject}s). It will return every {@link Locale}
	 * that is used for at least one property within the knowledge base.
	 * <p>
	 * Implementation note: <br>
	 * Because of searching every property within the whole knowledge base it is
	 * a good idea to store and reused the result of this operation instead of
	 * calling this method multiple times on the same knowledge base.
	 * 
	 * @created 15.12.2010
	 * @param kb the knowledge base to be examined
	 * @return the available locales
	 */
	public static Set<Locale> getAvailableLocales(KnowledgeBase kb) {
		Set<Locale> locales = new HashSet<Locale>();
		// get all locales from knowledge base
		getAvailableLocales(kb, locales);
		for (NamedObject object : kb.getManager().getAllTerminologyObjects()) {
			// get all locales from every NamedObject within the knowledge base
			getAvailableLocales(object, locales);
			if (object instanceof QuestionChoice) {
				for (Choice c : ((QuestionChoice) object).getAllAlternatives()) {
					getAvailableLocales(c, locales);
				}
			}
		}
		return locales;
	}

	private static void getAvailableLocales(NamedObject object, Set<Locale> locales) {
		InfoStore store = object.getInfoStore();
		for (Triple<Property<?>, Locale, Object> entry : store.entries()) {
			Locale locale = entry.getB();
			if (locale != null) {
				locales.add(locale);
			}
		}
	}
}

/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.core.knowledge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.terminology.AbstractTerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.Solution;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 12.01.2011
 */
public class TerminologyManager {

	/**
	 * Hashes the objects for names (unique name assumption required)
	 */
	private final Map<String, TerminologyObject> objectNameMap = new HashMap<String, TerminologyObject>();

	private final KnowledgeBase kb;

	/**
	 * @param knowledgeBase
	 */
	public TerminologyManager(KnowledgeBase knowledgeBase) {
		this.kb = knowledgeBase;
	}

	public void putTerminologyObject(TerminologyObject object) {
		if (object.getName() == null) {
			throw new IllegalStateException("TerminologyObject " + object
					+ " has no assigned name.");
		}
		else if (objectNameMap.containsKey(object.getName())) {
			if (objectNameMap.get(object.getName()) != object) {
				throw new IllegalArgumentException(
						"TerminologyObject "
								+ object
								+ " cannot be added, an Object with the same name is already contained in the knowledgebase.");
			}
			else {
				// no need to insert the object twice
				return;
			}
		}
		if (object.getKnowledgeBase() != kb) {
			throw new IllegalArgumentException(
					"TerminologyObject "
							+ object
							+ " cannot be added, it belongs to another knowledgebase.");
		}
		objectNameMap.put(object.getName(), object);
	}

	/**
	 * Deletes a terminology object from the knowledge base. Before the deletion
	 * the corresponding knowledge instances (KnowledgeSlices) are also removed.
	 * Exception thrown: An object cannot be removed, if it has children
	 * relations.
	 * 
	 * @param object the object to be removed
	 * @throws IllegalAccessException if the knowledge could not be removed from
	 *         the {@link KnowledgeBase}
	 */
	public void remove(AbstractTerminologyObject object) throws IllegalAccessException {
		if ((object.getChildren() != null) && (object.getChildren().length > 0)) {
			throw new IllegalAccessException(
					object
							+ " has some children, that should be removed/relinked before deletion.");
		}
		else {
			// removes object from list of children of all parents
			object.setParents(new ArrayList<AbstractTerminologyObject>(0));
			object.removeAllKnowledge();
			objectNameMap.remove(object.getName());
		}
	}

	/**
	 * Returns all {@link Solution} instances stored in this knowledge base.
	 * 
	 * @return list of all {@link Solution} instances contained in this
	 *         {@link KnowledgeBase}
	 */
	public List<Solution> getSolutions() {
		List<Solution> solutions = new ArrayList<Solution>();
		for (NamedObject o : objectNameMap.values()) {
			if (o instanceof Solution) {
				solutions.add((Solution) o);
			}
		}
		return solutions;
	}

	/**
	 * Returns all questionnaires contained in this {@link KnowledgeBase}.
	 * 
	 * @return an unmodifiable {@link List} of all {@link QContainer} instances
	 *         contained in this {@link KnowledgeBase}
	 */
	public List<QContainer> getQContainers() {
		List<QContainer> qcontainers = new ArrayList<QContainer>();
		for (NamedObject o : objectNameMap.values()) {
			if (o instanceof QContainer) {
				qcontainers.add((QContainer) o);
			}
		}
		return qcontainers;
	}

	/**
	 * Returns the (flattened) {@link List} of all {@link Question} instances
	 * represented in this knowledge base. The returned list may be
	 * unmodifiable.
	 * 
	 * @return list of all questions contained in this KnowledgeBase
	 */
	public List<Question> getQuestions() {
		List<Question> questions = new ArrayList<Question>();
		for (NamedObject o : objectNameMap.values()) {
			if (o instanceof Question) {
				questions.add((Question) o);
			}
		}
		return Collections.unmodifiableList(questions);
	}

	/**
	 * Tries to find a {@link Choice} with the specified identifier. Choices are
	 * only contained in {@link QuestionChoice} instances.
	 * 
	 * @param name the unique identifier of the
	 * @return a {@link Choice} instance having the specified unique identifier,
	 *         <code>null</code> if no {@link Choice} was found.
	 */
	public Choice searchAnswerChoice(String name) {
		for (Question q : getQuestions()) {
			if (q instanceof QuestionChoice) {
				QuestionChoice qc = (QuestionChoice) q;
				List<Choice> allAlternatives = qc.getAllAlternatives();
				for (Choice a : allAlternatives) {
					if (a.getName().equals(name)) {
						return a;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Tries to find a {@link Solution} instance with the specified unique name.
	 * 
	 * @return a {@link Solution} instance with the specified unique name;
	 *         <code>null</code> if none found
	 */
	public Solution searchSolution(String id) {
		if (objectNameMap.containsKey(id)) {
			NamedObject o = objectNameMap.get(id);
			if (o instanceof Solution) {
				return (Solution) o;
			}
		}
		return null;
	}

	/**
	 * Tries to find a {@link QASet} instance (questions, questionnaires) with
	 * the specified unique name.
	 * 
	 * @return a {@link QASet} instance with the specified unique name;
	 *         <code>null</code> if none found
	 */
	public QASet searchQASet(String id) {
		if (objectNameMap.containsKey(id)) {
			NamedObject o = objectNameMap.get(id);
			if (o instanceof QASet) {
				return (QASet) o;
			}
		}
		return null;
	}

	/**
	 * Tries to retrieve an terminology object with the specified name, that is
	 * contained in this knowledge base.
	 * 
	 * @param name the specified name
	 * @return the terminology object with the specified identifier;
	 *         <code>null</code> if none found
	 * @author joba
	 * @date 15.04.2010
	 */
	public TerminologyObject search(String name) {
		return objectNameMap.get(name);
	}

	/**
	 * Tries to find a {@link QContainer} instance with the specified unique
	 * name.
	 * 
	 * @return a {@link QContainer} instance with the specified unique name;
	 *         <code>null</code> if none found
	 */
	public QContainer searchQContainers(String name) {
		if (objectNameMap.containsKey(name)) {
			NamedObject o = objectNameMap.get(name);
			if (o instanceof QContainer) {
				return (QContainer) o;
			}
		}
		return null;
	}

	/**
	 * Tries to find a terminology object (solutions, questions, questionnaires)
	 * with the specified name.
	 * 
	 * @param name the specified name of the searched terminology object
	 * @return the search terminology object; <code>null</code> if none found
	 * @author joba
	 * @date 15.04.2010
	 */
	public TerminologyObject searchObjectForName(String name) {
		return this.objectNameMap.get(name);
	}

	/**
	 * Tries to find a {@link Question} instance with the specified unique
	 * identifier.
	 * 
	 * @param name the unique identifier of the search {@link Question}
	 * @return the searched question; <code>null</code> if none found
	 * @author joba
	 * @date 15.04.2010
	 */
	public Question searchQuestion(String name) {
		if (objectNameMap.containsKey(name)) {
			NamedObject o = objectNameMap.get(name);
			if (o instanceof Question) {
				return (Question) o;
			}
		}
		return null;
	}

	/**
	 * Returns all question/questionnaires stored in this {@link KnowledgeBase}
	 * instance.
	 * 
	 * @return all question/questionnaires contained in this knowledge base
	 * @author joba
	 * @date 15.04.2010
	 */
	public List<QASet> getQASets() {
		List<QASet> qASets = new ArrayList<QASet>();
		for (NamedObject o : objectNameMap.values()) {
			if (o instanceof QASet) {
				qASets.add((QASet) o);
			}
		}
		return qASets;
	}

	public List<NamedObject> getAllIDObjects() {
		List<NamedObject> objects = new LinkedList<NamedObject>();
		objects.addAll(getQContainers());
		objects.addAll(getSolutions());
		objects.addAll(getQuestions());
		objects.addAll(catchAnswersFromQuestions(getQuestions()));
		return objects;
	}

	private static List<Choice> catchAnswersFromQuestions(List<Question> questions) {
		List<Choice> ret = new LinkedList<Choice>();

		Iterator<Question> iter = questions.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof QuestionChoice) {
				QuestionChoice qc = (QuestionChoice) o;
				ret.addAll(qc.getAllAlternatives());
			}
		}
		return ret;
	}
}

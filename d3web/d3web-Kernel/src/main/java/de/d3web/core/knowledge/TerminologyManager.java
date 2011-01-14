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
import java.util.logging.Logger;
import java.util.regex.Pattern;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.IDObject;
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
	 * Hashes the objects for ID
	 */
	private final Map<String, TerminologyObject> objectIDMap = new HashMap<String, TerminologyObject>();

	/**
	 * Hashes the objects for names (unique name assumption required)
	 */
	private final Map<String, TerminologyObject> objectNameMap = new HashMap<String, TerminologyObject>();

	private final Map<String, Integer> idCounts = new HashMap<String, Integer>();

	public static final Pattern ID = Pattern.compile("(.*\\D)+(\\d)*");

	public static final Pattern IDPREFIX = Pattern.compile(".*\\D");

	private KnowledgeBase kb;

	public TerminologyManager(KnowledgeBase kb) {
		this.kb = kb;
	}

	public void putTerminologyObject(TerminologyObject object) {
		if (object.getId() == null) {
			throw new IllegalStateException("IDObject " + object
					+ " has no assigned ID.");
		}
		else if (objectIDMap.containsKey(object.getId())) {
			if (objectIDMap.get(object.getId()) != object) {
				throw new IllegalArgumentException(
						"IDObject "
								+ object
								+ " cannot be added, an Object with the same id is already contained in the knowledgebase.");
			}
			else {
				// no need to insert the object twice
				return;
			}
		}
		increaseIDCounter(object);
		objectIDMap.put(object.getId(), object);
		if (objectNameMap.containsKey(object.getName())) {
			Logger.getLogger("KnowledgeBase").warning(
					"Two id objects with the same name are contained in the kb: "
							+ object.getName());
		}
		objectNameMap.put(object.getName(), object);
		if (object.getKnowledgeBase() == null) {
			object.setKnowledgeBase(kb);
		}
	}

	private void increaseIDCounter(TerminologyObject object) {
		// TODO
	}

	public String getIDforPrefix(String prefix) {
		if (prefix.isEmpty() || IDPREFIX.matcher(prefix).matches()) {
			if (idCounts.containsKey(prefix)) {
				idCounts.put(prefix, idCounts.get(prefix) + 1);
			}
			else {
				idCounts.put(prefix, 1);
			}
			return prefix + idCounts.get(prefix);
		}
		else {
			throw new IllegalArgumentException("Prefix must be empty or end with a non digit.");
		}
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
	public void remove(NamedObject object) throws IllegalAccessException {
		if ((object.getChildren() != null) && (object.getChildren().length > 0)) {
			throw new IllegalAccessException(
					object
							+ " has some children, that should be removed/relinked before deletion.");
		}
		else {
			// removes object from list of children of all parents
			object.setParents(new ArrayList<NamedObject>(0));
			object.removeAllKnowledge();
			objectIDMap.remove(object.getId());
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
		for (IDObject o : objectIDMap.values()) {
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
		for (IDObject o : objectIDMap.values()) {
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
		for (IDObject o : objectIDMap.values()) {
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
	 * @param choiceID the unique identifier of the
	 * @return a {@link Choice} instance having the specified unique identifier,
	 *         <code>null</code> if no {@link Choice} was found.
	 */
	public Choice searchAnswerChoice(String choiceID) {
		for (Question q : getQuestions()) {
			if (q instanceof QuestionChoice) {
				QuestionChoice qc = (QuestionChoice) q;
				List<Choice> allAlternatives = qc.getAllAlternatives();
				for (Choice a : allAlternatives) {
					if (a.getId().equals(choiceID)) {
						return a;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Tries to find a {@link Solution} instance with the specified unique
	 * identifier.
	 * 
	 * @return a {@link Solution} instance with the specified unique identifier;
	 *         <code>null</code> if none found
	 */
	public Solution searchSolution(String id) {
		if (objectIDMap.containsKey(id)) {
			IDObject o = objectIDMap.get(id);
			if (o instanceof Solution) {
				return (Solution) o;
			}
		}
		return null;
	}

	/**
	 * Tries to find a {@link QASet} instance (questions, questionnaires) with
	 * the specified unique identifier.
	 * 
	 * @return a {@link QASet} instance with the specified unique identifier;
	 *         <code>null</code> if none found
	 */
	public QASet searchQASet(String id) {
		if (objectIDMap.containsKey(id)) {
			IDObject o = objectIDMap.get(id);
			if (o instanceof QASet) {
				return (QASet) o;
			}
		}
		return null;
	}

	/**
	 * Tries to retrieve an terminology object with the specified identifier,
	 * that is contained in this knowledge base.
	 * 
	 * @param id the specified identifier
	 * @return the terminology object with the specified identifier;
	 *         <code>null</code> if none found
	 * @author joba
	 * @date 15.04.2010
	 */
	public TerminologyObject search(String id) {
		TerminologyObject o = searchQuestion(id);
		if (o != null) {
			return o;
		}
		o = searchQContainers(id);
		if (o != null) {
			return o;
		}
		o = searchSolution(id);
		if (o != null) {
			return o;
		}
		return null;
	}

	/**
	 * Tries to find a {@link QContainer} instance with the specified unique
	 * identifier or name.
	 * 
	 * @return a {@link QContainer} instance with the specified unique
	 *         identifier or name; <code>null</code> if none found
	 */
	public QContainer searchQContainers(String id) {
		if (objectIDMap.containsKey(id)) {
			IDObject o = objectIDMap.get(id);
			if (o instanceof QContainer) {
				return (QContainer) o;
			}
		}
		else {
			for (QContainer qcontainer : getQContainers()) {
				String name = qcontainer.getName();
				if (name != null && name.equals(id)) {
					return qcontainer;
				}
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
	 * @param id the unique identifier of the search {@link Question}
	 * @return the searched question; <code>null</code> if none found
	 * @author joba
	 * @date 15.04.2010
	 */
	public Question searchQuestion(String id) {
		if (objectIDMap.containsKey(id)) {
			IDObject o = objectIDMap.get(id);
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
		for (IDObject o : objectIDMap.values()) {
			if (o instanceof QASet) {
				qASets.add((QASet) o);
			}
		}
		return qASets;
	}

	public List<IDObject> getAllIDObjects() {
		List<IDObject> objects = new LinkedList<IDObject>();
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

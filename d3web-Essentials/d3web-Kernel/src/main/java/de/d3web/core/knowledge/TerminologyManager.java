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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;

/**
 * The {@link TerminologyManager} provides convinient methods to access or
 * search for elements of a {@link KnowledgeBase} instance.
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 12.01.2011
 */
public class TerminologyManager {

	/**
	 * Hashes the objects for names (unique name assumption required)
	 */
	private final Map<String, TerminologyObject> objectNameMap = new HashMap<>();
	private final TreeIndexer solutionIndexer = new TreeIndexer();
	private final TreeIndexer questionIndexer = new TreeIndexer();
	private final KnowledgeBase kb;

	/**
	 * Creates a new manager for the specified {@link KnowledgeBase} instance.
	 *
	 * @param knowledgeBase the specified {@link KnowledgeBase} instance
	 */
	public TerminologyManager(KnowledgeBase knowledgeBase) {
		this.kb = knowledgeBase;
	}

	/**
	 * Inserts the specified {@link TerminologyObject} instance into the
	 * {@link KnowledgeBase}.
	 *
	 * @param object the specified {@link TerminologyObject} instance
	 * @created 06.05.2011
	 */
	public void putTerminologyObject(TerminologyObject object) {
		if (object.getName() == null) {
			throw new IllegalStateException("TerminologyObject " + object
					+ " has no assigned name.");
		}
		else if (objectNameMap.containsKey(object.getName())) {
			if (objectNameMap.get(object.getName()) == object) {
				// no need to insert the object twice
				// but the order may have changed
				clearIndexer(object);
				return;
			}
			else {
				throw new IllegalArgumentException(
						"TerminologyObject "
								+ object
								+ " cannot be added, an Object with the same name is already contained in the knowledge base.");
			}
		}
		if (object.getKnowledgeBase() != kb) {
			throw new IllegalArgumentException(
					"TerminologyObject "
							+ object
							+ " cannot be added, it belongs to another knowledge base.");
		}
		// rebuild indexes for that object type
		clearIndexer(object);
		objectNameMap.put(object.getName(), object);
	}

	/**
	 * Deletes a terminology object from the knowledge base. Exception thrown:
	 * An object cannot be removed, if it has children or parent relations.
	 * <p>
	 * Do not call this method directly, use
	 * TerminologyObject.removeFromKnowledgeBase().
	 *
	 * @param object the object to be removed
	 */
	public void remove(TerminologyObject object) {
		if ((object.getChildren() != null) && (object.getChildren().length > 0)
				|| (object.getParents() != null) && (object.getParents().length > 0)) {
			throw new IllegalArgumentException(
					object
							+ " has some children or parents, that should be removed/relinked before deletion.");
		}
		else {
			clearIndexer(object);
			objectNameMap.remove(object.getName());
		}
	}

	/**
	 * Returns all {@link Solution} instances stored in this knowledge base.
	 *
	 * @return list of all {@link Solution} instances contained in this
	 * {@link KnowledgeBase}
	 */
	public List<Solution> getSolutions() {
		return getObjects(Solution.class);
	}

	/**
	 * Returns all questionnaires contained in this {@link KnowledgeBase}.
	 *
	 * @return an unmodifiable {@link List} of all {@link QContainer} instances
	 * contained in this {@link KnowledgeBase}
	 */
	public List<QContainer> getQContainers() {
		return getObjects(QContainer.class);
	}

	/**
	 * Returns the (flattened) {@link List} of all {@link Question} instances
	 * represented in this knowledge base. The returned list may be
	 * unmodifiable.
	 *
	 * @return list of all questions contained in this KnowledgeBase
	 */
	public List<Question> getQuestions() {
		return getObjects(Question.class);
	}

	/**
	 * Returns the (flattened) {@link List} of all {@link TerminologyObject}
	 * instances represented in this knowledge base being an instance of the
	 * specified class. The returned list is unmodifiable.
	 *
	 * @return list of all TerminologyObjects of a certain type contained in
	 * this KnowledgeBase
	 */
	public <T extends TerminologyObject> List<T> getObjects(Class<T> clazz) {
		List<T> questions = new ArrayList<>();
		for (NamedObject o : objectNameMap.values()) {
			if (clazz.isInstance(o)) {
				questions.add(clazz.cast(o));
			}
		}
		return Collections.unmodifiableList(questions);
	}

	/**
	 * Tries to find a {@link Solution} instance with the specified unique name.
	 *
	 * @return a {@link Solution} instance with the specified unique name;
	 * <code>null</code> if none found
	 */
	public Solution searchSolution(String id) {
		NamedObject o = objectNameMap.get(id);
		if (o instanceof Solution) {
			return (Solution) o;
		}
		return null;
	}

	/**
	 * Tries to find a {@link QASet} instance (questions, questionnaires) with
	 * the specified unique name.
	 *
	 * @return a {@link QASet} instance with the specified unique name;
	 * <code>null</code> if none found
	 */
	public QASet searchQASet(String id) {
		NamedObject o = objectNameMap.get(id);
		if (o instanceof QASet) {
			return (QASet) o;
		}
		return null;
	}

	/**
	 * Tries to retrieve an terminology object with the specified name, that is
	 * contained in this knowledge base.
	 *
	 * @param name the specified name
	 * @return the terminology object with the specified identifier;
	 * <code>null</code> if none found
	 */
	public TerminologyObject search(String name) {
		return objectNameMap.get(name);
	}

	/**
	 * Tries to retrieve an terminology object with the specified name and
	 * class, that is contained in this knowledge base. If no object found with
	 * these criteria, null is returned.
	 *
	 * @param name    the specified name
	 * @param toClass the class the returned {@link TerminologyObject} needs to
	 *                have
	 * @return the terminology object with the specified identifier;
	 * <code>null</code> if none found
	 */
	public <T> T search(String name, Class<T> toClass) {
		TerminologyObject terminologyObject = objectNameMap.get(name);
		if (toClass.isInstance(terminologyObject)) {
			return toClass.cast(terminologyObject);
		}
		return null;
	}

	/**
	 * Tries to find a {@link QContainer} instance with the specified unique
	 * name.
	 *
	 * @return a {@link QContainer} instance with the specified unique name;
	 * <code>null</code> if none found
	 */
	public QContainer searchQContainer(String name) {
		NamedObject o = objectNameMap.get(name);
		if (o instanceof QContainer) {
			return (QContainer) o;
		}
		return null;
	}

	/**
	 * Tries to find a terminology object (solutions, questions, questionnaires)
	 * with the specified name.
	 *
	 * @param name the specified name of the searched terminology object
	 * @return the search terminology object; <code>null</code> if none found
	 * @deprecated use {@link #search(String)} instead
	 */
	@Deprecated
	public TerminologyObject searchObjectForName(String name) {
		return this.objectNameMap.get(name);
	}

	/**
	 * Tries to find a {@link Question} instance with the specified unique
	 * identifier.
	 *
	 * @param name the unique identifier of the search {@link Question}
	 * @return the searched question; <code>null</code> if none found
	 */
	public Question searchQuestion(String name) {
		NamedObject o = objectNameMap.get(name);
		if (o instanceof Question) {
			return (Question) o;
		}
		return null;
	}

	/**
	 * Returns all question/questionnaires stored in this {@link KnowledgeBase}
	 * instance.
	 *
	 * @return all question/questionnaires contained in this knowledge base
	 */
	public List<QASet> getQASets() {
		List<QASet> qASets = new ArrayList<>();
		for (NamedObject o : objectNameMap.values()) {
			if (o instanceof QASet) {
				qASets.add((QASet) o);
			}
		}
		return qASets;
	}

	/**
	 * Returns all {@link TerminologyObject} instances contained in the
	 * corresponding {@link KnowledgeBase}.
	 *
	 * @return all {@link TerminologyObject} instances contained in the
	 * corresponding {@link KnowledgeBase}
	 * @created 06.05.2011
	 */
	public Collection<TerminologyObject> getAllTerminologyObjects() {
		return Collections.unmodifiableCollection(objectNameMap.values());
	}

	/**
	 * Returns the index of the specific object in the knowledge base's object
	 * tree. The index is counted in depth-first-search order. If an object is
	 * connected to the tree at multiple places, the lowest index is used. The
	 * root objects ({@link KnowledgeBase#getRootQASet()} and
	 * {@link KnowledgeBase#getRootSolution()}) of the knowledge base starts
	 * with index 0.
	 * <p>
	 * If a object or its predecessors aren't connected to the knowledge base's
	 * root objects, the root of these dangling trees get a number higher than
	 * all objects in these trees. Within such a tree the depth-first-search
	 * order is still preserved (as long as the objects aren't also added to
	 * other dangling trees or the root tree).
	 * <p>
	 * For performance reasons and due to building the indexes on demand and
	 * re-index if the terminology of the knowledge base changes, you should
	 * avoid to call this method during building the knowledge base.
	 *
	 * @param object the object to get the index for
	 * @return the index of the object in its particular tree
	 * @created 17.03.2013
	 */
	public int getTreeIndex(TerminologyObject object) {
		return getIndexer(object).getIndex(object);
	}

	private void clearIndexer(TerminologyObject object) {
		getIndexer(object).clear();
	}

	private TreeIndexer getIndexer(TerminologyObject object) {
		return (object instanceof Solution) ? solutionIndexer : questionIndexer;
	}

}

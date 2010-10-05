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

package de.d3web.core.knowledge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSConfig;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.IDObject;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.DCMarkedUp;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.knowledge.terminology.info.Properties;
import de.d3web.core.knowledge.terminology.info.PropertiesContainer;
import de.d3web.core.manage.KnowledgeBaseManagement;

/**
 * The KnowledgeBase stores all terminology objects (Question, Solution, etc.)
 * and provides interfaces to access the problem-solving/dialog knowledge of the
 * domain.
 * 
 * New terminology objects should be added here and should be created using the
 * KnowlegdeBaseManagement factory. For the creation of problem-solving/dialog
 * knowledge, you should use the factories provided with the particular
 * KnowledgeSlices (e.g., Rule, XCLModel).
 * 
 * @author joba
 * @author Christian Betz
 * @see Solution
 * @see Question
 * @see RuleComplex
 * @see QASet
 */
public class KnowledgeBase implements KnowledgeContainer, DCMarkedUp,
		PropertiesContainer {

	private Properties properties;

	private DCMarkup dcMarkup;

	private String kbID;

	private final Map<String, String> costVerbalization;

	private final Map<String, String> costUnit;

	private List<? extends QASet> initQuestions = new LinkedList<QASet>();

	private final List<Solution> solutions;

	private final List<Resource> resouces = new ArrayList<Resource>();

	private final List<PSConfig> psConfigs = new ArrayList<PSConfig>();

	private QASet rootQASet = null;

	private Solution rootSolution = null;

	/**
	 * Hashes the objects for ID
	 */
	private final Map<String, IDObject> objectIDMap = new HashMap<String, IDObject>();

	/**
	 * Hashes the objects for names (unique name assumption required)
	 */
	private final Map<String, TerminologyObject> objectNameMap = new HashMap<String, TerminologyObject>();

	/**
	 * Map with key="ps-method type" value="list of e.g. rules provided by this
	 * type"
	 */
	private final Map<Class<? extends PSMethod>, Map<MethodKind, List<KnowledgeSlice>>> knowledgeMap;

	/**
	 * @return the unique identifier of this KnowledgeBase instance.
	 */
	@Override
	public String getId() {
		return kbID;
	}

	/**
	 * Sets a unique identifier for this KnowledgeBase instance.
	 * 
	 * @param id a unique identifier
	 * @author joba
	 * @date 15.04.2010
	 */
	public void setId(String id) {
		kbID = id;
	}

	/**
	 * An access method to retrieve the particular {@link KnowledgeSlice}
	 * instance for a given problem-solver and the access key {@link MethodKind}
	 * 
	 * @return usually a List of knowledge slices relating to this NamedObject,
	 *         the specified problem-solver class and it's kind.
	 * @param problemsolver the specified problem-solver
	 * @param kind the access key for the type of knowledge to be retrieved
	 */
	@Override
	public Object getKnowledge(Class<? extends PSMethod> problemsolver, MethodKind kind) {
		Map<MethodKind, List<KnowledgeSlice>> o = knowledgeMap.get(problemsolver);
		if (o != null) {
			return o.get(kind);
		}
		else {
			return null;
		}
	}

	/**
	 * Creates a new knowledge base instance. For the general creation of a
	 * knowledge base and its corresponding objects we recommend to use the
	 * {@link KnowledgeBaseManagement} class.
	 */
	public KnowledgeBase() {
		solutions = new ArrayList<Solution>();
		initQuestions = new ArrayList<QASet>();
		costVerbalization = new TreeMap<String, String>();
		costUnit = new TreeMap<String, String>();
		properties = new Properties();
		dcMarkup = new DCMarkup();

		// unsynchronized version, allows null values
		knowledgeMap = new HashMap<Class<? extends PSMethod>, Map<MethodKind, List<KnowledgeSlice>>>();

	}

	/**
	 * Adds a new solution to the knowledge base. The new object is only added,
	 * if it is not already contained in the knowledge base.
	 * 
	 * @param solution the new solution to be added to the knowledge base
	 */
	public void add(Solution solution) {
		checkID(solution);

		if (!objectIDMap.containsKey(solution.getId())) {
			objectIDMap.put(solution.getId(), solution);
			objectNameMap.put(solution.getName(), solution);

			solutions.add(solution);
			if (solution.getKnowledgeBase() == null) {
				solution.setKnowledgeBase(this);
			}

		}
	}

	private void checkID(IDObject ido) {
		if (ido.getId() == null) {
			throw new IllegalStateException("IDObject " + ido
					+ " has no assigned ID.");
		}

	}

	/**
	 * Inserts a new questionnaire (QContainer) to the knowledge base. The new
	 * object is only inserted, if it is not already contained in the knowledge
	 * base.
	 * 
	 * @param questionnaire the new questionnaire to be added
	 */
	public void add(QContainer questionnaire) {
		addQASet(questionnaire);
	}

	/**
	 * Inserts a new {@link Question} instance to the knowledge base. The new
	 * object is only inserted, if it is not already contained in the knowledge
	 * base.
	 * 
	 * @param question the new question to be added
	 */
	public void add(Question question) {
		addQASet(question);
	}

	/**
	 * Removes the specified {@link KnowledgeSlice} instance from the knowledge
	 * base. <BR>
	 * How it is done: <BR>
	 * <OL>
	 * <LI>Internal linking/indexes of the {@link KnowledgeSlice} are removed.
	 * <LI>Instance is set to <code>null</code> (garbage collector removes
	 * {@link KnowledgeSlice} instance from memory).
	 * </OL>
	 * 
	 * @param slice the {@link KnowledgeSlice} instance to be removed from the
	 *        {@link KnowledgeBase}
	 * @return true, if the knowledge slice was contained in knowledge base and
	 *         could be successfully removed
	 */
	public boolean remove(KnowledgeSlice slice) {
		boolean removed = removeKnowledge(slice.getProblemsolverContext(),
				slice);
		slice.remove();
		return removed;
	}

	/**
	 * Inserts a {@link KnowledgeSlice} instance to this {@link KnowledgeBase}
	 * instance. The knowledge is indexed in the knowledge base according to its
	 * corresponding problem-solver and the access key within the
	 * problem-solver.
	 * 
	 * @param problemsolver the problem-solver, that uses the added
	 *        {@link KnowledgeSlice} instance
	 * @param knowledgeSlice the {@link KnowledgeSlice} instance to be added to
	 *        this {@link KnowledgeBase}
	 * @param knowledgeContext the access key for the indexing with the
	 *        problem-solver
	 */
	@Override
	public final synchronized void addKnowledge(Class<? extends PSMethod> problemsolver,
			KnowledgeSlice knowledgeSlice, MethodKind knowledgeContext) {
		/* make sure, that a storage for the problemsolver is available */
		if (knowledgeMap.get(problemsolver) == null) {
			// usually only 2 kinds (FORWARD and BACKWARD) of knowledge is
			// needed
			knowledgeMap.put(problemsolver,
					new HashMap<MethodKind, List<KnowledgeSlice>>());
		}
		Map<MethodKind, List<KnowledgeSlice>> storage = knowledgeMap
				.get(problemsolver);

		/* make sure, that a storage for the kind of knowledge is available */
		if (storage.get(knowledgeContext) == null) {
			List<KnowledgeSlice> knowledgeSlices = new LinkedList<KnowledgeSlice>();
			storage.put(knowledgeContext, knowledgeSlices);
		}

		/* all right: now put that slice of knowledge in its slot */
		storage.get(knowledgeContext).add(knowledgeSlice);
	}

	/**
	 * Deletes a specified {@link KnowledgeSlice} instance from the
	 * {@link KnowledgeBase} independently from the {@link MethodKind} access
	 * key.
	 * 
	 * @param problemsolver the problem-solver, that uses the added
	 *        {@link KnowledgeSlice} instance
	 * @param knowledgeSlice the {@link KnowledgeSlice} instance to be deleted
	 *        from this {@link KnowledgeBase}
	 * @return true, if the {@link KnowledgeSlice} instance was removed
	 *         successfully
	 * @author joba
	 * @date 15.04.2010
	 */
	public boolean removeKnowledge(Class<? extends PSMethod> problemsolver,
			KnowledgeSlice knowledgeSlice) {
		boolean result = false;
		Map<MethodKind, List<KnowledgeSlice>> knowledge = knowledgeMap
				.get(problemsolver);
		if (knowledge != null) {
			for (MethodKind context : knowledge.keySet()) {
				result |= removeKnowledge(problemsolver, knowledgeSlice,
						context);
			}
		}
		return result;
	}

	/**
	 * Deletes a specified {@link KnowledgeSlice} instance from the
	 * {@link KnowledgeBase}. The {@link KnowledgeSlice} is assumed to be
	 * indexed by the specified problem-solver and access key.
	 * 
	 * @param problemsolver the problem-solver, that uses the added
	 *        {@link KnowledgeSlice} instance
	 * @param knowledgeSlice the {@link KnowledgeSlice} instance to be deleted
	 *        from this {@link KnowledgeBase}
	 * @param accessKey the access key specifying how the {@link KnowledgeSlice}
	 *        instance is indexed
	 * @return true, if the {@link KnowledgeSlice} instance was removed
	 *         successfully
	 * @author joba
	 * @date 15.04.2010
	 */
	public boolean removeKnowledge(Class<? extends PSMethod> problemsolver,
			KnowledgeSlice knowledgeSlice, MethodKind accessKey) {
		Map<MethodKind, List<KnowledgeSlice>> knowledge = knowledgeMap
				.get(problemsolver);
		if (knowledge != null) {
			List<KnowledgeSlice> slices = knowledge.get(accessKey);
			if (slices != null) {
				while (slices.remove(knowledgeSlice)) { // NOSONAR
					// remove all occurring slices
					// this is ok, because work is done in the condition
				}
				return true;
			}
		}
		return false;
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

			// remove object from list of contained objects
			if (object instanceof Solution) {
				solutions.remove(object);
			}
		}
	}

	// /**
	// * Are query and item case insensitive equal or is item a substring of
	// * query?
	// *
	// */
	// public static boolean fuzzyEqual(String query, String item) {
	// return item.toLowerCase().indexOf(query.toLowerCase()) != -1;
	// }

	/**
	 * Returns all problem-solvers, for which {@link KnowledgeSlice} instances
	 * are stores in the {@link KnowledgeBase}.
	 * 
	 * @return a {@link Collection} of all problem-solvers storing knowledge in
	 *         this {@link KnowledgeBase}
	 */
	public Collection<Class<? extends PSMethod>> getAllKnownProblemSolver() {
		return knowledgeMap.keySet();
	}

	/**
	 * Collects and returns all {@link KnowledgeSlice} instances that are stored
	 * in this {@link KnowledgeBase}.
	 * 
	 * @return a {@link Collection} of all {@link KnowledgeSlice} instances
	 *         contained in the {@link KnowledgeBase}
	 */
	public Collection<KnowledgeSlice> getAllKnowledgeSlices() {
		Set<KnowledgeSlice> allKnowledgeSlices = new HashSet<KnowledgeSlice>();
		Iterator<Class<? extends PSMethod>> psmIter = knowledgeMap.keySet().iterator();
		while (psmIter.hasNext()) {
			allKnowledgeSlices.addAll(getAllKnowledgeSlicesFor(psmIter.next()));
		}
		return allKnowledgeSlices;
	}

	/**
	 * Collects and returns all {@link KnowledgeSlice} instances that are stored
	 * in this {@link KnowledgeBase} for a specified problem-solver.
	 * 
	 * @return Collection containing objects of type {@link KnowledgeSlice} for
	 *         the specified problem-solver
	 */
	public Collection<KnowledgeSlice> getAllKnowledgeSlicesFor(
			Class<? extends PSMethod> problemSolverContext) {
		Set<KnowledgeSlice> slices = new HashSet<KnowledgeSlice>();
		Map<MethodKind, List<KnowledgeSlice>> knowledge = knowledgeMap
				.get(problemSolverContext);
		if (knowledge != null) {
			Iterator<MethodKind> kindIter = knowledge.keySet().iterator();
			while (kindIter.hasNext()) {
				slices.addAll(knowledge.get(kindIter.next()));
			}
		}
		return slices;
	}

	/**
	 * Collects and returns all {@link KnowledgeSlice} instances that are stored
	 * in this {@link KnowledgeBase} for a specified problem-solver and
	 * specified access key.
	 * 
	 * @return Collection containing objects of type {@link KnowledgeSlice} for
	 *         the specified problem-solver and access key
	 * 
	 * @param problemsolver the specified problem-solver
	 * @param accesskey the specified access key how the knowledge is stored in
	 *        the problem-solver
	 * @return a {@link Collection} of {@link KnowledgeSlice} instances
	 *         containing all knowledge for the specified problem-solver and
	 *         access key
	 */
	public Collection<KnowledgeSlice> getAllKnowledgeSlicesFor(Class<? extends PSMethod> problemsolver, MethodKind accesskey) {
		Map<MethodKind, List<KnowledgeSlice>> knowledge = knowledgeMap
				.get(problemsolver);
		if (knowledge != null) {
			return knowledge.get(accesskey);
		}
		return new ArrayList<KnowledgeSlice>();
	}

	/**
	 * Returns all {@link Solution} instances stored in this knowledge base. The
	 * returned list is unmodifiable.
	 * 
	 * @return list of all {@link Solution} instances contained in this
	 *         {@link KnowledgeBase}
	 */
	public List<Solution> getSolutions() {
		return Collections.unmodifiableList(solutions);
	}

	/**
	 * Returns the ordered {@link List} of all initial questions (
	 * {@link Question})/questionnaires ({@link QContainer}) . These
	 * questions/questionnaires are prompted first when starting a new dialog.
	 * 
	 * @return a list of the initial questions/questionnaires
	 */
	public List<? extends QASet> getInitQuestions() {
		return initQuestions;
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
	 * The solutions contained in this {@link KnowledgeBase} are organized in a
	 * hierarchy. This method returns the root solution.
	 * 
	 * @return the root solution of this {@link KnowledgeBase}
	 * @author joba
	 * @date 15.04.2010
	 */
	public Solution getRootSolution() {
		return rootSolution;
	}

	/**
	 * The questionnaires and contained questions are organized in a hierarchy.
	 * This method returns the root object (usually a {@link QContainer}).
	 * 
	 * @return the root {@link QASet} instance of this {@link KnowledgeBase}
	 */
	public QASet getRootQASet() {
		return rootQASet;
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

		// deprecated: should be removed when objectMap hashing is stable
		QASet ret = searchQuestion(id);
		if (ret == null) {
			ret = searchQContainers(id);
		}
		return ret;

	}

	/**
	 * Tries to find a {@link QContainer} instance with the specified unique
	 * identifier.
	 * 
	 * @return a {@link QContainer} instance with the specified unique
	 *         identifier; <code>null</code> if none found
	 */
	public QContainer searchQContainers(String id) {
		if (objectIDMap.containsKey(id)) {
			IDObject o = objectIDMap.get(id);
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
	 * Defines the ordered list of initial questions/questionnaires. This
	 * {@link List} of {@link QASet}s is prompted at the beginning of every new
	 * problem-solving session.
	 * 
	 * @param initQuestions the collection of init questions/questionnaires
	 * @author joba
	 * @date 15.04.2010
	 */
	public void setInitQuestions(List<? extends QASet> initQuestions) {
		this.initQuestions = initQuestions;
	}

	/**
	 * Returns a compact {@link String} representation of this
	 * {@link KnowledgeBase} instance (usually only the ID).
	 * 
	 * @return a compact {@link String} representation of this instance
	 */
	@Override
	public String toString() {
		return "KnowledgeBase ID: " + kbID;
	}

	/**
	 * Returning the meta-description of this {@link KnowledgeBase} instance.
	 * 
	 * @return the meta-description of this {@link KnowledgeBase} instance.
	 */
	@Override
	public DCMarkup getDCMarkup() {
		return dcMarkup;
	}

	/**
	 * Sets the meta-description of this {@link KnowledgeBase} instance.
	 * 
	 * @param dcMarkup the meta-description of this {@link KnowledgeBase}
	 *        instance.
	 */
	@Override
	public void setDCMarkup(DCMarkup dcMarkup) {
		this.dcMarkup = dcMarkup;
	}

	/**
	 * Returns properties defined for this {@link KnowledgeBase} instance.
	 * 
	 * @return additional properties defined for this knowledge base
	 */
	@Override
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Sets the properties defined for this {@link KnowledgeBase} instance.
	 * 
	 * @param properties the properties of this knowledge base
	 */
	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * Inserts a new resource for this {@link KnowledgeBase} instance. For
	 * example, a resource is a multi-media file attached to the
	 * {@link KnowledgeBase}.
	 * 
	 * @param resource a new resource
	 * @author joba
	 * @date 15.04.2010
	 */
	public void addResouce(Resource resource) {
		this.resouces.add(resource);
	}

	/**
	 * Returns all resources stored in this {@link KnowledgeBase} instance. For
	 * example, a multi-media is a resource.
	 * 
	 * @return all resources of this knowledge base
	 * @author joba
	 * @date 15.04.2010
	 */
	public List<Resource> getResources() {
		return Collections.unmodifiableList(this.resouces);
	}

	/**
	 * Returns a stored resource for a specified pathname accessor.
	 * 
	 * @param pathname the specified pathname accessor.
	 * @return a resource stored by the specified accessor
	 * @author joba
	 * @date 15.04.2010
	 */
	public Resource getResource(String pathname) {
		for (Resource resource : resouces) {
			if (pathname.equalsIgnoreCase(resource.getPathName())) {
				return resource;
			}
		}
		return null;
	}

	/**
	 * Inserts a new question/questionnaire to this {@link KnowledgeBase}
	 * instance
	 * 
	 * @param qaSet the question/questionnaire to be added to this knowledge
	 *        base
	 * @author joba
	 * @date 15.04.2010
	 */
	public void addQASet(QASet qaSet) {
		checkID(qaSet);
		if (!objectIDMap.containsKey(qaSet.getId())) {
			objectIDMap.put(qaSet.getId(), qaSet);
			objectNameMap.put(qaSet.getName(), qaSet);
			if (qaSet.getKnowledgeBase() == null) {
				qaSet.setKnowledgeBase(this);
			}
		}

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

	/**
	 * Returns the configurations of all registered problem-solvers.
	 * 
	 * @return the list of problem-solver configurations sorted by priority
	 */
	public List<PSConfig> getPsConfigs() {
		// the list is sorted
		Collections.sort(psConfigs);
		return Collections.unmodifiableList(psConfigs);
	}

	/**
	 * Inserts a new problem-solver configuration.
	 * 
	 * @param psConfig the new problem-solver configuration
	 * @author joba
	 * @date 15.04.2010
	 */
	public void addPSConfig(PSConfig psConfig) {
		psConfigs.add(psConfig);
	}

	/**
	 * Removes a specified problem-solver configuration.
	 * 
	 * @param psConfig the specified problem-solver configuration
	 * @author joba
	 * @date 15.04.2010
	 */
	public void removePSConfig(PSConfig psConfig) {
		psConfigs.remove(psConfig);
	}

	// +++++++++++++++++ Deprecated Methods ++++++++++++++++++

	/**
	 * Not used anymore.
	 * 
	 * @deprecated Not used anymore.
	 */
	@Deprecated
	public void setCostUnit(String id, String name) {
		costUnit.put(id, name);
	}

	/**
	 * Not used anymore.
	 * 
	 * @deprecated Not used anymore.
	 */
	@Deprecated
	public void setCostVerbalization(String id, String name) {
		costVerbalization.put(id, name);
	}

	/**
	 * Not used anymore.
	 * 
	 * @deprecated Not used anymore.
	 */
	@Deprecated
	public Set<String> getCostIDs() {
		return costVerbalization.keySet();
	}

	/**
	 * Not used anymore.
	 * 
	 * @deprecated Not used anymore.
	 */
	@Deprecated
	public String getCostUnit(String id) {
		return costUnit.get(id);
	}

	/**
	 * Not used anymore.
	 * 
	 * @deprecated Not used anymore.
	 */
	@Deprecated
	public String getCostVerbalization(String id) {
		return costVerbalization.get(id);
	}

	public void setRootQASet(QASet rootQASet) {
		this.rootQASet = rootQASet;
		if (!getQASets().contains(rootQASet)) {
			addQASet(rootQASet);
		}
	}

	public void setRootSolution(Solution rootSolution) {
		this.rootSolution = rootSolution;
		if (!solutions.contains(rootSolution)) {
			solutions.add(rootSolution);
		}
	}

}
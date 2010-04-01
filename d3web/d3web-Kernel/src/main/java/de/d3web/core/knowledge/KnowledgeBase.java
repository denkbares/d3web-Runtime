/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
import java.util.Vector;
import java.util.logging.Logger;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSConfig;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.terminology.Diagnosis;
import de.d3web.core.knowledge.terminology.IDObject;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.info.DCMarkedUp;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.knowledge.terminology.info.Properties;
import de.d3web.core.knowledge.terminology.info.PropertiesContainer;
import de.d3web.core.session.values.AnswerChoice;

/**
 * Stores questions (specially initial questions), sets of questions and
 * diagnoses which are relevant to a single knowledge base. The knotting
 * knowledge (e.g. rules) between the questions and diagnoses is not stored
 * explicitly here. This is weaved in the questions and diagnoses where it is
 * used.
 * 
 * @author joba, Christian Betz
 * @see Diagnosis
 * @see Question
 * @see RuleComplex
 * @see QASet
 */
/**
 * @author Administrator
 * 
 */
public class KnowledgeBase implements KnowledgeContainer, DCMarkedUp,
		PropertiesContainer {

	private Properties properties;

	private DCMarkup dcMarkup;

	private String kbID;

	private Map<String, String> costVerbalization;

	private Map<String, String> costUnit;

	private List<? extends QASet> initQuestions;

	private List<Diagnosis> diagnoses;

	private List<Resource> resouces = new ArrayList<Resource>();
	
	private List<PSConfig> psConfigs = new ArrayList<PSConfig>();

	/**
	 * Hashes the objects for ID
	 */
	private Map<String, IDObject> objectIDMap = new HashMap<String, IDObject>();

	/**
	 * Hashes the objects for names (unique name assumption required)
	 */
	private Map<String, IDObject> objectNameMap = new HashMap<String, IDObject>();

	/**
	 * Map with key="ps-method type" value="list of e.g. rules provided by this
	 * type"
	 */
	private Map<Class<? extends PSMethod>, Map<MethodKind, List<KnowledgeSlice>>> knowledgeMap;

	/**
	 * @see de.d3web.core.knowledge.terminology.IDReference#getId()
	 */
	public String getId() {
		return kbID;
	}

	public void setId(String id) {
		kbID = id;
	}

	/**
	 * @return usually a List of knowledge slices relating to this NamedObject,
	 *         the specified problemsolver class and it's kind.
	 * @param problemsolver
	 *            java.lang.Class
	 * @param kind
	 *            kind of knowledgeUsed (e.g. FORWARD or BACKWARD)
	 */
	public Object getKnowledge(Class<? extends PSMethod> problemsolver, MethodKind kind) {
		Map<MethodKind, List<KnowledgeSlice>> o = knowledgeMap.get(problemsolver);
		if (o != null) return o.get(kind);
		else return null;
	}

	public KnowledgeBase() {
		diagnoses = new ArrayList<Diagnosis>();
		initQuestions = new ArrayList<QASet>();
		costVerbalization = new TreeMap<String, String>();
		costUnit = new TreeMap<String, String>();
		properties = new Properties();
		dcMarkup = new DCMarkup();

		// unsynchronized version, allows null values
		knowledgeMap = new HashMap<Class<? extends PSMethod>, Map<MethodKind, List<KnowledgeSlice>>>();

	}

	/**
	 * Adds a new diagnosis d to the knowledge base. The new object is not
	 * added, if it is already in the knowledge base.
	 * 
	 * @param d
	 *            the new diagnosis to be added
	 */
	public void add(Diagnosis d) {
		checkID(d);

		if (!objectIDMap.containsKey(d.getId())) {
			objectIDMap.put(d.getId(), d);
			objectNameMap.put(d.getName(), d);

			diagnoses.add(d);
			if (d.getKnowledgeBase() == null)
				d.setKnowledgeBase(this);

		}
	}

	private void checkID(IDObject ido) {
		if (ido.getId() == null) {
			throw new IllegalStateException("IDObject " + ido
					+ " has no assigned ID.");
		}

	}

	/**
	 * Adds a new qcontainer q to the knowledge base. The new object is not
	 * added, if it is already in the knowledge base.
	 * 
	 * @param q
	 *            the new qcontainer to be added
	 */
	public void add(QContainer q) {
		addQASet(q);
		// checkID(q);
		//
		// if (!objectIDMap.containsKey(q.getId())) {
		// objectIDMap.put(q.getId(), q);
		// objectNameMap.put(q.getText(), q);
		//
		// qcontainers.add(q);
		// if (q.getKnowledgeBase() == null)
		// q.setKnowledgeBase(this);
		// }

	}

	/**
	 * Adds a new question q to the knowledge base. The new object is not added,
	 * if it is already in the knowledge base.
	 * 
	 * @param q
	 *            the new question to be added
	 */
	public void add(Question q) {
		addQASet(q);
		// checkID(q);
		//
		// if (!objectIDMap.containsKey(q.getId())) {
		// objectIDMap.put(q.getId(), q);
		// objectNameMap.put(q.getText(), q);
		//
		// questions.add(q);
		// if (q.getKnowledgeBase() == null)
		// q.setKnowledgeBase(this);
		// }

	}

	/**
	 * Removes the specified rule from the knowledge base. <BR>
	 * How it is done: <BR>
	 * <OL>
	 * <LI>rule internal linking is removed (forward, backward)
	 * <LI>rule is set to <code>null</code> (garbage collector removes rule from
	 * memory)
	 * </OL>
	 * 
	 * @param rule
	 *            the rule to be removed
	 * @return true, if rule was contained in knowledge base, and if it could be
	 *         removed
	 */
	public boolean remove(KnowledgeSlice slice) {
		boolean removed = removeKnowledge(slice.getProblemsolverContext(),
				slice);
		slice.remove();
		slice = null;
		return removed;
	}

	/**
	 * Adds any kind of knowledge for the given problem solver and knowledge
	 * context.
	 */
	public synchronized void addKnowledge(Class<? extends PSMethod> problemsolver,
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

	public boolean removeKnowledge(Class<? extends PSMethod> problemsolver,
			KnowledgeSlice knowledgeSlice, MethodKind knowledgeContext) {
		Map<MethodKind, List<KnowledgeSlice>> knowledge = knowledgeMap
				.get(problemsolver);
		if (knowledge != null) {
			List<KnowledgeSlice> slices = knowledge.get(knowledgeContext);
			if (slices != null) {
				while (slices.remove(knowledgeSlice)) {
					// remove all occurring slices
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * A NamedObject is removed from the knowledge base. Before deletion the
	 * corresponding knowledge is also removed (by calling removeKnowledge).
	 * Exception thrown: An object cannot be removed, if
	 * <OL>
	 * <LI>Object has some children</LI>
	 * </OL>
	 * 
	 * @param o
	 *            the object to be removed
	 * @throws IllegalAccessException
	 *             see above
	 */
	public void remove(NamedObject o) throws IllegalAccessException {
		if ((o.getChildren() != null) && (o.getChildren().length > 0)) {
			throw new IllegalAccessException(
					o
							+ " has some children, that should be removed/relinked before removing.");
		}
		else {
			o.removeAllKnowledge();
			objectIDMap.remove(o.getId());
			objectNameMap.remove(o.getName());
			/*
			 * // iteratively, clean the knowledge map Set keySet = new
			 * HashSet(o.getKnowledgeMap().keySet()); for (Iterator iter =
			 * keySet.iterator(); iter.hasNext();) { Object psMethod =
			 * iter.next(); Map kMap = (Map)(o.getKnowledgeMap().get(psMethod));
			 * Set kMapKeys = new HashSet(kMap.keySet()); for (Iterator iterator
			 * = kMapKeys.iterator(); iterator.hasNext();) { Object methodKind =
			 * iterator.next(); if (methodKind instanceof MethodKind) {
			 * Collection slices = new LinkedList((Collection)
			 * (o.getKnowledge((Class) psMethod, (MethodKind) methodKind))); for
			 * (Iterator iter2 = slices.iterator(); iter2.hasNext();) {
			 * KnowledgeSlice slice = (KnowledgeSlice) iter2.next();
			 * o.removeKnowledge((Class)psMethod, slice,
			 * (MethodKind)methodKind); } } } }
			 */
			// remove object from list of contained objects
			if (o instanceof Diagnosis) {
				diagnoses.remove(o);
				// } else if (o instanceof Question) {
				// questions.remove(o);
				// } else if (o instanceof QContainer) {
				// qcontainers.remove(o);
				// } else {
				// Logger.getLogger(this.getClass().getName()).warning(
				// "Did not remove object " + o + " (" + o.getClass()
				// + ") from knowledge base because no"
				// + "instanceof Diagnosis/Question!");
			}
		}
	}

	/**
	 * are query and item case insentitive equal or is item a substring of
	 * query?
	 */
	public static boolean fuzzyEqual(String query, String item) {
		return item.toLowerCase().indexOf(query.toLowerCase()) != -1;
	}

	/**
	 * 
	 * @return a Collection of all stored Problem Solver
	 */
	public Collection<Class<? extends PSMethod>> getAllKnownProblemSolver() {
		return knowledgeMap.keySet();
	}

	/**
	 * Get all knowledge slices contained in this knowledge base.
	 * 
	 * @return a Collection containing objects of type KnowledgeSlice
	 */
	public Collection<KnowledgeSlice> getAllKnowledgeSlices() {
		Set<KnowledgeSlice> allKnowledgeSlices = new HashSet<KnowledgeSlice>();
		Iterator<Class<? extends PSMethod>> psmIter = knowledgeMap.keySet().iterator();
		while (psmIter.hasNext())
			allKnowledgeSlices.addAll(getAllKnowledgeSlicesFor(psmIter.next()));
		return allKnowledgeSlices;
	}

	/**
	 * Get all knowledge slices contained in this knowledge base for the given
	 * problem solver.
	 * 
	 * @return Collection containing objects of type KnowledgeSlice for the
	 *         given PSContext
	 */
	public Collection<KnowledgeSlice> getAllKnowledgeSlicesFor(
			Class<? extends PSMethod> problemSolverContext) {
		Set<KnowledgeSlice> slices = new HashSet<KnowledgeSlice>();
		Map<MethodKind, List<KnowledgeSlice>> knowledge = knowledgeMap
				.get(problemSolverContext);
		if (knowledge != null) {
			Iterator<MethodKind> kindIter = knowledge.keySet().iterator();
			while (kindIter.hasNext())
				slices.addAll(knowledge.get(kindIter.next()));
		}
		return slices;
	}
	
	/**
	 * Get all knowledge slices contained in this knowledge base for the given
	 * problem solver and method kins.
	 * @param problemSolverContext problemsolver
	 * @param kind MethodKind
	 * @return a Collection containing the specified KnowledgeSlices
	 */
	public Collection<KnowledgeSlice> getAllKnowledgeSlicesFor(Class<? extends PSMethod> problemSolverContext, MethodKind kind) {
		Map<MethodKind, List<KnowledgeSlice>> knowledge = knowledgeMap
				.get(problemSolverContext);
		if (knowledge != null) {
			return knowledge.get(kind);
		}
		return new ArrayList<KnowledgeSlice>();
	}

	public Set<String> getCostIDs() {
		return costVerbalization.keySet();
	}

	public String getCostUnit(String id) {
		return costUnit.get(id);
	}

	public String getCostVerbalization(String id) {
		return costVerbalization.get(id);
	}

	/**
	 * Returns all {@link Diagnosis} instances contained in this knowledge base
	 * as a sequential list. The returned list may be unmodifiable.
	 * 
	 * @return list of all diagnoses contained in this KnowledgeBase
	 */
	public List<Diagnosis> getDiagnoses() {
		return Collections.unmodifiableList(diagnoses);
	}

	/**
	 * @return a list of initial questions (questions to be asked in a new case)
	 */
	public List<? extends QASet> getInitQuestions() {
		return initQuestions;
	}

	/**
	 * @return an unmodifiable List of all QContainers contained in this
	 *         KnowledgeBase
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
	 * Returns the (flattened) list of all {@link Question} instances contained
	 * in this knowledge base. The returned list may be unmodifiable.
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
		return questions;
	}

	/**
	 * @return the qasets that do not have any parent.
	 */
	public Diagnosis getRootDiagnosis() {
		Vector<Diagnosis> retVec = new Vector<Diagnosis>();
		Iterator<Diagnosis> iter = getDiagnoses().iterator();
		while (iter.hasNext()) {
			Diagnosis d = iter.next();
			if (d.getParents() == null || d.getParents().length==0) {
				retVec.add(d);
			}
		}
		if (retVec.size() > 1) {
			Logger.getLogger(this.getClass().getName()).warning(
					"more than one diagnosis root node!");

			// [HOTFIX]:aha:multiple root / orphan handling
			Collection<Diagnosis> orphans = new Vector<Diagnosis>();
			Diagnosis root = null;
			iter = retVec.iterator();
			while (iter.hasNext()) {
				Diagnosis d = iter.next();
				if (d.getId().equals("P000")) root = d;
				else orphans.add(d);
			}
			if (root != null) {
				Logger.getLogger(this.getClass().getName()).warning(
						"fixed: single root is now " + root.getId());
				iter = orphans.iterator();
				while (iter.hasNext()) {
					Diagnosis d = (Diagnosis) iter.next();
					d.addParent(root);
					Logger.getLogger(this.getClass().getName()).warning(
							"fixed: node " + d.getId() + " is now child of "
							+ root.getId());
				}
			}
			return root;

		}
		else if (retVec.size() < 1) {
			Logger.getLogger(this.getClass().getName()).severe(
					"no root node in diagnosis tree!");
			return null;
		}
		return (Diagnosis) retVec.get(0);
	}

	/**
	 * @return the qasets that do not have any parent.
	 */
	public QASet getRootQASet() {
		Vector<QASet> retVec = new Vector<QASet>();
		Iterator<QASet> iter = getQASets().iterator();
		while (iter.hasNext()) {
			QASet fk = iter.next();
			if (fk.getParents() == null || fk.getParents().length==0) {
				retVec.add(fk);
			}
		}
		if (retVec.size() > 1) {
			Logger.getLogger(this.getClass().getName()).warning(
					"more than one root node in qaset tree!");

			// [HOTFIX]:aha:multiple root / orphan handling
			Collection<QASet> orphans = new Vector<QASet>();
			QASet root = null;
			iter = retVec.iterator();
			while (iter.hasNext()) {
				QASet q = iter.next();
				if (q.getId().equals("Q000")) root = q;
				else orphans.add(q);
			}
			if (root != null) {
				Logger.getLogger(this.getClass().getName()).warning(
						"fixed: single root is now " + root.getId());
				iter = orphans.iterator();
				while (iter.hasNext()) {
					QASet q = iter.next();
					q.addParent(root);
					Logger.getLogger(this.getClass().getName()).warning(
							"fixed: node " + q.getId() + " is now child of "
							+ root.getId());
				}
			}
			return root;

		}
		else if (retVec.size() < 1) {
			Logger.getLogger(this.getClass().getName()).severe(
					"no root node in qaset tree!");
			return null;
		}
		return retVec.get(0);
	}

	/**
	 * Searches for an Object by its id.
	 * 
	 * @return found object, null if not present.
	 */
	public IDObject search(String id) {
		// suche erst bei Fragen
		IDObject o = searchQuestion(id);
		if (o != null)
			return o;
		// ... bei Frageklassen
		o = searchQContainers(id);
		if (o != null)
			return o;
		// ... bei Diagnosen
		o = searchDiagnosis(id);
		if (o != null)
			return o;
		/*
		 * // ... bei Regeln o = sucheId(getRules(), id); if (o != null) return
		 * o;
		 */
		return null;
	}

	/**
	 * Searches the Answer with the specified ID
	 * 
	 * @param answerID
	 *            ID of the Answer
	 * @return AnswerChoice with the specified ID
	 */
	public AnswerChoice searchAnswerChoice(String answerID) {
		for (Question q : getQuestions()) {
			if (q instanceof QuestionChoice) {
				QuestionChoice qc = (QuestionChoice) q;
				List<AnswerChoice> allAlternatives = qc.getAllAlternatives();
				for (AnswerChoice a : allAlternatives) {
					if (a.getId().equals(answerID)) {
						return a;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Searches for a diagnosis by its id
	 * 
	 * @return found diagnosis, null if not present.
	 */
	public Diagnosis searchDiagnosis(String id) {
		if (objectIDMap.containsKey(id)) {
			IDObject o = objectIDMap.get(id);
			if (o instanceof Diagnosis)
				return (Diagnosis) o;
		}
		return null;
	}

	/**
	 * Searches for a QASet by its id
	 * 
	 * @return found QASet, null if not present.
	 */
	public QASet searchQASet(String id) {
		if (objectIDMap.containsKey(id)) {
			IDObject o = objectIDMap.get(id);
			if (o instanceof QASet)
				return (QASet) o;
		}

		// deprecated: should be removed when objectMap hashing is stable
		QASet ret = searchQuestion(id);
		if (ret == null) {
			ret = searchQContainers(id);
		}
		return ret;

	}

	/**
	 * Searches for a QContainer by its id
	 * 
	 * @return found QContainer, null if not present.
	 */
	public QContainer searchQContainers(String id) {
		if (objectIDMap.containsKey(id)) {
			IDObject o = objectIDMap.get(id);
			if (o instanceof QContainer)
				return (QContainer) o;
		}
		return null;
	}

	/**
	 * Searches an Object for a given name (unique name assumption required)
	 * 
	 * @param name
	 * @return
	 */
	public IDObject searchObjectForName(String name) {
		return this.objectNameMap.get(name);
	}

	/**
	 * Searches for a Question by its id
	 * 
	 * @return found question, null if not present.
	 */
	public Question searchQuestion(String id) {
		if (objectIDMap.containsKey(id)) {
			IDObject o = objectIDMap.get(id);
			if (o instanceof Question)
				return (Question) o;
		}
		return null;
	}

	/**
	 * Stores information about the unit of a specific cost.
	 */
	public void setCostUnit(String id, String name) {
		costUnit.put(id, name);
	}

	public void setCostVerbalization(String id, String name) {
		costVerbalization.put(id, name);
	}

	/**
	 * Sets the initial question list.
	 */
	public void setInitQuestions(List<? extends QASet> initQuestions) {
		this.initQuestions = initQuestions;
	}

	/**
	 * @return a String representation of this kb.
	 */
	public String toString() {
		return "KnowledgeBase ID: "+kbID;
	}

	public DCMarkup getDCMarkup() {
		return dcMarkup;
	}

	public void setDCMarkup(DCMarkup dcMarkup) {
		this.dcMarkup = dcMarkup;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void addResouce(Resource resource) {
		this.resouces.add(resource);
	}

	public List<Resource> getResources() {
		return Collections.unmodifiableList(this.resouces);
	}

	public Resource getResource(String pathname) {
		for (Resource resource : resouces) {
			if (pathname.equalsIgnoreCase(resource.getPathName())) {
				return resource;
			}
		}
		return null;
	}

	public void addQASet(QASet qaSet) {
		checkID(qaSet);
		if (!objectIDMap.containsKey(qaSet.getId())) {
			objectIDMap.put(qaSet.getId(), qaSet);
			objectNameMap.put(qaSet.getName(), qaSet);
			if (qaSet.getKnowledgeBase() == null)
				qaSet.setKnowledgeBase(this);
		}

	}

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
	 * Returns a sorted List of PSConfigs
	 * @return PSConfigs sorted by Priority
	 */
	public List<PSConfig> getPsConfigs() {
		//the list is sorted 
		Collections.sort(psConfigs);
		return Collections.unmodifiableList(psConfigs);
	}

	public void addPSConfig(PSConfig psConfig) {
		psConfigs.add(psConfig);
	}
	
	public void removePSConfig(PSConfig psConfig) {
		psConfigs.remove(psConfig);
	}
}
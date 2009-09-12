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

package de.d3web.kernel.domainModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;
import de.d3web.kernel.psMethods.nextQASet.PSMethodNextQASet;
import de.d3web.kernel.supportknowledge.DCMarkedUp;
import de.d3web.kernel.supportknowledge.DCMarkup;
import de.d3web.kernel.supportknowledge.Properties;
import de.d3web.kernel.supportknowledge.PropertiesContainer;

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
	PropertiesContainer, java.io.Serializable {

    private static class IDObjectIgnoreCaseComparator implements
	    Comparator<IDObject> {
	public int compare(IDObject o1, IDObject o2) {
	    return o1.getId().compareToIgnoreCase(o2.getId());
	}
    }

    // Id for the default case repository
    private static final String DEFAULT_CASE_REPOSITORY_ID = "DEFAULT_CASE_REPOSITORY_ID";

    private Map<String, Collection> caseRepositories;

    private Properties properties;

    private DCMarkup dcMarkup;

    private String kbID;

    private Map<String, String> costVerbalization;

    private Map<String, String> costUnit;

    private List<Question> questions;

    private List<QContainer> qcontainers;

    private List initQuestions;

    private List<Diagnosis> diagnoses;

    private List<PriorityGroup> priorityGroups;

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
    private Map<Class, Map<MethodKind, List<KnowledgeSlice>>> knowledgeMap;

    /**
     * @see de.d3web.kernel.domainModel.IDReference#getId()
     */
    public String getId() {
	return kbID;
    }

    public void setId(String id) {
	kbID = id;
    }

    public String createNewCaseId() {
	// Das hier ist keine super-eindeutige Funktion, insbesondere nicht über
	// mehrere Instanzen (auf verschiedenen Rechnern zur gleichen Zeit).
	// Aber es sollte eigentlich für's erste genügen.
	Date date = new Date();
	StringBuffer id = new StringBuffer();
	id.append("C");
	id.append(date.toString());
	id.append(Double.toString(Math.random()).substring(2));
	return id.toString();
    }

    /**
     * Get caserepositories of the Kb
     * 
     * @return Map from Name of the Repository (String) to Collection of
     *         CaseObject
     */
    public Map getCaseRepositories() {
	return caseRepositories;
    }

    /**
     * Get the default caserepository of the Kb
     * 
     * @return the default case repository as a Collection of CaseObject
     */
    public Collection getDefaultCaseRepository() {
	return getCaseRepository(DEFAULT_CASE_REPOSITORY_ID);
    }

    /**
     * Sets the default case repository of the Kb
     * 
     * @param caseBase
     *                Collection of CaseObject
     */
    public void setDefaultCaseRepository(Collection caseBase) {
	addCaseRepository(DEFAULT_CASE_REPOSITORY_ID, caseBase);
    }

    /**
     * Get the named caserepository
     * 
     * @return the caserepository as Collection of CaseObject
     */
    public Collection getCaseRepository(String id) {
	if (caseRepositories != null) {
	    return caseRepositories.get(id);
	}
	return null;
    }

    /**
     * Adds a new caseRepository (as Collection of CaseObject) under a given
     * name.
     */
    public void addCaseRepository(String id, Collection repository) {
	if (caseRepositories == null) {
	    caseRepositories = new HashMap<String, Collection>();
	}
	caseRepositories.put(id, repository);
    }

    /**
     * @return usually a List of knowledge slices relating to this NamedObject,
     *         the specified problemsolver class and it's kind.
     * @param problemsolver
     *                java.lang.Class
     * @param kind
     *                kind of knowledgeUsed (e.g. FORWARD or BACKWARD)
     */
    public Object getKnowledge(Class problemsolver, MethodKind kind) {
	Object o = knowledgeMap.get(problemsolver);
	if (o != null)
	    return ((Map) o).get(kind);
	else
	    return null;
    }

    public KnowledgeBase() {
	diagnoses = new ArrayList<Diagnosis>();
	questions = new ArrayList<Question>();
	initQuestions = new ArrayList();
	qcontainers = new ArrayList<QContainer>();
	priorityGroups = new ArrayList<PriorityGroup>();
	costVerbalization = new TreeMap<String, String>();
	costUnit = new TreeMap<String, String>();
	properties = new Properties();
	dcMarkup = new DCMarkup();

	// unsynchronized version, allows null values
	knowledgeMap = new HashMap<Class, Map<MethodKind, List<KnowledgeSlice>>>();

    }

    /**
     * Adds a new diagnosis d to the knowledge base. The new object is not
     * added, if it is already in the knowledge base.
     * 
     * @param d
     *                the new diagnosis to be added
     */
    public void add(Diagnosis d) {
	checkID(d);

	if (!objectIDMap.containsKey(d.getId())) {
	    objectIDMap.put(d.getId(), d);
	    objectNameMap.put(d.getText(), d);

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
     * Allows to change IDs for QContainers, questions, answers and diagnosis -
     * sorted (for id) lists are resorted afterwards
     * 
     * @param o
     * @param newID
     * @return whether object-ID has been set
     */
    boolean changeID(IDObject o, String newID) {
	if (newID == null) {
	    return false;
	}
	// TODO: vb: check if id is already used by an other object and return false or throw exception
	// TODO: vb: make IDObject.setId unvisible (package) ?!?
	// TODO: vb: do not search in lists!
	// TODO: vb: how to handle is the id has been used (also how to handle add...() if id has already been used)
	if (this.objectIDMap.containsKey(o.getId())) {
	    this.objectIDMap.remove(o.getId());
	    o.setId(newID);
	    this.objectIDMap.put(o.getId(), o);
	    return true;
	}

	if (searchIdInList(qcontainers, o.getId()) != null) {
	    o.setId(newID);
	    return true;
	} else if (searchIdInList(questions, o.getId()) != null) {
	    o.setId(newID);
	    return true;
	} else if (searchIdInList(diagnoses, o.getId()) != null) {
	    o.setId(newID);
	    return true;
	}

	if (o instanceof Answer) {
	    o.setId(newID);
	    return true;
	}

	return false;

    }

    /**
     * Adds a new priority group pg to the knowledge base. The new object is not
     * added, if it is already in the knowledge base.
     * 
     * @param pg
     *                the new priority group to be added
     */
    public void add(PriorityGroup pg) {

	if (!objectIDMap.containsKey(pg.getId())) {
	    objectIDMap.put(pg.getId(), pg);
	    objectNameMap.put(pg.getText(), pg);

	    getPriorityGroups().add(pg);
	    pg.setKnowledgeBase(this);
	}
    }

    /**
     * Adds a new qcontainer q to the knowledge base. The new object is not
     * added, if it is already in the knowledge base.
     * 
     * @param q
     *                the new qcontainer to be added
     */
    public void add(QContainer q) {
	checkID(q);

	if (!objectIDMap.containsKey(q.getId())) {
	    objectIDMap.put(q.getId(), q);
	    objectNameMap.put(q.getText(), q);

	    qcontainers.add(q);
	    if (q.getKnowledgeBase() == null)
		q.setKnowledgeBase(this);
	}

    }

    /**
     * Adds a new question q to the knowledge base. The new object is not added,
     * if it is already in the knowledge base.
     * 
     * @param q
     *                the new question to be added
     */
    public void add(Question q) {
	checkID(q);

	if (!objectIDMap.containsKey(q.getId())) {
	    objectIDMap.put(q.getId(), q);
	    objectNameMap.put(q.getText(), q);

	    questions.add(q);
	    if (q.getKnowledgeBase() == null)
		q.setKnowledgeBase(this);
	}

    }

    /**
     * Removes the specified rule from the knowledge base. <BR>
     * How it is done: <BR>
     * <OL>
     * <LI>rule internal linking is removed (forward, backward)
     * <LI>rule is set to <code>null</code> (garbage collector removes rule
     * from memory)
     * </OL>
     * 
     * @param rule
     *                the rule to be removed
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
    public synchronized void addKnowledge(Class problemsolver,
	    KnowledgeSlice knowledgeSlice, MethodKind knowledgeContext) {
	try {
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

	} catch (Exception e) {
	    D3WebCase.strace(e + " occured in " + getClass() + ".addKnowledge("
		    + problemsolver + "," + knowledgeSlice + ","
		    + knowledgeContext + ")");
	}
    }

    public boolean removeKnowledge(Class problemsolver,
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

    public boolean removeKnowledge(Class problemsolver,
	    KnowledgeSlice knowledgeSlice, MethodKind knowledgeContext) {
	try {
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
	} catch (Exception e) {
	    D3WebCase.strace(e + " occured in " + getClass()
		    + ".removeKnowledge(" + problemsolver + ","
		    + knowledgeSlice + "," + knowledgeContext + ")");
	    return false;
	}
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
     *                the object to be removed
     * @throws IllegalAccessException
     *                 see above
     */
    public void remove(NamedObject o) throws IllegalAccessException {
	if ((o.getChildren() != null) && (o.getChildren().size() > 0)) {
	    throw new IllegalAccessException(
		    o
			    + " has some children, that should be removed/relinked before removing.");
	} else {
	    o.removeAllKnowledge();
	    objectIDMap.remove(o.getId());
	    objectNameMap.remove(o.getText());
	    /*
	     * // iteratively, clean the knowledge map Set keySet = new
	     * HashSet(o.getKnowledgeMap().keySet()); for (Iterator iter =
	     * keySet.iterator(); iter.hasNext();) { Object psMethod =
	     * iter.next(); Map kMap = (Map)(o.getKnowledgeMap().get(psMethod));
	     * Set kMapKeys = new HashSet(kMap.keySet()); for (Iterator iterator =
	     * kMapKeys.iterator(); iterator.hasNext();) { Object methodKind =
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
	    } else if (o instanceof Question) {
		questions.remove(o);
	    } else if (o instanceof QContainer) {
		qcontainers.remove(o);
	    } else {
		Logger.getLogger(this.getClass().getName()).warning(
			"Did not remove object " + o + " (" + o.getClass()
				+ ") from knowledge base because no"
				+ "instanceof Diagnosis/Question!");
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
     * Used in d3web.Train, can be used anywhere
     * 
     * @param baseVec
     *                List of NamedObjects
     */
    public static Vector fuzzySearchForText(String query, List baseVec) {
	// Liefert alle Objekte aus baseVec, deren Text fuzzyEqual zu query ist.
	Vector retVec = new Vector();
	Iterator iter = baseVec.iterator();
	while (iter.hasNext()) {
	    NamedObject obj = (NamedObject) iter.next();
	    if (fuzzyEqual(query, obj.getText())) {
		retVec.add(obj);
	    }
	}
	return retVec;
    }

    /**
     * 
     * @return a Collection of all stored Problem Solver
     */
    public Collection getAllKnownProblemSolver() {
	return knowledgeMap.keySet();
    }

    /**
     * Get all knowledge slices contained in this knowledge base.
     * 
     * @return a Collection containing objects of type KnowledgeSlice
     */
    public Collection<KnowledgeSlice> getAllKnowledgeSlices() {
	Set<KnowledgeSlice> allKnowledgeSlices = new HashSet<KnowledgeSlice>();
	Iterator<Class> psmIter = knowledgeMap.keySet().iterator();
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
	    Class problemSolverContext) {
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
     * @return an unmodifiable List of all diagnoses contained in this
     *         KnowledgeBase
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
     * @return a list of priority groups
     */
    public List<PriorityGroup> getPriorityGroups() {
	return priorityGroups;
    }

    public QASetIterator getQASetIterator() {
	return new QASetIterator(this);
    }

    /**
     * @return an unmodifiable List of all QContainers contained in this
     *         KnowledgeBase
     */
    public List<QContainer> getQContainers() {
	return Collections.unmodifiableList(qcontainers);
    }

    /**
     * @return an unmodifiable List of all questions contained in this
     *         KnowledgeBase
     */
    public List<Question> getQuestions() {
	return Collections.unmodifiableList(questions);
    }

    /**
     * @return the qasets that do not have any parent.
     */
    public Diagnosis getRootDiagnosis() {
	Vector retVec = new Vector();
	Iterator iter = getDiagnoses().iterator();
	while (iter.hasNext()) {
	    Diagnosis d = (Diagnosis) iter.next();
	    if (d.getParents() == null || d.getParents().isEmpty()) {
		retVec.add(d);
	    }
	}
	if (retVec.size() > 1) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "more than one diagnosis root node!");

	    // [HOTFIX]:aha:multiple root / orphan handling
	    Collection orphans = new Vector();
	    Diagnosis root = null;
	    iter = retVec.iterator();
	    while (iter.hasNext()) {
		Diagnosis d = (Diagnosis) iter.next();
		if (d.getId().equals("P000"))
		    root = d;
		else
		    orphans.add(d);
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

	} else if (retVec.size() < 1) {
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
	Vector retVec = new Vector();
	Iterator iter = getQContainers().iterator();
	while (iter.hasNext()) {
	    QContainer fk = (QContainer) iter.next();
	    if (fk.getParents() == null || fk.getParents().isEmpty()) {
		retVec.add(fk);
	    }
	}
	if (retVec.size() > 1) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "more than one root node in qaset tree!");

	    // [HOTFIX]:aha:multiple root / orphan handling
	    Collection orphans = new Vector();
	    QASet root = null;
	    iter = retVec.iterator();
	    while (iter.hasNext()) {
		QASet q = (QASet) iter.next();
		if (q.getId().equals("Q000"))
		    root = q;
		else
		    orphans.add(q);
	    }
	    if (root != null) {
		Logger.getLogger(this.getClass().getName()).warning(
			"fixed: single root is now " + root.getId());
		iter = orphans.iterator();
		while (iter.hasNext()) {
		    QASet q = (QASet) iter.next();
		    q.addParent(root);
		    Logger.getLogger(this.getClass().getName()).warning(
			    "fixed: node " + q.getId() + " is now child of "
				    + root.getId());
		}
	    }
	    return root;

	} else if (retVec.size() < 1) {
	    Logger.getLogger(this.getClass().getName()).severe(
		    "no root node in qaset tree!");
	    return null;
	}
	return (QASet) retVec.get(0);
    }

    /**
     * inspects the KB and prints out all objects the KB contains.
     */
    public void inspect() {
	System.out.println(getQuestions().size() + " Fragen und "
		+ getDiagnoses().size() + " Diagnosen");
	Iterator iter, secIter;
	iter = getQuestions().iterator();
	while (iter.hasNext()) {
	    Question frage = (Question) iter.next();
	    System.out.println("<" + frage.getClass().getName() + " "
		    + frage.getId() + ": " + frage.getText() + ">");
	    secIter = null;
	    if (frage.getKnowledge(PSMethodHeuristic.class) != null)
		secIter = ((List) (frage.getKnowledge(PSMethodHeuristic.class)))
			.iterator();
	    if (secIter != null)
		while (secIter.hasNext()) {
		    RuleComplex regel = (RuleComplex) secIter.next();
		    System.out.println("  DiagnoseRegel: " + regel.getId()
			    + ": " + regel);
		}
	    secIter = null;
	    if (frage.getKnowledge(PSMethodNextQASet.class) != null)
		secIter = ((List) (frage.getKnowledge(PSMethodNextQASet.class)))
			.iterator();
	    if (secIter != null)
		while (secIter.hasNext()) {
		    RuleComplex regel = (RuleComplex) secIter.next();
		    System.out.println("  FolgefragenRegel: " + regel.getId()
			    + ": " + regel);
		}
	}
	iter = getDiagnoses().iterator();
	while (iter.hasNext()) {
	    Diagnosis diagnose = (Diagnosis) iter.next();
	    System.out.println("<" + diagnose.getClass().getName() + " "
		    + diagnose.getId() + ": " + diagnose.getText() + ">");
	    secIter = null;
	    if (diagnose.getKnowledge(PSMethodHeuristic.class) != null)
		secIter = ((List) (diagnose
			.getKnowledge(PSMethodHeuristic.class))).iterator();
	    if (secIter != null)
		while (secIter.hasNext()) {
		    RuleComplex regel = (RuleComplex) secIter.next();
		    System.out.println("  DiagnoseRegel: " + regel.getId()
			    + ": " + regel);
		}
	    secIter = null;
	    if (diagnose.getKnowledge(PSMethodNextQASet.class) != null)
		secIter = ((List) (diagnose
			.getKnowledge(PSMethodNextQASet.class))).iterator();
	    if (secIter != null)
		while (secIter.hasNext()) {
		    RuleComplex regel = (RuleComplex) secIter.next();
		    System.out.println("  FolgefragenRegel: " + regel.getId()
			    + ": " + regel);
		}
	}
    }

    /**
     * Searches for an Object by its id.
     * 
     * @return found object, null if not present.
     */
    public IDObject search(String id) {
	// suche erst bei Fragen
	IDObject o = searchQuestions(id);
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
	// ... bei Prioritätsgruppen
	o = searchPriorityGroups(id);
	if (o != null)
	    return o;

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

	// deprecated: should be removed when objectMap hashing is stable
	return (Diagnosis) searchIdInList(getDiagnoses(), id);
    }

    /**
     * this method shouldnt be necessary any more to search objects as they are
     * hashed O(1) this method should be factored out, but as all the tests dont
     * create the Objects correctly (setID()) on junit testing this method is
     * still called as the objects are not hashed correctly on creation of the
     * KB
     * 
     * runs in linar time! O(n)
     * 
     * @param list
     * @param id
     * @return
     */
    @Deprecated
    private static IDObject searchIdInList(List list, String id) {
	Logger
		.getLogger(KnowledgeBase.class.getName())
		.warning(
			"Searching object for id in list(linear time) as it wasnt hashed "
				+ id
				+ " shouldnt happen! (only in junit tests, which need to be refactored)");
	if (id == null) {
	    return null;
	}
	for (Object object : list) {
	    if (object instanceof IDObject) {
		if (((IDObject) object).getId().equalsIgnoreCase(id)) {
		    return (IDObject) object;
		}
	    }
	}

	return null;
    }

    public static IDObject searchId(List v, String id) {
	Iterator iter = v.iterator();
	while (iter.hasNext()) {
	    IDObject o = (IDObject) iter.next();
	    if (o.getId().equalsIgnoreCase(id)) {
		return o;
	    }
	}
	return null;
    }

    /**
     * Searches for a PriorityGroup by its id
     * 
     * @return found PriorityGroup, null if not present.
     */
    public PriorityGroup searchPriorityGroups(String id) {
	if (objectIDMap.containsKey(id)) {
	    IDObject o = objectIDMap.get(id);
	    if (o instanceof PriorityGroup)
		return (PriorityGroup) o;
	}

	// deprecated: should be removed when objectMap hashing is stable
	return (PriorityGroup) searchId(getPriorityGroups(), id);
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
	QASet ret = searchQuestions(id);
	if (ret == null) {
	    ret = searchQContainers(id);
	}
	if (ret == null) {
	    ret = searchPriorityGroups(id);
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

	// deprecated: should be removed when objectMap hashing is stable
	return (QContainer) searchIdInList(getQContainers(), id);
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
    public Question searchQuestions(String id) {
	if (objectIDMap.containsKey(id)) {
	    IDObject o = objectIDMap.get(id);
	    if (o instanceof Question)
		return (Question) o;
	}

	// deprecated: should be removed when objectMap hashing is stable
	return (Question) searchIdInList(getQuestions(), id);
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
    public void setInitQuestions(java.util.List initQuestions) {
	this.initQuestions = initQuestions;
    }

    /**
     * @return a String representation of this kb.
     */
    public String toString() {
	String className = this.getClass().getName();
	try {
	    className = className.substring(className.lastIndexOf(".") + 1);
	} catch (Exception ex) {
	    // Exception ignored ...
	}
	return "<" + className + ">";
    }

    public DCMarkup getDCMarkup() {
	return dcMarkup;
    }

    public void setDCDMarkup(DCMarkup dcMarkup) {
	this.dcMarkup = dcMarkup;
    }

    public Properties getProperties() {
	return properties;
    }

    public void setProperties(Properties properties) {
	this.properties = properties;
    }

    public void cleanupStaleQASets(Collection qasets2Remove) {
	StringBuffer sb = new StringBuffer();
	sb.append("ignored " + qasets2Remove.size() + " orphaned qasets: ");
	for (Iterator iter = qasets2Remove.iterator(); iter.hasNext();) {
	    QASet q = (QASet) iter.next();
	    String text = q.getText();
	    if (text.length() > 20)
		text = text.substring(0, 17) + "...";
	    sb.append(q.getId() + "(" + text + ")");
	    if (iter.hasNext())
		sb.append("; ");
	    Iterator qiter = new LinkedList(q.getChildren()).iterator();
	    // we need to create first a copy of the children-list
	    // to avoid a ConcurrentModificationException, because
	    // removeParent removes the Child from the getChildren()-List,
	    // which would break the iterator.
	    while (qiter.hasNext())
		((QASet) qiter.next()).removeParent(q);
	    if (q instanceof Question)
		questions.remove(q);
	    else if (q instanceof QContainer)
		qcontainers.remove(q);
	}
	Logger.getLogger(this.getClass().getName()).warning(sb.toString());
    }

}
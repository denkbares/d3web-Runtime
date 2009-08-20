package de.d3web.kernel.domainModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.supportknowledge.Properties;
import de.d3web.kernel.supportknowledge.PropertiesContainer;

/**
 * NamedObject is parent of knowledge-base objects like QASet, Question,
 * Diagnosis or Answer. <BR>
 * 
 * It provides a map, to store information relevant to the problem-solving
 * methods used in the knowledge base. Each problem-solver should use this map
 * to store the knowledge it needs for this single NamedObject. <BR>
 * NamedObject contains a Map (name: properties) to store additional properties
 * dynamically. <BR>
 * Further there is a property "timeValued", which indicates if this Object is
 * able to change over time or not (default: not timeValued).
 * 
 * @author joba, chris, hoernlein
 * @see de.d3web.kernel.domainModel.IDObject
 * @see de.d3web.kernel.misc.PropertiesAdapter
 */
public abstract class NamedObject extends IDObject implements CaseObjectSource,
	KnowledgeContainer, PropertiesContainer {

    private Properties properties;

    private String text;

    private KnowledgeBase knowledgeBase;

    private List<NamedObject> parents;

    private List<NamedObject> children;

    private Collection<NamedObject> linkedChildren;

    private Collection<NamedObject> linkedParents;

    /*
     * Map with key="ps-method type" value="list of e.g. rules provided by this
     * type"
     * 
     * The map has to be transient, so that huge knowledgebases can be
     * serialized!
     */
    private transient Map<Class, Map<MethodKind, List<KnowledgeSlice>>> knowledgeMap;

    public static final int BEFORE = 1;

    public static final int AFTER = 2;

    /**
     * Creates a new NamedObject. Per default NamedObjects are not time
     * dependend. <BR>
     * You have to set the following properties by hand:
     * <LI>id (see IDObject)
     * <LI>knowledgeBase
     * <LI>text
     */
    public NamedObject() {
	super();
	init();
    }

    private void init() {
	// unsynchronized version, allows null values
	knowledgeMap = new HashMap<Class, Map<MethodKind, List<KnowledgeSlice>>>();

	children = new LinkedList<NamedObject>();
	parents = new LinkedList<NamedObject>();
	linkedChildren = new LinkedList<NamedObject>();
	linkedParents = new LinkedList<NamedObject>();
	properties = new Properties();
    }

    public NamedObject(String id) {
	super(id);
	init();
    }

    public Properties getProperties() {
	return properties;
    }

    public void setProperties(Properties properties) {
	this.properties = properties;
    }

    /**
     * Adds a new child to this named object
     * 
     * @see #addParent(NamedObject parent)
     */
    public void addChild(NamedObject child) {
	if (!hasChild(child)) {
	    addParentChildLink(this, child);
	}
    }

    public void addLinkedChild(NamedObject child) {
	if (!hasChild(child)) {
	    addChild(child);
	}
	if (!getLinkedChildren().contains(child)) {
	    linkedChildren.add(child);
	    child.addLinkedParent(this);
	}
    }

    /**
     * Adds a new child to this named object above or below the specified second
     * object
     * 
     * @param position
     *                1 represents "before", 2 "after"; use NamedObject.BEFORE,
     *                NamedObject.AFTER
     * @see #addParent(NamedObject parent)
     */
    public void addChild(NamedObject childToAdd,
	    NamedObject childToMarkPosition, int position) {
	if (!hasChild(childToAdd)) {
	    children.add(children.indexOf(childToMarkPosition) + position - 1,
		    childToAdd);
	    childToAdd.parents.add(this);
	}
    }

    public void addLinkedChild(NamedObject childToAdd,
	    NamedObject childToMarkPosition, int position) {
	if (!hasChild(childToAdd)) {
	    addChild(childToAdd);
	}
	if (!getLinkedChildren().contains(childToAdd)) {
	    linkedChildren.add(childToAdd);
	}
    }

    /**
     * Adds a new slice of knowledge relevant to this NamedObject. default
     * context: FORWARD knowledge
     * 
     * @param poblemsolver
     *                java.lang.Class
     * @param knowlegeSlice
     *                java.lang.Object
     */
    public synchronized void addKnowledge(Class problemsolver,
	    KnowledgeSlice knowledgeSlice) {
	addKnowledge(problemsolver, knowledgeSlice, MethodKind.FORWARD);
    }

    /**
     * Adds a new slice of knowledge relevant to this NamedObject. The context
     * is relevant of how the knowledge should be used.
     * 
     * @param poblemsolver
     *                Class
     * @param knowlegeSlice
     *                Object
     * @param knowledgeContext
     *                the context in which the knowledge acts
     */
    public synchronized void addKnowledge(Class problemsolver,
	    KnowledgeSlice knowledgeSlice, MethodKind knowledgeContext) {
	try {
	    List knowledgeSlices;
	    /* make sure, that a storage for the problemsolver is available */
	    if (knowledgeMap.get(problemsolver) == null) {
		// usually only 2 kinds (FORWARD and BACKWARD) of knowledge is
		// needed
		HashMap kinds = new HashMap(2);
		knowledgeMap.put(problemsolver, kinds);
	    }
	    Map storage = (knowledgeMap.get(problemsolver));

	    /* make sure, that a storage for the kind of knowledge is available */
	    if (storage.get(knowledgeContext) == null) {
		knowledgeSlices = new LinkedList();
		storage.put(knowledgeContext, knowledgeSlices);
	    }

	    /* all right: now put that slice of knowledge in its slot */
	    List slot = (List) storage.get(knowledgeContext);
	    if (!slot.contains(knowledgeSlice))
		slot.add(knowledgeSlice);

	    if (getKnowledgeBase() != null) {
		getKnowledgeBase().addKnowledge(problemsolver, knowledgeSlice,
			knowledgeContext);
	    }

	} catch (Exception e) {

	    D3WebCase.strace(e + " occured in " + getClass() + ".addKnowledge("
		    + problemsolver + "," + knowledgeSlice + ","
		    + knowledgeContext + ")");
	}
    }

    /**
     * adds this NamedObject as parent of new children.
     */
    private synchronized void addToNewChildren(List<NamedObject> children) {
	if (children != null) {
	    Iterator<NamedObject> iter = children.iterator();
	    while (iter.hasNext())
		iter.next().addParent(this);
	}
    }

    /**
     * adds this NamedObject as child of new parents.
     */
    private synchronized void addToNewParents(List newParents) {
	if (newParents != null) {
	    Iterator iter = newParents.iterator();
	    while (iter.hasNext()) {
		NamedObject parent = (NamedObject) iter.next();
		parent.addChild(this);
	    }
	}
    }

    public synchronized void addParent(NamedObject parent) {
	if (!hasParent(parent)) {
	    addParentChildLink(parent, this);
	}
    }
    
    private static void addParentChildLink(NamedObject parent, NamedObject child) {
	parent.children.add(child);
	child.parents.add(parent);
    }

    public synchronized void addLinkedParent(NamedObject parent) {
	if (!hasParent(parent)) {
	    addParent(parent);
	}
	if (!getLinkedParents().contains(parent)) {
	    linkedParents.add(parent);
	    parent.addLinkedChild(this);
	}
    }

    public List<? extends NamedObject> getChildren() {
	return children;
    }

    /**
     * @return usually a List of knowledge slices relating to this NamedObject
     *         and the specified problemsolver class. default knowledge kind:
     *         MethodKind.FORWARD
     * @param problemsolver
     *                java.lang.Class
     */
    public List<? extends KnowledgeSlice> getKnowledge(Class problemsolver) {
	return getKnowledge(problemsolver, MethodKind.FORWARD);
    }

    /**
     * @return usually a List of knowledge slices relating to this NamedObject,
     *         the specified problemsolver class and it's kind.
     * @param problemsolver
     *                java.lang.Class
     * @param kind
     *                kind of knowledgeUsed (e.g. FORWARD or BACKWARD)
     */
    public List<? extends KnowledgeSlice> getKnowledge(Class problemsolver,
	    MethodKind kind) {
	Map<MethodKind, List<KnowledgeSlice>> o = knowledgeMap
		.get(problemsolver);
	if (o != null)
	    return o.get(kind);
	else
	    return null;
    }

    /**
     * @return the knowledge base this object belongs to.
     */
    public KnowledgeBase getKnowledgeBase() {
	return knowledgeBase;
    }

    /**
     * @return a List of QASets which are the parents of this object.
     */
    public List<? extends NamedObject> getParents() {
	return parents;
    }

    /**
     * @param no
     * @return true if no is contained in the parents list
     */
    public boolean hasParent(NamedObject no) {
	if (getParents() == null)
	    return false;
	return getParents().contains(no);
    }
    
    /**
     * @param child
     * @return true if this is a parent of child
     */
    public boolean hasChild(NamedObject child) {
	return child.hasParent(this);
    }
    
    /**
     * @return the name of this object.
     */
    public String getText() {
	return text;
    }

    private static void removeParentChildLink(NamedObject parent, NamedObject child) {
	parent.children.remove(child);
	child.parents.remove(parent);
    }
    
    public void removeChild(NamedObject child) {
	if (hasChild(child)) {
	    removeParentChildLink(this, child);
	}
    }

    public void removeLinkedChild(NamedObject child) {
	if (getLinkedChildren().contains(child)) {
	    linkedChildren.remove(child);
	    child.removeLinkedParent(this);

	    removeParentChildLink(this, child);
	}
    }

    /**
     * Removes a slice of knowledge relevant to this NamedObject. The context is
     * relevant of how the knowledge should be used.
     * 
     * @param poblemsolver
     *                Class
     * @param knowlegeSlice
     *                Object
     * @param knowledgeContext
     *                the context in which the knowledge acts
     */
    public synchronized void removeKnowledge(Class problemsolver,
	    KnowledgeSlice knowledgeSlice, MethodKind knowledgeContext) {
	try {
	    removeLocalKnowledge(problemsolver, knowledgeSlice,
		    knowledgeContext);

	    if (getKnowledgeBase() != null) {
		getKnowledgeBase().removeKnowledge(problemsolver,
			knowledgeSlice);
	    }

	} catch (Exception e) {
	    D3WebCase.strace(e + " occured in " + getClass()
		    + ".removeKnowledge(" + problemsolver + ","
		    + knowledgeSlice + "," + knowledgeContext + ")");
	}
    }

    public Collection<KnowledgeSlice> getAllKnowledge() {
	Collection<KnowledgeSlice> result = new ArrayList<KnowledgeSlice>();
	for (Class problemsolverKeyClass : knowledgeMap.keySet()) {
	    Map<MethodKind, List<KnowledgeSlice>> map = knowledgeMap
		    .get(problemsolverKeyClass);
	    for (MethodKind methodKind : map.keySet()) {
		List<KnowledgeSlice> list = map.get(methodKind);

		if (list != null) {
		    for (KnowledgeSlice slice : list) {
			result.add(slice);
		    }
		}
	    }
	}
	return result;
    }

    public Collection<KnowledgeSlice> getAllKnowledge(MethodKind methodKind) {
	Collection<KnowledgeSlice> result = new ArrayList<KnowledgeSlice>();
	for (Class problemsolverKeyClass : knowledgeMap.keySet()) {
	    Map<MethodKind, List<KnowledgeSlice>> map = knowledgeMap
		    .get(problemsolverKeyClass);
	    List<KnowledgeSlice> list = map.get(methodKind);

	    if (list != null) {
		for (KnowledgeSlice slice : list) {
		    result.add(slice);
		}
	    }
	}
	return result;
    }

    /**
     * Removes all knowledgeslices relevant to this namedobject. Collects all
     * knowledgeslices and delegates to removeKnowledge().
     * 
     */
    public synchronized Collection<KnowledgeSlice> removeAllKnowledge() {
	Collection<KnowledgeSlice> result = new ArrayList<KnowledgeSlice>();
	for (Class problemsolverKeyClass : knowledgeMap.keySet()) {
	    Map<MethodKind, List<KnowledgeSlice>> map = knowledgeMap
		    .get(problemsolverKeyClass);
	    for (MethodKind methodKind : new ArrayList<MethodKind>(map.keySet())) {
		List<KnowledgeSlice> list = map.get(methodKind);
		for (KnowledgeSlice slice : new ArrayList<KnowledgeSlice>(list)) {
		    removeKnowledge(problemsolverKeyClass, slice, methodKind);
		    result.add(slice);
		}
	    }
	}
	return result;
    }

    /**
     * Removes a slice of knowledge relevant to this NamedObject without
     * modifying the knowlegebase-instance. The context is relevant of how the
     * knowledge should be used.
     * 
     * @param poblemsolver
     *                Class
     * @param knowlegeSlice
     *                Object
     * @param knowledgeContext
     *                the context in which the knowledge acts
     */
    public synchronized boolean removeLocalKnowledge(Class problemsolver,
	    KnowledgeSlice knowledgeSlice, MethodKind knowledgeContext) {
	try {
	    // List knowledgeSlices;

	    /* make sure, that a storage for the problemsolver is available */
	    if (knowledgeMap.get(problemsolver) == null) {
		return false;
	    }
	    Map storage = (knowledgeMap.get(problemsolver));

	    /* make sure, that a storage for the kind of knowledge is available */
	    if (storage.get(knowledgeContext) == null) {
		return false;
	    }

	    /* all right: now put that slice of knowledge in its slot */
	    List slot = (List) storage.get(knowledgeContext);
	    if (slot.contains(knowledgeSlice)) {
		while (slot.remove(knowledgeSlice))
		    ;
	    }
	    storage.put(knowledgeContext, slot);
	    return true;

	} catch (Exception e) {
	    D3WebCase.strace(e + " occured in " + getClass()
		    + ".removeLocalKnowledge(" + problemsolver + ","
		    + knowledgeSlice + "," + knowledgeContext + ")");
	    return false;
	}
    }

    private synchronized void removeAllChildren() {
	if ((children != null)) {
	    while (!children.isEmpty()) {
		NamedObject child = children.get(0);
		child.removeParent(this);
	    }
	}
    }

    /**
     * removes this NamedObject as children of parents.
     */
    private synchronized void removeAllParents() {
	if (parents != null) {
	    while (!parents.isEmpty()) {
		NamedObject parent = parents.get(0);
		removeParent(parent);
	    }
	}
    }

    /**
     * removes this NamedObject as parent of children.
     */
    public synchronized void removeParent(NamedObject parent) {
	if (hasParent(parent)) {
	    parents.remove(parent);
	    removeParentChildLink(parent, this);
	}
    }

    public synchronized void removeLinkedParent(NamedObject parent) {
	if (getLinkedParents().contains(parent)) {
	    linkedParents.remove(parent);
	    parent.removeLinkedChild(this);
	    
	    removeParentChildLink(parent, this);
	}
    }

    /**
     * sets children.
     */
    public void setChildren(List children) {
	removeAllChildren();
	addToNewChildren(children);
	this.children = children;
    }

    /**
     * Sets the knowledge base this object belongs to.
     * 
     * @param newKnowledgeBase
     *                de.d3web.kernel.domainModel.KnowledgeBase
     * @throws KnowledgeBaseObjectModificationException
     *                 if KnowledgeBase has beed already defined
     */
    public void setKnowledgeBase(KnowledgeBase knowledgeBase)
	    throws KnowledgeBaseObjectModificationException {
	if (this.knowledgeBase == null) {
	    this.knowledgeBase = knowledgeBase;
	} else if (!knowledgeBase.equals(getKnowledgeBase())) {
	    throw new KnowledgeBaseObjectModificationException(
		    "KnowledgeBase already defined!");
	}
    }

    /**
     * Sets a new List of QASets as the parents of this object.
     */
    public void setParents(List parents) {
	removeAllParents(); // from the old parents
	addToNewParents(parents); // to the new parents
	this.parents = parents; // NB: set the parents here!
	// due to the hasParent/hasChild check!!
    }

    /**
     * Sets a new text (name) for this object.
     * 
     * @param newText
     *                java.lang.String
     */
    public void setText(String text) {
	this.text = text;
    }

    /**
     * sets the given value for this NamedObject for the current case.
     */
    public abstract void setValue(XPSCase theCase, Object[] values);

    public String toString() {
	return getText();
    }

    /**
     * This method is necessary to serialize a knowlegebase correctly
     * (knowledgeMap is transient!) AND SHOULD BE USED FOR THAT PURPOSE ONLY!!!
     * It is visible only within the package.
     * 
     * @author georg
     */
    Map getKnowledgeMap() {
	return knowledgeMap;
    }

    /**
     * This method is necessary to deserialize a knowlegebase correctly
     * (knowledgeMap is transient!) AND SHOULD BE USED FOR THAT PURPOSE ONLY!!!
     * It is visible only within the package.
     * 
     * @author georg
     */
    void setKnowledgeMap(Map map) {
	knowledgeMap = map;
    }

    // /**
    // * Checks, if objects have equal ID and Text.
    // * @return boolean
    // * @param other java.lang.Object
    // */
    // public boolean equals(Object other) {
    // if (this == other)
    // return true;
    // else if (!super.equals(other)) {
    // return false;
    // } else {
    // NamedObject otherNO = (NamedObject) other;
    // return (
    // (getId().equals(otherNO.getId())
    // && (getText().equals(otherNO.getText()))));
    // }
    // }

    // Computing the hash code on anything other than the objects memory address
    // will result in loss of data if this property is changed.
    // This is crucial especially for the text.

    // /**
    // * Computes hash code out of ID and Text string.
    // * @return computed hash code
    // */
    // public int hashCode() {
    // return (
    // (getId() == null ? 0 : getId().hashCode())
    // + 31 * (getText() == null ? 0 : getText().hashCode()));
    // }

    public Collection getLinkedChildren() {
	return linkedChildren;
    }

    private Collection getLinkedParents() {
	return linkedParents;
    }

}
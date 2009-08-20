package de.d3web.kernel.domainModel;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.dynamicObjects.XPSCaseObject;

public class PriorityGroup extends QASet {

    private class PGComparator implements java.util.Comparator {

	/**
	 * Compares its two arguments for order. Returns a negative integer,
	 * zero, or a positive integer as the first argument is less than, equal
	 * to, or greater than the second.
	 * <p>
	 * 
	 * The implementor must ensure that <tt>sgn(compare(x, y)) ==
	 * -sgn(compare(y, x))</tt>
	 * for all <tt>x</tt> and <tt>y</tt>. (This implies that
	 * <tt>compare(x, y)</tt> must throw an exception if and only if
	 * <tt>compare(y, x)</tt> throws an exception.)
	 * <p>
	 * 
	 * The implementor must also ensure that the relation is transitive:
	 * <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y, z)&gt;0))</tt>
	 * implies <tt>compare(x, z)&gt;0</tt>.
	 * <p>
	 * 
	 * Finally, the implementer must ensure that <tt>compare(x, y)==0</tt>
	 * implies that <tt>sgn(compare(x, z))==sgn(compare(y, z))</tt> for
	 * all <tt>z</tt>.
	 * <p>
	 * 
	 * It is generally the case, but <i>not</i> strictly required that
	 * <tt>(compare(x, y)==0) == (x.equals(y))</tt>. Generally speaking,
	 * any comparator that violates this condition should clearly indicate
	 * this fact. The recommended language is "Note: this comparator imposes
	 * orderings that are inconsistent with equals."
	 * 
	 * @return a negative integer, zero, or a positive integer as the first
	 *         argument is less than, equal to, or greater than the second.
	 * @throws ClassCastException
	 *                 if the arguments' types prevent them from being
	 *                 compared by this Comparator.
	 */
	public int compare(Object o1, Object o2) throws ClassCastException {
	    QContainer qc1 = (QContainer) o1;
	    QContainer qc2 = (QContainer) o2;
	    try {
		return qc1.getPriority().compareTo(qc2.getPriority());
	    } catch (NullPointerException e) {
		return 0;
	    }
	}
    }

    private Integer minLevel;
    private Integer maxLevel;
    private List computedChildren = null;

    /**
     * PriorityGroups constructor comment.
     * 
     * @param theKb
     *                de.d3web.kernel.domainModel.KnowledgeBase
     * @param theId
     *                java.lang.String
     * @param theText
     *                java.lang.String
     */
    public PriorityGroup() {
	super();
	setMinLevel(null);
	setMaxLevel(null);
    }

    public PriorityGroup(String id) {
	super(id);
	setMinLevel(null);
	setMaxLevel(null);
    }

    /**
     * @return true iff the QContainers priority is contained in [minLevel;
     *         maxLevel[ with null representing negative/positive infinite
     *         respectively. <br>
     *         This means this function returns true if priority satisfies the
     *         following constraints:<br>
     *         maxLevel = null &amp;&amp; priority = null<br>
     *         maxLevel = null &amp;&amp; minLevel &le; prioriy<br>
     *         maxLevel &ne; null &amp;&amp; minLevel &le; priority &lt;
     *         maxLevel<br>
     */
    public boolean contains(QContainer qContainer) {
	Integer priority = qContainer.getPriority();
	if (priority == null) {
	    // if QContainer.priority is not specified, it is not contained in
	    // any priority group.

	    // formerly: it was supposed to be very large, so only if MaxLevel
	    // is not set too, qConainer is contained in this PriorityGroup
	    // return ((getMaxLevel() == null) ? true : false);
	    return false;
	}
	// priority != null

	if ((getMaxLevel() != null)
		&& (priority.intValue() >= getMaxLevel().intValue())) {
	    // a MaxLevel is given, so I can test, if priority is higher than or
	    // equal to max level
	    return false;
	}
	if ((getMinLevel() != null)
		&& (priority.intValue() < getMinLevel().intValue())) {
	    // a MinLevel is given, so I can test, if priority is lower than max
	    // level
	    return false;
	}
	return true;
    }

    /**
     * The PriorityGroups Children are the QContainers within the Groups
     * Priority range.
     * 
     * @return java.util.List
     */
    public List getChildren() {
	if (computedChildren == null) {
	    computedChildren = getQContainers();
	    setChildren(computedChildren);
	}
	return computedChildren;
    }

    /**
     * @return the maximal level of this PrioritxGroup
     */
    public Integer getMaxLevel() {
	return maxLevel;
    }

    /**
     * @return the minimal level of this PriorityGroup
     */
    public Integer getMinLevel() {
	return minLevel;
    }

    public void setMaxLevel(Integer maxLevel) {
	this.maxLevel = maxLevel;
    }

    public void setMinLevel(Integer minLevel) {
	this.minLevel = minLevel;
    }

    /**
     * @return all the QContainers contained in this PriorityGroup. This is
     *         determined by the respective priorities. The following constraint
     *         is applicable:<br>
     *         minLevel &le; priority &lt; maxLevel
     */
    private List getQContainers() {
	List retValue = new Vector();
	Iterator iter = getKnowledgeBase().getQContainers().iterator();
	// getKnowledgeBase().getQASetIterator();
	// getQContainers().iterator()
	while (iter.hasNext()) {
	    Object next = iter.next();
	    if (next instanceof QContainer) {
		QContainer nextQContainer = (QContainer) next;
		if (contains(nextQContainer)) {
		    retValue.add(nextQContainer);
		}
	    }
	}
	Collections.sort(retValue, new PGComparator());
	return retValue;
    }

    /**
     * No Implementation yet !
     */
    public XPSCaseObject createCaseObject() {
	Logger.getLogger(this.getClass().getName()).throwing(
		this.getClass().getName(), "createCaseObject",
		new NotImplementedFeatureException());
	return null;

    }

    /**
     * No Implementation yet !
     */
    public boolean expand(List onList, XPSCase theCase) {
	Logger.getLogger(this.getClass().getName()).throwing(
		this.getClass().getName(), "expand",
		new NotImplementedFeatureException());
	return false;

    }

    /**
     * No Implementation yet !
     * 
     * @return false
     */
    public boolean hasValue(XPSCase theCase) {
	Logger.getLogger(this.getClass().getName()).throwing(
		this.getClass().getName(), "hasValue",
		new NotImplementedFeatureException());
	return false;
    }

    /**
     * No Implementation yet !
     * 
     * @return false
     */
    public boolean isDone(XPSCase theCase) {
	Logger.getLogger(this.getClass().getName()).throwing(
		this.getClass().getName(), "isDone",
		new NotImplementedFeatureException());
	return false;

    }

    /**
     * No Implementation yet !
     * 
     * @return false
     */
    public boolean isDone(XPSCase theCase, boolean respectValidFollowQuestions) {
	Logger.getLogger(this.getClass().getName()).throwing(
		this.getClass().getName(), "isDone",
		new NotImplementedFeatureException());
	return false;

    }

    /**
     * Sets the knowledgebase, to which this objects belongs to and adds this
     * object to the knowledge base (reverse link).
     * 
     * @param newKnowledgeBase
     *                de.d3web.kernel.domainModel.KnowledgeBase
     */
    public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
	try {
	    super.setKnowledgeBase(knowledgeBase);
	    // maybe somebody should remove this object from the old
	    // knowledge base if available
	    getKnowledgeBase().add(this);
	} catch (KnowledgeBaseObjectModificationException ex) {
	    Logger.getLogger(this.getClass().getName()).throwing(
		    this.getClass().getName(), "setKnowledgeBase", ex);
	}
    }

    public void setValue(XPSCase theCase, Object[] values) {
	Logger.getLogger(this.getClass().getName()).throwing(
		this.getClass().getName(), "setValue",
		new NotImplementedFeatureException());
    }

    /**
     * No Implementation yet !
     */
    public void addContraReason(Reason source, XPSCase theCase) {
	Logger.getLogger(this.getClass().getName()).throwing(
		this.getClass().getName(), "addContraReason",
		new NotImplementedFeatureException());
    }

    /**
     * No Implementation yet !
     */
    public void addProReason(Reason source, XPSCase theCase) {
	Logger.getLogger(this.getClass().getName()).throwing(
		this.getClass().getName(), "addProReason",
		new NotImplementedFeatureException());
    }

    /**
     * No Implementation yet !
     */
    public void removeContraReason(Reason source, XPSCase theCase) {
	Logger.getLogger(this.getClass().getName()).throwing(
		this.getClass().getName(), "removeContraReason",
		new NotImplementedFeatureException());
    }

    /**
     * No Implementation yet !
     */
    public void removeProReason(Reason source, XPSCase theCase) {
	Logger.getLogger(this.getClass().getName()).throwing(
		this.getClass().getName(), "removeProReason",
		new NotImplementedFeatureException());
    }

}
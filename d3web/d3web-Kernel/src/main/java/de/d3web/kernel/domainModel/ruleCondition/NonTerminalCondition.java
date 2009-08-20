package de.d3web.kernel.domainModel.ruleCondition;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/**
 * Abstract condition for all non-terminal conditions. A non-terminal condition
 * is a container for other terminal or non-terminal sub-conditions.
 * The composite pattern is used for this. This class is the 
 * abstract class for a "composite".
 * 
 * @author Michael Wolber, joba
 */
public abstract class NonTerminalCondition extends AbstractCondition {

	protected List terms;

	/**
	 * Creates a new non-terminal condtion with the specified
	 * sub-conditions.
	 * @param conditions the specified sub-conditions
	 */
	public NonTerminalCondition(List conditions) {
		terms = conditions;
	}

	/**
	 * @return all terminal objects of terms in this complex condition
	 */
	public List getTerminalObjects() {
		List v = new LinkedList();
		Iterator iter = terms.iterator();
		while (iter.hasNext()) {
			List v2 = ((AbstractCondition) iter.next()).getTerminalObjects();
			v = union(v, v2);
		}
		return v;
	}

	/**
	 * @return List containing the terms of this complex condition
	 */
	public java.util.List getTerms() {
		return terms;
	}
	
	public void setTerms(List theTerms) {
		terms = theTerms;
	}

	private List union(List v1, List v2) {
		List ret = new LinkedList();
		ret.addAll(v1);
		ret.addAll(v2);
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (this == other)
			return true;
		else if ((other == null) || (getClass() != other.getClass())) {
			return false;
		} else {
			NonTerminalCondition otherNTC = (NonTerminalCondition) other;
			
			if ((this.getTerms()) != null && (otherNTC.getTerms() != null))
				if (this.getTerms().containsAll(otherNTC.getTerms()) &&
					otherNTC.getTerms().containsAll(this.getTerms()))
					return true;
				else
					return false;
				//beide == null?
			else return ((this.getTerms()) == null && (otherNTC.getTerms() == null));
					
		}
	}
		
	
	public int hashCode() {
		return getTerms().hashCode();
	}

	/**
	 * Create a deep copy of the instance.
	 */	
	public AbstractCondition copy() {
		List newTerms; 
		newTerms = new LinkedList();
		Iterator tIter = getTerms().iterator();
		while (tIter.hasNext()) {
			AbstractCondition c = (AbstractCondition) tIter.next();
			newTerms.add(c.copy());
		}
		return createInstance(newTerms, this);
	}

	/**
	 * Template method. Needs to be implemented in sub-classes by their constructor.
	 */
	protected abstract AbstractCondition createInstance(List theTerms, AbstractCondition original);
	

}
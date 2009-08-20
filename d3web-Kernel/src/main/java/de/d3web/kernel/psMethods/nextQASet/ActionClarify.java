package de.d3web.kernel.psMethods.nextQASet;
import java.util.LinkedList;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.RuleAction;
import de.d3web.kernel.domainModel.RuleComplex;

/**
 * ActionClarify represents the indication of a QASet in order to clarify a suspected diagnosis.
 * Creation date: (21.02.2002 13:14:06)
 * @author Christian Betz
 */
public class ActionClarify extends ActionNextQASet {

	private Diagnosis target = null;

	/**
	 * @param Creates a new clarification action for the given corresponding rule
	 */
	public ActionClarify(RuleComplex theCorrespondingRule) {
		super(theCorrespondingRule);
	}

	/**
	 * @return the Diagnosis to clarify
	 */
	public Diagnosis getTarget() {
		return target;
	}

	/**
	 * sets the Diagnosis to clarify
	 */
	public void setTarget(de.d3web.kernel.domainModel.Diagnosis newTarget) {
		target = newTarget;
	}
	
	public RuleAction copy() {
		ActionClarify a = new ActionClarify(getCorrespondingRule());
		a.setQASets(new LinkedList(getQASets()));
		a.setTarget(getTarget());
		return a;
	}
	
	public int hashCode() {
		int hash = 0;
		if (getQASets() != null)
			hash +=getQASets().hashCode();
		if (getTarget() != null)
			hash += getTarget().hashCode();
		return hash;
	}
	
	public boolean equals(Object o) {
		if (o==this) 
			return true;
		if (o instanceof ActionClarify) {
			ActionClarify a = (ActionClarify)o;
			return (isSame(a.getQASets(), getQASets()) &&
					isSame(a.getTarget(), getTarget()));
		}
		else
			return false;
	}
	
	private boolean isSame(Object obj1, Object obj2) {
		if(obj1 == null && obj2 == null) return true;
		if(obj1 != null && obj2 != null) return obj1.equals(obj2);
		return false;
	}
	
}
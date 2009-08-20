package de.d3web.kernel.psMethods.nextQASet;

import java.util.ArrayList;

import de.d3web.kernel.domainModel.RuleAction;

/**
 * ActionIndication represents the general indication of a QASet.
 * Creation date: (21.02.2002 13:14:06)
 * @author Christian Betz
 */
public class ActionIndication extends ActionNextQASet {

	/**
	 * Creates a new indication action for the given corresponding rule
	 */
	public ActionIndication(
		de.d3web.kernel.domainModel.RuleComplex theCorrespondingRule) {
		super(theCorrespondingRule);
	}
	
	public RuleAction copy() {
		ActionIndication a = new ActionIndication(getCorrespondingRule());
		a.setQASets(new ArrayList(getQASets()));
		return a;
	}
	
	public int hashCode() {
		if(getQASets() != null)
			return (getQASets().hashCode());
		return 0;
	}
	
	public boolean equals(Object o) {
		if (o==this) 
			return true;
		if (o instanceof ActionIndication) {
			ActionIndication a = (ActionIndication)o;
			return isSame(a.getQASets(), getQASets());
		}
		else
			return false;
	}
	
	protected boolean isSame(Object obj1, Object obj2) {
		if(obj1 == null && obj2 == null) return true;
		if(obj1 != null && obj2 != null) return obj1.equals(obj2);
		return false;
	}
	
}
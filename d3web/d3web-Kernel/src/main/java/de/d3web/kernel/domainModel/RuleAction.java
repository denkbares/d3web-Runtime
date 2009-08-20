package de.d3web.kernel.domainModel;

import java.util.List;

/**
 * Abstract class to describe actions executed by rules,
 * when their conditions are true.
 * @author Joachim Baumeister
 */
public abstract class RuleAction implements Cloneable, java.io.Serializable {
	private RuleComplex correspondingRule;

	public RuleAction(RuleComplex theCorrespondingRule) {
		correspondingRule = theCorrespondingRule;
	}

	/**
	 * Executes the included action.
	 */
	public abstract void doIt(de.d3web.kernel.XPSCase theCase);

	/**
	 * @return all objects participating on the action.<BR>
	 * Needed from RuleComplex to manage dynamic references of 
	 * knowledge maps.
	 */
	public abstract List getTerminalObjects();

	public RuleComplex getCorrespondingRule() {
		return correspondingRule;
	}

	public abstract Class getProblemsolverContext();

	public void setCorrespondingRule(RuleComplex newCorrespondingRule) {
		correspondingRule = newCorrespondingRule;
	}

	/**
	 * @return true (default), if this action needs to be executed
	 * only once, when the corresponding rule can fire 
	 * (true -- e.g. ActionHeuristicRS),or, if the action has to 
	 * be executed each time the rule is checked 
	 * (false -- e.g. ActionSetValue/ActionAddValue)
	 */
	public boolean singleFire() {
		return true;
	}

	/**
	 * Tries to undo the included action.
	 */
	public abstract void undo(de.d3web.kernel.XPSCase theCase);
	
	/**
	 * Returns a clone of this RuleAction.<p>
	 */
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
	
	public abstract RuleAction copy();
}
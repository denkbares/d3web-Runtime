package de.d3web.kernel.dynamicObjects;
import de.d3web.kernel.domainModel.RuleComplex;
/**
 * Stores the dynamic, user specific values for a RuleComplex
 * object. It corresponds to the static RuleComplex object.<br>
 * Values to be stored:<br>
 * <li> Current state of the rule (fired/not fired)
 * @author Christian Betz, joba
 * @see RuleComplex
 */
public class CaseRuleComplex extends XPSCaseObject {
	private boolean fired = false;

	/**
	 * Creates a new CaseRuleComlplex. The dynamic store for
	 * the given RuleComplex.
	 */
	public CaseRuleComplex(RuleComplex rule) {
		super(rule);
	}

	/**
	 * Creation date: (04.07.00 14:01:25)
	 * @return the current firing-state of the corresponding rule.
	 */
	public boolean hasFired() {
		return fired;
	}

	/**
	 * Creation date: (04.07.00 14:01:25)
	 * @param fired the new dynamic firing-state of the corresponding rule.
	 */
	public void setFired(boolean fired) {
		this.fired = fired;
	}
}
package de.d3web.kernel.psMethods.nextQASet;
import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.RuleAction;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.psMethods.MethodKind;

/**
 * This abstract class is representing the Action of an indication rule. Specialize this in order to implement a new
 * indication type.
 * Creation date: (19.06.2001 18:21:07)
 * @author Christian Betz
 */
public abstract class ActionNextQASet extends RuleAction {
	private java.util.List qasets;

	/**
	 * Creates a new ActionNextQASet for the given corresponding rule.
	 */
	public ActionNextQASet(RuleComplex theCorrespondingRule) {
		super(theCorrespondingRule);
	}

	/**
	  * Indicates all QASets specified by "setQASets"-Method
	  */
	public void doIt(XPSCase theCase) {
		doItWithContext(theCase, getCorrespondingRule().getProblemsolverContext());
	}
	
	protected void doItWithContext(XPSCase theCase, Class context) {
		Iterator qaset = getQASets().iterator();
		while (qaset.hasNext()) {
			QASet nextQASet = (QASet) qaset.next();
			nextQASet.activate(
				theCase,
				getCorrespondingRule(),
				context);
				
			if (nextQASet instanceof QContainer) {
				theCase.getIndicatedQContainers().add((QContainer) nextQASet);
			}
		}
	}

	/**
	 * @return PSMethodNextQASet.class
	 */
	public Class getProblemsolverContext() {
		return PSMethodNextQASet.class;
	}

	/**
	 * @return List of QASets this Action can indicate
	 */
	public List<QASet> getQASets() {
		return qasets;
	}

	/**
	 * @return all objects participating on the action.<BR>
	 * same as getQASets()
	 */
	public List getTerminalObjects() {
		return getQASets();
	}

	/**
	 * Inserts the corresponding rule as Knowledge to the given QASets
	 */
	private void insertRuleIntoQASets(List theQasets) {
		if (theQasets != null) {
			Iterator qaset = theQasets.iterator();
			while (qaset.hasNext()) {
				((QASet) qaset.next()).addKnowledge(
					getProblemsolverContext(),
					getCorrespondingRule(),
					MethodKind.BACKWARD);
			}
		}
	}

	/**
	 * Removes the corresponding rule from the given QASets
	 */
	private void removeRuleFromOldQASets(List theQasets) {
		if (theQasets != null) {
			Iterator qaset = theQasets.iterator();
			while (qaset.hasNext()) {
				((QASet) qaset.next()).removeKnowledge(
					getProblemsolverContext(),
					getCorrespondingRule(),
					MethodKind.BACKWARD);
			}
		}
	}

	/**
	 * sets a List of QASets that this Action can activate
	 */
	public void setQASets(List qasets) {
		removeRuleFromOldQASets(this.qasets);
		this.qasets = qasets;
		insertRuleIntoQASets(this.qasets);
	}

	/**
	  * Deactivates all activated QASets
	  */
	public void undo(XPSCase theCase) {
		Iterator qaset = getQASets().iterator();
		while (qaset.hasNext()) {
			((QASet) (qaset.next())).deactivate(
				theCase,
				getCorrespondingRule(),
				getCorrespondingRule().getProblemsolverContext());
		}
	}	
}
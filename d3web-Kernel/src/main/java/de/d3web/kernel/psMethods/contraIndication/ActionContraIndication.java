package de.d3web.kernel.psMethods.contraIndication;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.RuleAction;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.psMethods.MethodKind;

/**
 * RuleAction that contra indicates a QASet, when the corresponding rule fires
 * Creation date: (19.06.2001 18:32:09)
 * @author Joachim Baumeister
 */
public class ActionContraIndication extends RuleAction {
	private List qasets;

	/**
	 * Creates a new ActionContraIndication for the given corresponding RuleComplex
	 */
	public ActionContraIndication(RuleComplex theCorrespondingRule) {
		super(theCorrespondingRule);
	}

	/**
	 * Invoked, if rule fires (action)
	 * Creation date: (02.11.2000 14:38:26)
	 * @param theCase current case
	 */
	public void doIt(XPSCase theCase) {
		Iterator qaset = getQASets().iterator();
		while (qaset.hasNext()) {
			((QASet) qaset.next()).addContraReason(
				new QASet.Reason(getCorrespondingRule()),
				theCase);
		}
	}

	/**
	 * @return PSMethodContraIndication.class
	 */
	public Class getProblemsolverContext() {
		return PSMethodContraIndication.class;
	}

	/**
	 * @return List of QASets this action can contraindicate
	 */
	public java.util.List getQASets() {
		return qasets;
	}

	/**
	 * @return all objects participating on the action.<BR>
	 * -> getQASets()
	 */
	public List getTerminalObjects() {
		return getQASets();
	}

	/**
	 * inserts the corresponding rule as knowledge to all
	 * QASets participating on the action.
	 */
	private void insertRuleIntoQASets(List sets) {
		if (getQASets() != null) {
			Iterator qaset = getQASets().iterator();
			while (qaset.hasNext()) {
				((QASet) qaset.next()).addKnowledge(
					getProblemsolverContext(),
					getCorrespondingRule(),
					MethodKind.BACKWARD);
			}
		}
	}

	/**
	 * removes the corresponding rule as knowledge to all
	 * QASets participating on the action.
	 */
	private void removeRuleFromOldQASets(List qasets) {
		if (getQASets() != null) {
			Iterator qaset = getQASets().iterator();
			while (qaset.hasNext()) {
				((QASet) qaset.next()).removeKnowledge(
					getProblemsolverContext(),
					getCorrespondingRule(),
					MethodKind.BACKWARD);
			}
		}
	}

	/**
	 * sets the QASets for contraindication
	 */
	public void setQASets(List theQasets) {
		removeRuleFromOldQASets(getQASets());
		qasets = theQasets;
		insertRuleIntoQASets(getQASets());
	}

	/**
	 * Invoked, if rule is undone (undoing action)
	 * @param theCase current case
	 */
	public void undo(XPSCase theCase) {
		Iterator qaset = getQASets().iterator();
		while (qaset.hasNext()) {
			((QASet) qaset.next()).removeContraReason(
				new QASet.Reason(getCorrespondingRule()),
				theCase);
		}
	}
	
	public int hashCode() {
		if(getQASets() != null)
			return (getQASets().hashCode());
		else return 0;
	}
	
	public boolean equals(Object o) {
		if (o==this) 
			return true;
		if (o instanceof ActionContraIndication) {
			ActionContraIndication a = (ActionContraIndication)o;
			if(getQASets() != null && a.getQASets() != null) { 
				return a.getQASets().equals(getQASets());
			}
			else if(getQASets() == null && a.getQASets() == null) {
				return true;
			}
			else  {
				return false;
			}
		}
		else
			return false;
	}
	
	public RuleAction copy() {
		ActionContraIndication newAction = new ActionContraIndication(getCorrespondingRule());
		newAction.setQASets(new LinkedList(getQASets()));
		return newAction;
	}
}
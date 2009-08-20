package de.d3web.kernel.psMethods.nextQASet;

import java.util.ArrayList;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.RuleAction;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.psMethods.PSMethodInit;

/**
 * Indicates a QASet like ActionIndication, but for QContainers: Then, the QContainer is 
 * inserted directly after the current QConatiner (i.e., instant indication).   
 * @author baumeister
 * @see ActionIndication
 */
public class ActionInstantIndication extends ActionIndication {

	public ActionInstantIndication(RuleComplex theCorrespondingRule) {
		super(theCorrespondingRule);
	}
	
	public RuleAction copy() {
		ActionInstantIndication a = new ActionInstantIndication(getCorrespondingRule());
		a.setQASets(new ArrayList<QASet>(getQASets()));
		return a;
	}
	
	public boolean equals(Object o) {
		if (o==this) 
			return true;
		if (o instanceof ActionInstantIndication) {
			ActionInstantIndication a = (ActionInstantIndication)o;
			return isSame(a.getQASets(), getQASets());
		}
		else
			return false;
	}
	
	@Override
	public void doIt(XPSCase theCase) {
		doItWithContext(theCase, PSMethodInit.class);
	}

}

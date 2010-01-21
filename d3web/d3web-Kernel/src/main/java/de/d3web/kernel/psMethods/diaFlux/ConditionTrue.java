package de.d3web.kernel.psMethods.diaFlux;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.NoAnswerException;
import de.d3web.kernel.domainModel.ruleCondition.TerminalCondition;
import de.d3web.kernel.domainModel.ruleCondition.UnknownAnswerException;

/**
 *
 * @author hatko
 * Created on: 09.10.2009
 */
public class ConditionTrue extends TerminalCondition {
	
	private static final long serialVersionUID = 9073242686723969312L;
	public static final AbstractCondition INSTANCE = new ConditionTrue();
	
	
	private ConditionTrue() {
		super(null);
	}

	@Override
	public boolean eval(XPSCase theCase) throws NoAnswerException,
			UnknownAnswerException {
		return true;
	}

	@Override
	public AbstractCondition copy() {
		return this;
	}
}
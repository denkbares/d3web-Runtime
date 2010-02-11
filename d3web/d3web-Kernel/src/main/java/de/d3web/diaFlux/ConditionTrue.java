package de.d3web.diaFlux;

import de.d3web.core.inference.condition.AbstractCondition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.TerminalCondition;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.XPSCase;

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
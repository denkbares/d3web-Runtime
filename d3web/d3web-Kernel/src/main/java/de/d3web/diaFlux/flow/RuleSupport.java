package de.d3web.diaFlux.flow;

import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.XPSCase;

/**
 * @author Reinhard Hatko
 *
 * Created: 20.12.2009
 */
public class RuleSupport implements ISupport {

	private final Rule rule;
	
	
	/**
	 * @param condition
	 */
	public RuleSupport(Rule rule) {
		this.rule = rule;
	}


	@Override
	public boolean isValid(XPSCase xpsCase) {
		try {
			
			rule.check(xpsCase);
			
			return rule.canFire(xpsCase);
		} catch (UnknownAnswerException e) {
			return false;
		}
	}
	
	
	/**
	 * @return the rule
	 */
	public Rule getRule() {
		return rule;
	}

}

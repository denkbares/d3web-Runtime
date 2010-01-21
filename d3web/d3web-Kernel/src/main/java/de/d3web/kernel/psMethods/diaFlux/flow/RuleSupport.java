package de.d3web.kernel.psMethods.diaFlux.flow;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Rule;
import de.d3web.kernel.domainModel.ruleCondition.UnknownAnswerException;

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

package de.d3web.kernel.psMethods.diaFlux.flow;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.NoAnswerException;
import de.d3web.kernel.domainModel.ruleCondition.UnknownAnswerException;

/**
 * @author Reinhard Hatko
 *
 * Created: 20.12.2009
 */
public class RuleSupport implements ISupport {

	private final RuleComplex rule;
	
	
	/**
	 * @param condition
	 */
	public RuleSupport(RuleComplex rule) {
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
	public RuleComplex getRule() {
		return rule;
	}

}

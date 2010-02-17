package de.d3web.costBenefit;

import java.util.List;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.QContainer;
import de.d3web.costBenefit.inference.PSMethodCostBenefit;

/**
 * The DefaultCostFunction returns the static costs of a QContainer. The actual
 * case is not used.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DefaultCostFunction implements CostFunction {

	public DefaultCostFunction() {
	}

	@Override
	public double getCosts(QContainer qcon, XPSCase theCase) {
		List<? extends KnowledgeSlice> allKnowledge = qcon.getKnowledge(PSMethodCostBenefit.class, CostBenefit.COST_BENEFIT);
		for (KnowledgeSlice ks: allKnowledge) {
			return ((CostBenefit) ks).getCosts();
		}
		return 0;
	}
	
}

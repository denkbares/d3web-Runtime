package de.d3web.persistence.xml.loader.rules;

import java.util.List;

import org.w3c.dom.Node;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.RuleFactory;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;
import de.d3web.persistence.xml.loader.ActionContentFactory;
import de.d3web.persistence.xml.loader.KBLoader;

public class HeuristicRuleActionPersistenceHandler implements RuleActionPersistenceHandler {
	private static HeuristicRuleActionPersistenceHandler instance = new HeuristicRuleActionPersistenceHandler();

	private HeuristicRuleActionPersistenceHandler() {
		super();
	}
	
	public static HeuristicRuleActionPersistenceHandler getInstance() {
		return instance ;
	}
	public Class getContext() {
		return PSMethodHeuristic.class;
	}

	public String getName() {
		return "ActionHeuristicPS";
	}

	public RuleComplex getRuleWithAction(Node node, String id, KBLoader kbLoader, Class context) {
		List ah = ActionContentFactory.createActionHeuristicPSContent(node, kbLoader);
		return RuleFactory.createHeuristicPSRule(id, (Diagnosis) ah.get(1), (Score) ah.get(0),
				null);
	}

}

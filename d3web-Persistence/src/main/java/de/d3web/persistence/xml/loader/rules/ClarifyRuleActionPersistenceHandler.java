package de.d3web.persistence.xml.loader.rules;

import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Node;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.RuleFactory;
import de.d3web.kernel.psMethods.dialogControlling.PSMethodDialogControlling;
import de.d3web.persistence.xml.loader.ActionContentFactory;
import de.d3web.persistence.xml.loader.KBLoader;

public class ClarifyRuleActionPersistenceHandler implements
		RuleActionPersistenceHandler {
	private static ClarifyRuleActionPersistenceHandler instance = new ClarifyRuleActionPersistenceHandler();

	private ClarifyRuleActionPersistenceHandler() {
		super();
	}
	
	public static ClarifyRuleActionPersistenceHandler getInstance() {
		return instance ;
	}
	public Class getContext() {
		return PSMethodDialogControlling.class;
	}

	public String getName() {
		return "ActionClarify";
	}

	public RuleComplex getRuleWithAction(Node node, String id,
			KBLoader kbLoader, Class context) {
		List anq = ActionContentFactory.createActionNextQASetContent(node, kbLoader);
		Diagnosis target = ActionContentFactory.getTarget(node, kbLoader);
		return RuleFactory.createClarificationRule(id, anq, target, null);
	}

}

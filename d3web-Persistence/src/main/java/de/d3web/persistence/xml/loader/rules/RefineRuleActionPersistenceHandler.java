package de.d3web.persistence.xml.loader.rules;

import java.util.List;

import org.w3c.dom.Node;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.RuleFactory;
import de.d3web.kernel.psMethods.dialogControlling.PSMethodDialogControlling;
import de.d3web.persistence.xml.loader.ActionContentFactory;
import de.d3web.persistence.xml.loader.KBLoader;

public class RefineRuleActionPersistenceHandler implements
		RuleActionPersistenceHandler {
	private static RefineRuleActionPersistenceHandler instance = new RefineRuleActionPersistenceHandler();

	private RefineRuleActionPersistenceHandler() {
		super();
	}
	
	public static RefineRuleActionPersistenceHandler getInstance() {
		return instance ;
	}
	public Class getContext() {
		return PSMethodDialogControlling.class;
	}

	public String getName() {
		return "ActionRefine";
	}

	public RuleComplex getRuleWithAction(Node node, String id,
			KBLoader kbLoader, Class context) {
		List anq = ActionContentFactory.createActionNextQASetContent(node, kbLoader);
		Diagnosis target = ActionContentFactory.getTarget(node, kbLoader);
		return RuleFactory.createRefinementRule(id, anq, target, null);
	}

}

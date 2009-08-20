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

public class NextQASetRuleActionPersistenceHandler implements
		RuleActionPersistenceHandler {
	private static NextQASetRuleActionPersistenceHandler instance = new NextQASetRuleActionPersistenceHandler();

	private NextQASetRuleActionPersistenceHandler() {
		super();
	}
	
	public static NextQASetRuleActionPersistenceHandler getInstance() {
		return instance ;
	}
	public Class getContext() {
		return PSMethodDialogControlling.class;
	}

	public String getName() {
		return "ActionNextQASet";
	}

	public RuleComplex getRuleWithAction(Node node, String id,
			KBLoader kbLoader, Class context) {
		Logger.getLogger(this.getClass().getName()).warning("the knowledgebase is using the outdated ActionNextQASet as action type!");

		List anq = ActionContentFactory.createActionNextQASetContent(node, kbLoader);
		return RuleFactory.createIndicationRule(id, anq, null);
	}

}

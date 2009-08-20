package de.d3web.persistence.xml.loader.rules;

import java.util.List;

import org.w3c.dom.Node;

import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.RuleFactory;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.psMethods.dialogControlling.PSMethodDialogControlling;
import de.d3web.persistence.xml.loader.ActionContentFactory;
import de.d3web.persistence.xml.loader.KBLoader;

public class SuppressAnswerRuleActionPersistenceHandler implements
		RuleActionPersistenceHandler {
	private static SuppressAnswerRuleActionPersistenceHandler instance = new SuppressAnswerRuleActionPersistenceHandler();

	private SuppressAnswerRuleActionPersistenceHandler() {
		super();
	}
	
	public static SuppressAnswerRuleActionPersistenceHandler getInstance() {
		return instance ;
	}
	public Class getContext() {
		return PSMethodDialogControlling.class;
	}

	public String getName() {
		return "ActionSuppressAnswer";
	}

	public RuleComplex getRuleWithAction(Node node, String id,
			KBLoader kbLoader, Class context) {
		List sa = ActionContentFactory.createActionSuppressAnswerContent(node, kbLoader);
		return RuleFactory.createSuppressAnswerRule(id, (QuestionChoice) sa.get(0),
				((List) sa.get(1)).toArray(), null);
	}

}

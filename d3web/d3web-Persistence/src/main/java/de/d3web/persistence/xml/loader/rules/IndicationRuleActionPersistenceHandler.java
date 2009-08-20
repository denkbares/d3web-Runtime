package de.d3web.persistence.xml.loader.rules;

import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Node;

import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.RuleFactory;
import de.d3web.kernel.psMethods.dialogControlling.PSMethodDialogControlling;
import de.d3web.persistence.xml.loader.ActionContentFactory;
import de.d3web.persistence.xml.loader.KBLoader;

public class IndicationRuleActionPersistenceHandler implements
		RuleActionPersistenceHandler {
	private static IndicationRuleActionPersistenceHandler instance = new IndicationRuleActionPersistenceHandler();

	private IndicationRuleActionPersistenceHandler() {
		super();
	}
	
	public static IndicationRuleActionPersistenceHandler getInstance() {
		return instance ;
	}
	public Class getContext() {
		return PSMethodDialogControlling.class;
	}

	public String getName() {
		return "ActionIndication";
	}

	public RuleComplex getRuleWithAction(Node node, String id,
			KBLoader kbLoader, Class context) {
		List anq = ActionContentFactory.createActionNextQASetContent(node,
				kbLoader);
		return RuleFactory.createIndicationRule(id, anq, null);
	}

}

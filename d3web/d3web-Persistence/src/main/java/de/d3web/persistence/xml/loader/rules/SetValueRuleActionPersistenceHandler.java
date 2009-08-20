package de.d3web.persistence.xml.loader.rules;

import java.util.List;

import org.w3c.dom.Node;

import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.RuleFactory;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.psMethods.dialogControlling.PSMethodDialogControlling;
import de.d3web.persistence.xml.loader.ActionContentFactory;
import de.d3web.persistence.xml.loader.KBLoader;

public class SetValueRuleActionPersistenceHandler implements RuleActionPersistenceHandler {
	private static SetValueRuleActionPersistenceHandler instance = new SetValueRuleActionPersistenceHandler();

	private SetValueRuleActionPersistenceHandler() {
		super();
	}
	
	public static SetValueRuleActionPersistenceHandler getInstance() {
		return instance ;
	}
	public Class getContext() {
		return PSMethodDialogControlling.class;
	}

	public String getName() {
		return "ActionSetValue";
	}

	public RuleComplex getRuleWithAction(Node node, String id, KBLoader kbLoader, Class context) {
		List as = ActionContentFactory.createActionValueContent(node, kbLoader);
		return RuleFactory.createSetValueRule(id, (Question) as.get(0), ((List) as.get(1))
				.toArray(), null);
	}

}

package de.d3web.persistence.xml.loader.rules;

import java.util.List;

import org.w3c.dom.Node;

import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.RuleFactory;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.psMethods.dialogControlling.PSMethodDialogControlling;
import de.d3web.persistence.xml.loader.ActionContentFactory;
import de.d3web.persistence.xml.loader.KBLoader;

public class AddValueRuleActionPersistenceHandler implements RuleActionPersistenceHandler {
	private static AddValueRuleActionPersistenceHandler instance = new AddValueRuleActionPersistenceHandler();

	private AddValueRuleActionPersistenceHandler() {
		super();
	}
	
	public static AddValueRuleActionPersistenceHandler getInstance() {
		return instance ;
	}
	public Class getContext() {
		return PSMethodDialogControlling.class;
	}

	public String getName() {
		return "ActionAddValue";
	}

	public RuleComplex getRuleWithAction(Node node, String id, KBLoader kbLoader, Class context) {
		List aa = ActionContentFactory.createActionValueContent(node, kbLoader);
		return RuleFactory.createAddValueRule(id, (Question) aa.get(0), ((List) aa.get(1))
				.toArray(), null);
	}

}

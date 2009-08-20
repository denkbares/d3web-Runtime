package de.d3web.persistence.xml.loader.rules;

import org.w3c.dom.Node;

import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.persistence.xml.loader.KBLoader;

public interface RuleActionPersistenceHandler {

	public String getName();
	public RuleComplex getRuleWithAction(Node node, String id, KBLoader kbLoader, Class context);
	public Class getContext();
	
}

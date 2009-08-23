package de.d3web.caserepository.dom;

import org.w3c.dom.Node;

import de.d3web.caserepository.CaseObjectImpl;

public interface AdditionalCaseObjectCreator {

	/**
	 * 
	 * @param creator
	 * @param node
	 * @param caseObject
	 */
	public void process(CaseObjectCreator creator, Node node, CaseObjectImpl caseObject);
	
}
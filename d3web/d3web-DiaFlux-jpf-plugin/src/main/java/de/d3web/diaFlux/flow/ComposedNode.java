package de.d3web.diaFlux.flow;

import de.d3web.core.session.Session;


/**
 * 
 * @author Reinhard Hatko
 * @created 10.08.10
 */
public class ComposedNode extends Node {

	private final String flowName; 
	private final String startNodeName;
	
	public ComposedNode(String id, String flowName, String startNodeName) {
		super(id, flowName);
		this.flowName = flowName;
		this.startNodeName = startNodeName;
	}

	@Override
	public void doAction(Session session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void undoAction(Session session) {
		// TODO Auto-generated method stub
		
	}
	
	

	
}

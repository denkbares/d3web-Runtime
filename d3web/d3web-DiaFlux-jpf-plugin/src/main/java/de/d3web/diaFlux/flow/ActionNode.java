package de.d3web.diaFlux.flow;

import de.d3web.core.inference.PSAction;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.inference.FluxSolver;

public class ActionNode extends Node {

	protected final PSAction action;

	public ActionNode(String id, String name, PSAction action) {
		super(id, name);
		
		if (action == null)
			throw new IllegalArgumentException("'action' must not be null.");
		
		this.action = action;
	}



	public PSAction getAction() {
		return action;
	}
	
	@Override
	public void doAction(Session session) {
		getAction().doIt(session, this, FluxSolver.getInstance());		
	}
	
	@Override
	public void undoAction(Session session) {
		getAction().undo(session, this, FluxSolver.getInstance());
	}
	
	

}

package de.d3web.diaFlux;

import java.util.logging.Level;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.NodeSupport;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.diaFlux.inference.FluxSolver;

public class ComposedNodeAction extends IndicateFlowAction {

	
		
	public ComposedNodeAction(String flow, String node) {
		super(flow, node);
	}
	
	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {
		
		log("Indicating Startnode '"  + getStartNodeName() + "' of flow '" + getFlowName() + "'.", Level.FINE);
		
		StartNode startNode = findStartNode(session);
		INode composedNode = (INode) source;
		support = new NodeSupport(composedNode);
		
		FluxSolver.indicateFlowFromNode(session, composedNode, startNode, support);
	}
	
	

}

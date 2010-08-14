package de.d3web.diaFlux;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.session.Session;

/**
 * 
 * @author Reinhard Hatko
 * @created 12.08.2010
 */
public class ComposedNodeAction extends IndicateFlowAction {

	public ComposedNodeAction(String flow, String node) {
		super(flow, node);
	}

	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {

		// Logger.getLogger(getClass().getName()).log(Level.FINE,
		// ("Indicating Startnode '" + getStartNodeName() + "' of flow '" +
		// getFlowName() + "'."));
		//
		// StartNode startNode = DiaFluxUtils.findStartNode(session,
		// getFlowName(), getStartNodeName());
		// INode composedNode = (INode) source;
		// support = new NodeSupport(composedNode);
		//
		// FluxSolver.indicateFlowFromNode(session, composedNode, startNode,
		// support);
	}

}

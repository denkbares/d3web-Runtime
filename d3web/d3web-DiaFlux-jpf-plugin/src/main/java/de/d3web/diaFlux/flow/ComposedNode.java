package de.d3web.diaFlux.flow;

import de.d3web.core.session.Session;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.Entry;
import de.d3web.diaFlux.inference.FluxSolver;
import de.d3web.diaFlux.inference.IPath;
import de.d3web.diaFlux.inference.Path;
import de.d3web.diaFlux.inference.PathEntry;
import de.d3web.diaFlux.inference.PathReference;

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

		StartNode startNode = DiaFluxUtils.findStartNode(session, flowName, startNodeName);

		// this node now supports the startnode
		FluxSolver.addSupport(session, startNode, new NodeSupport(this));
	}

	@Override
	public void undoAction(Session session) {
		StartNode startNode = DiaFluxUtils.findStartNode(session, flowName, startNodeName);

		FluxSolver.removeSupport(session, startNode, new NodeSupport(this));
	}

	@Override
	public Entry createEntry(Session session, ISupport support) {

		StartNode startNode = DiaFluxUtils.findStartNode(session, flowName, startNodeName);
		IPath path = new Path(startNode, support);

		DiaFluxUtils.getFlowData(session).addPathRef(new PathReference(path));

		return new PathEntry(path, this, support);
	}

}

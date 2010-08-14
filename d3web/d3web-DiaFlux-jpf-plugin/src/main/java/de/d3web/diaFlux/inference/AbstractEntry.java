package de.d3web.diaFlux.inference;

import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.INodeData;
import de.d3web.diaFlux.flow.ISupport;

public abstract class AbstractEntry implements Entry {

	protected final INode node;
	protected final ISupport support;

	public AbstractEntry(INode node, ISupport support) {
		this.node = node;
		this.support = support;
	}

	@Override
	public INode getNode() {
		return node;
	}

	@Override
	public ISupport getSupport() {
		return support;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + node + "]@"
				+ Integer.toHexString(hashCode());
	}

	/**
	 * Checks if this entry's support is still valid. If the support is no
	 * longer valid, it is removed from the according NodeData. If it is valid,
	 * nothing is done. Returns if the node is still active, i.e. if this entry
	 * support is no longer valid, if this node has other support.
	 * 
	 * @param session
	 * @return if the node is still active
	 */
	protected boolean checkSupport(Session session) {
		INodeData nodeData = DiaFluxUtils.getNodeData(getNode(), session);

		if (!support.isValid(session)) {
			FluxSolver.removeSupport(session, getNode(), getSupport());
		}

		return nodeData.isActive();
	}

	@Override
	public boolean removeSupport(Session session) {
		return FluxSolver.removeSupport(session, getNode(), getSupport());
	}

}
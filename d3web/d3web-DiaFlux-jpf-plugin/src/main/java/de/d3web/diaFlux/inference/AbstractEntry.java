package de.d3web.diaFlux.inference;

import java.util.Collection;

import de.d3web.core.inference.PropagationEntry;
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


	public boolean checkSupport(Session session) {
		INodeData nodeData = DiaFluxUtils.getNodeData(getNode(), session);
		
		if (!support.isValid(session)) {
			nodeData.removeSupport(support);
		}
		
		return nodeData.isActive();
	}

}
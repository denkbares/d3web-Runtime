/**
 *
 */
package de.d3web.diaFlux.flow;

import de.d3web.core.session.Session;
import de.d3web.diaFlux.inference.DiaFluxUtils;

/**
 *
 * @author Reinhard Hatko
 *
 * Created: 20.12.2009
 */
public class NodeSupport implements ISupport {

	private final INode node;


	public NodeSupport(INode node) {
		if (node == null)
			throw new IllegalArgumentException("node must not be null.");

		this.node = node;
	}

	public boolean isValid(Session theCase) {
		return DiaFluxUtils.getNodeData(node, theCase).isActive();
	}


	public INode getNode() {
		return node;
	}


	@Override
	public String toString() {
		return "NodeSupport:" + node;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NodeSupport other = (NodeSupport) obj;
		if (node == null) {
			if (other.node != null) return false;
		}
		else if (!node.equals(other.node)) return false;
		return true;
	}

}

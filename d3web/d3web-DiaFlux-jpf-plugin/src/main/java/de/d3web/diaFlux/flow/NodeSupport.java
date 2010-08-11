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

}

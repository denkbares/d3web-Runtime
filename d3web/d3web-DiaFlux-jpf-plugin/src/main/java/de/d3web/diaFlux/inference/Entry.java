package de.d3web.diaFlux.inference;

import java.util.Collection;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.ISupport;

/**
 * @author Reinhard Hatko
 *
 * Created: 08.08.2010
 */
public interface Entry {


	INode getNode();
	

	ISupport getSupport();

	boolean propagate(Session session, Collection<PropagationEntry> changes);
	
	
	/**
	 * Checks if this entry's support is still valid. 
	 * If the support is no longer valid, it is removed from the according
	 * NodeData. If it is valid, nothing is done.
	 * Returns if the node is still active, i.e. if this entry support is no longer valid,
	 * if this node has other support.
	 * 
	 * @param session
	 * @return if the node is still active
	 */
	boolean checkSupport(Session session);
	
	
}
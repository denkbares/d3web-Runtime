package de.d3web.diaFlux.inference;

import java.util.Collection;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.ISupport;

/**
 * @author Reinhard Hatko
 * 
 *         Created: 08.08.2010
 */
public interface Entry {

	/**
	 * 
	 * @return the node which was reached with this entry
	 */
	INode getNode();

	/**
	 * @return the support of this entry. The node this entry represents can
	 *         though have other supports
	 */
	ISupport getSupport();

	/**
	 * 
	 * 
	 * @param session the current session
	 * @param changes the changes of the current propagation
	 * @return
	 */
	boolean propagate(Session session, Collection<PropagationEntry> changes);

	// boolean checkSupport(Session session);

	/**
	 * 
	 * 
	 * @param session
	 * @return true, if the support was successfully removed, false otherwise
	 */
	boolean removeSupport(Session session);

}
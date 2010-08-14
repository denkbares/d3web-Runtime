package de.d3web.diaFlux.inference;

import java.util.Collection;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.ISupport;

/**
 * 
 * @author Reinhard Hatko
 * @created 09.08.2010
 * 
 */
public class PathEntry extends AbstractEntry implements Entry {

	private final IPath path;

	public PathEntry(IPath path, INode node, ISupport support) {
		super(node, support);
		this.path = path;

	}

	@Override
	public boolean propagate(Session session, Collection<PropagationEntry> changes) {

		return path.propagate(session, changes);

	}

	public IPath getPath() {
		return path;
	}

}

/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.diaFlux.inference;

import java.util.Collection;
import java.util.Iterator;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.INode;

/**
 * Just a class for a dirty hack to further use the taghandler for highlighting.
 * for every active composedNode containing a Path, a PathRef is inserted into
 * the pathes of the caseobject.
 * 
 * @author Reinhard Hatko
 * @created 12.08.2010
 */
public class PathReference implements IPath {

	private final IPath path;

	public PathReference(IPath path) {
		this.path = path;
	}

	public INode getFirstNode() {
		return path.getFirstNode();
	}

	public boolean propagate(Session session, Collection<PropagationEntry> changes) {
		return path.propagate(session, changes);
	}

	public boolean isEmpty() {
		return path.isEmpty();
	}

	public Iterator<? extends Entry> iterator() {
		return path.iterator();
	}

	@Override
	public String toString() {
		return "PathRef " + path.toString();
	}

}

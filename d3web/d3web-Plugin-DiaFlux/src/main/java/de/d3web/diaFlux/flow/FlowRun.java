/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.diaFlux.flow;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * 
 * @author Reinhard Hatko
 * @created 17.02.2011
 */
public class FlowRun implements Iterable<INode> {

	private final Set<INode> nodes;

	public FlowRun() {
		this.nodes = new HashSet<INode>();
	}

	public boolean isActive(INode node) {
		return nodes.contains(node);
	}

	public boolean add(INode node) {
		return nodes.add(node);
	}

	public boolean remove(INode node) {
		return nodes.remove(node);
	}

	@Override
	public Iterator<INode> iterator() {
		return new LinkedList<INode>(nodes).iterator();
	}

}

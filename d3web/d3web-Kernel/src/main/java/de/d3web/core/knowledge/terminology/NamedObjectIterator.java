/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.d3web.core.knowledge.terminology;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import de.d3web.core.knowledge.KnowledgeBase;
/**
 * A breadth-first iterator through the knowledgebase. The call to getStartObject determines which hierarchy to travers.
 * @author Christian Betz
 */
public abstract class NamedObjectIterator implements Iterator {
	private LinkedList openList;

	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates a new NamedObjectIterator and initializes the internal "openList" 
	 * with the startObject of the NamedObjects in the given knowledge base kb 
	 */
	public NamedObjectIterator(KnowledgeBase kb) {
		super();
		openList = new java.util.LinkedList();
		openList.add(getStartObject(kb));

	}

	protected abstract NamedObject getStartObject(KnowledgeBase kb);

	public boolean hasNext() {
		return !openList.isEmpty();
	}

	public Object next() throws NoSuchElementException {
		NamedObject retObj = (NamedObject) openList.removeFirst();
		openList.addAll(0, retObj.getChildren());
		return retObj;
	}
}
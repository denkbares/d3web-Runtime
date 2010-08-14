/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.core.io.utilities;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is an Iterator-implementation for iterating over the children of a
 * DOM-Node Creation date: (10.05.2001 13:30:43)
 * 
 * @author praktikum00s
 */
public class ChildrenIterator implements Iterator<Node> {

	private NodeList children = null;
	private int actualPos = 0;
	private int numberOfChildren = 0;

	/**
	 * Creates a new ChildrenIterator to iterate over the childrne of the given
	 * DOM-Node
	 * 
	 * @param parent the parent-node of the children to iterate over
	 */
	public ChildrenIterator(Node parent) {
		super();
		children = parent.getChildNodes();
		numberOfChildren = children.getLength();
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> would return an element
	 * rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the iterator has more elements.
	 */
	public boolean hasNext() {
		return actualPos < numberOfChildren;
	}

	/**
	 * Returns the next element in the interation.
	 * 
	 * @return the next element in the interation.
	 */
	public Node next() {
		return children.item(actualPos++);
	}

	/**
	 * does nothing here!
	 */
	public void remove() {
	}
}
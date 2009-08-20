package de.d3web.xml.domtools;
import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is an Iterator-implementation for iterating over the children of a DOM-Node
 * Creation date: (10.05.2001 13:30:43)
 * @author praktikum00s
 */
public class ChildrenIterator implements Iterator {
	private NodeList children = null;
	private int actualPos = 0;
	private int numberOfChildren = 0;

	/**
	 * Creates a new ChildrenIterator to iterate over the childrne of the given DOM-Node
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
	public Object next() {
		return children.item(actualPos++);
	}

	/**
	 * does nothing here!
	 */
	public void remove() {
	}
}
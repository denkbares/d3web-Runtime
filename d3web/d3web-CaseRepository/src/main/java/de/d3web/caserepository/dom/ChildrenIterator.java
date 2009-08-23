package de.d3web.caserepository.dom;
import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * @author: praktikum00s
 */
public class ChildrenIterator implements Iterator {
	
	private NodeList children = null;
	private int actualPos = 0;
	private int numberOfChildren = 0;

	/**
	 * 
	 * @param parent Node
	 */
	public ChildrenIterator(Node parent) {
		super();
		children = parent.getChildNodes();
		numberOfChildren = children.getLength();
	}

	/**
	 * ChildrenIterator constructor comment.
	 */
	/* This might become interesting later on...
	public ChildrenIterator(Node parent, String xpath)
		throws TransformerException {
		super();
		children = XPathAPI.selectNodeList(parent, xpath);
		numberOfChildren = children.getLength();
	}
	*/
	
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
	 * @return Object - the next element in the interation.
	 * @exception NoSuchElementException iteration has no more elements.
	 */
	public Object next() {
		return children.item(actualPos++);
	}

	/**
	 * 
	 * Removes from the underlying collection the last element returned by the
	 * iterator (optional operation).  This method can be called only once per
	 * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
	 * the underlying collection is modified while the iteration is in
	 * progress in any way other than by calling this method.
	 *
	 * @exception UnsupportedOperationException if the <tt>remove</tt>
	 *		  operation is not supported by this Iterator.
	 
	 * @exception IllegalStateException if the <tt>next</tt> method has not
	 *		  yet been called, or the <tt>remove</tt> method has already
	 *		  been called after the last call to the <tt>next</tt>
	 *		  method.
	 */
	public void remove() { /* not implemented */ }
	
}
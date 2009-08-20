package de.d3web.kernel.domainModel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
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
package de.d3web.collections;

import java.util.Comparator;

import de.d3web.collections.PartialHierarchyTree.Node;

class PartialHierarchyNodeComparator<T> implements Comparator<PartialHierarchyTree.Node<T>> {

	private final Comparator<T> comp;

	public PartialHierarchyNodeComparator(Comparator<T> c) {
		this.comp = c;
	}

	@Override
	public int compare(Node<T> arg0, Node<T> arg1) {
		return comp.compare(arg0.data, arg1.data);
	}

}
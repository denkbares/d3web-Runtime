/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 
 * This data structure is able to capture a (evolving) subset of nodes of a
 * hierarchy as a tree, still retaining the hierarchical relations. That is, if
 * node A is successor of node B in the overall hierarchy G, then node A is also
 * successor of B in this PartialHierarchyTree P (if both contained).
 * 
 * A,B &isin; P and succ(A,B) &isin; G => succ(A,B) &isin; P
 * 
 * The collection of nodes in this data structure can be modified easily, i.e.,
 * nodes can be added or removed, while all hierarchical relations are
 * guaranteed to be retained. In particular, one can start with an empty
 * PartialHierarchyTree and step-by-step insert nodes, while the
 * PartialHierarchyTree will maintain and update the tree structure.
 * 
 * NOTE: This data structure only works correct for well-formed hierarchies,
 * this is each node has at most one parent and the hierarchy is free of cycles.
 * 
 * @author jochenreutelshofer
 * @created 12.04.2013
 */
public class PartialHierarchyTree<T> {

	private final Node<T> root;
	private final PartialHierarchy<T> hierarchy;

	public PartialHierarchyTree(PartialHierarchy<T> h) {
		this.hierarchy = h;
		root = new Node<T>(null);
		root.children = new ArrayList<Node<T>>();
	}

	/**
	 * Returns the root node of this tree. WARNING: This root node is a
	 * convenience node containing NO data. It is NOT part of the data inserted
	 * into this tree and should be used as starting point for tree traversion
	 * only.
	 * 
	 * 
	 * @created 25.11.2013
	 * @return
	 */
	public Node<T> getRoot() {
		return root;
	}

	/**
	 * Returns all top level nodes of this tree. If this returns an empty list,
	 * the overall tree is empty.
	 * 
	 * @created 26.11.2013
	 * @return
	 */
	public List<Node<T>> getRootLevelNodes() {
		return root.getChildren();
	}


	/**
	 * Removes the element from the tree. It considers correct hierarchical
	 * removement, i.e., tree reflects hierarchical node structure after
	 * removement. The tree is restructured if necessary.
	 * 
	 * 
	 * @created 12.04.2013
	 * @param t
	 * @return whether t has been found and removed
	 */
	public boolean removeNodeFromTree(T term) {
		return removeNodeFromTree(term, root);
	}

	/**
	 * Returns the node with value t if existing in this tree. Search runs in
	 * logarithmic time using the hierarchy relation.
	 * 
	 * @created 25.11.2013
	 * @param t
	 * @return
	 */
	public Node<T> find(T t) {
		if (t == null) return null;
		return findRecursiveNode(t, root);
	}


	private Node<T> findRecursiveNode(T externalNode, Node<T> treeNode) {
		if (externalNode.equals(treeNode.data)) return treeNode;

		// descending tree for search only makes sense for sub-node
		if (treeNode.equals(root) || hierarchy.isSuccessorOf(externalNode, treeNode.data)) {
			List<Node<T>> children = treeNode.getChildren();
			Iterator<Node<T>> iterator = children.iterator();
			while (iterator.hasNext()) {
				Node<T> child = iterator.next();

				// other search recursive
				Node<T> found = findRecursiveNode(externalNode, child);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	/**
	 * Returns all nodes contained in this tree.
	 * 
	 * @created 12.04.2013
	 * @return
	 */
	public Set<Node<T>> getNodes() {
		Set<Node<T>> result = new HashSet<Node<T>>();
		collectNodes(root, result);
		return result;
	}

	/**
	 * Returns all nodes contained in this tree.
	 * 
	 * @created 12.04.2013
	 * @return
	 */
	public List<T> getNodesDFSOrder() {
		List<T> result = new ArrayList<T>();
		addDFS(root, result);
		return result;
	}

	private void addDFS(Node<T> n, List<T> result) {
		List<Node<T>> children = n.getChildren();
		for (Node<T> child : children) {
			result.add(child.data);
			addDFS(child, result);
		}
	}

	/**
	 * Returns a set containing the data elements of all nodes contained in this
	 * tree.
	 * 
	 * @created 12.04.2013
	 * @return
	 */
	public Set<T> getNodeContents() {
		Set<Node<T>> nodes = new HashSet<Node<T>>();
		collectNodes(root, nodes);
		Set<T> result = new HashSet<T>();
		for (Node<T> node : nodes) {
			result.add(node.data);
		}
		return result;
	}

	/**
	 * 
	 * @created 12.04.2013
	 * @param root2
	 * @param result
	 */
	private void collectNodes(Node<T> n, Set<Node<T>> result) {
		result.add(n);
		List<Node<T>> children = n.getChildren();
		for (Node<T> child : children) {
			collectNodes(child, result);
		}
		result.remove(root);
	}

	public int getNodeCount() {
		Set<Node<T>> set = new HashSet<Node<T>>();
		collectNodes(getRoot(), set);
		return set.size();
	}

	/**
	 * Inserts a new element into the tree. It considers correct hierarchical
	 * insertion, i.e., tree reflects hierarchical node structure after
	 * insertion. The tree is restructured if necessary.
	 * 
	 * If the element is already contained in the tree nothing happens.
	 * 
	 * 
	 * @created 12.04.2013
	 * @param t
	 * @return true if node was inserted, false otherwise (t already in tree)
	 */
	public boolean insertNode(T t) {
		if (t == null) return false;
		Node<T> existingNode = find(t);
		if (existingNode == null) {
			insertNodeUnder(t, root);
			return true;
		}
		else {
			// element already in tree
			return false;
		}
	}

	/**
	 * Two cases need to be considered:
	 * 
	 * 1) One of the siblings is ancestor of the new concept:
	 * 
	 * - -Then we need to descent and insert under that one
	 * 
	 * 2) A sibling can be a successor of the new concept:
	 * 
	 * - -Then the new concept has to be inserted at this level, but the
	 * siblings need to be searched for successor-concepts. If such concepts are
	 * found, they need to be re-hanged to be child of the new concept.
	 * 
	 * @created 12.04.2013
	 * @param t
	 * @param parent
	 */
	private void insertNodeUnder(T t, Node<T> parent) {

		List<Node<T>> children = parent.getChildren();
		Iterator<Node<T>> descentIterator = children.iterator();

		// look for super concepts to descent
		boolean descent = false;
		while (descentIterator.hasNext()) {
			Node<T> child = descentIterator.next();
			if (hierarchy.isSuccessorOf(t, child.data)) {
				insertNodeUnder(t, child);
				descent = true;
				break;
			}
		}
		if (!descent) {
			Node<T> newNode = new Node<T>(t);
			parent.addChild(newNode);
			newNode.setParent(parent);

			// then check siblings, which could be sub-concepts of the new one
			Iterator<Node<T>> checkSiblingsIterator = children.iterator();
			List<Node<T>> successorSiblings = new ArrayList<Node<T>>();
			while (checkSiblingsIterator.hasNext()) {
				Node<T> sibling = checkSiblingsIterator.next();
				if (sibling.data.equals(t)) continue;
				if (hierarchy.isSuccessorOf(sibling.data, t)) {
					// re-hang sibling to be successor of t
					successorSiblings.add(sibling);

				}
			}
			for (Node<T> successorSibling : successorSiblings) {
				newNode.addChild(successorSibling);
				parent.removeChild(successorSibling);
				successorSibling.setParent(newNode);
			}
		}

	}

	/**
	 * 
	 * @created 12.04.2013
	 * @param term
	 * @param root2
	 */
	private boolean removeNodeFromTree(T term, Node<T> node) {
		boolean found = false;
		List<Node<T>> children = node.getChildren();
		Iterator<Node<T>> iterator = children.iterator();
		Node<T> foundNode = null;
		while (iterator.hasNext()) {
			Node<T> child = iterator.next();
			if (child.data.equals(term)) {
				// if found, delete child node and hook up grand-children
				foundNode = child;
				found = true;
			}
			else {
				// other search recursive
				if (removeNodeFromTree(term, child)) {
					found = true;
				}
			}
			if (found) {
				break;
			}
		}
		if (foundNode != null) {
			removeChildNode(foundNode, node);
		}
		return found;
	}

	private void removeChildNode(Node<T> child, Node<T> father) {
		List<Node<T>> grandChildren = child.getChildren();
		father.removeChild(child);
		for (Node<T> grandChild : grandChildren) {
			father.addChild(grandChild);
		}
	}

	public static class Node<T> {

		private final T data;
		private transient Node<T> parent;
		private List<Node<T>> children = new ArrayList<Node<T>>();;

		@Override
		public String toString() {
			return data.toString();
		}

		/**
		 * 
		 */
		public Node(T data) {
			this.data = data;
		}

		/**
		 * Returns the parent node of this node, returns null if this node is on
		 * top-level.
		 * 
		 * 
		 * @created 26.11.2013
		 * @return
		 */
		public Node<T> getParent() {
			if (parent.data == null) return null;
			return parent;
		}

		@Override
		public int hashCode() {
			if (data != null) {
				return data.hashCode();
			}
			else {
				return super.hashCode();
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Node<?>) {
				Object otherData = ((Node<?>) obj).getData();
				if (otherData == null && data == null) return super.equals(obj);
				if (data == null) return false;
				return data.equals(otherData);
			}
			return false;
		}

		public void setParent(Node<T> parent) {
			this.parent = parent;
		}

		public T getData() {
			return data;
		}

		public void addChild(Node<T> n) {
			if (!children.contains(n)) {
				children.add(n);
				n.parent = this;
			}
		}

		public boolean removeChild(Node<T> n) {
			return children.remove(n);
		}

		public boolean containsChild(Node<T> n) {
			return children.contains(n);
		}

		public List<Node<T>> getChildren() {
			return Collections.unmodifiableList(children);
		}

		public List<Node<T>> getChildrenSortedDefault() {
			return getChildrenSorted(new DefaultComparator());
		}

		public List<Node<T>> getChildrenSorted(Comparator<T> c) {
			List<Node<T>> copy = new ArrayList<Node<T>>();
			copy.addAll(children);
			Collections.sort(copy, new NodeComparator(c));
			return Collections.unmodifiableList(copy);
		}

		class DefaultComparator implements Comparator<T> {

			@SuppressWarnings("unchecked")
			@Override
			public int compare(T o1, T o2) {
				if(o1 instanceof Comparable && o2 instanceof Comparable) {
					return ((Comparable<T>) o1).compareTo((o2));
				}
				throw new IllegalArgumentException(
						"DefaultComparator can not be used. Object are no Comparables!");
			}
		}

		class NodeComparator implements Comparator<Node<T>> {

			private final Comparator<T> comp;

			public NodeComparator(Comparator<T> c) {
				this.comp = c;
			}

			@Override
			public int compare(Node<T> arg0, Node<T> arg1) {
				return comp.compare(arg0.data, arg1.data);
			}

		}

	}
}
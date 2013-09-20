package de.d3web.core.knowledge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.core.knowledge.terminology.Solution;

public class TreeIndexer {

	private Map<TerminologyObject, Integer> indexCache = null;
	private int maxIndex = 0;

	/**
	 * Clears the caches of this indexer and prepares it to reindex all objects
	 * on demand.
	 * 
	 * @created 17.03.2013
	 */
	public synchronized void clear() {
		this.maxIndex = 0;
		this.indexCache = null;
	}

	/**
	 * Returns the index of the specific object in the knowledge base's object
	 * tree. The index is counted in depth-first-search order. If an object is
	 * connected to the tree at multiple places, the lowest index is used. The
	 * root objects ({@link KnowledgeBase#getRootQASet()} and
	 * {@link KnowledgeBase#getRootSolution()}) of the knowledge base starts
	 * with index 0.
	 * <p>
	 * If a object or its predecessors aren't connected to the knowledge base's
	 * root objects, the root of these dangling trees get a number higher than
	 * all objects in these trees. Within such a tree the depth-first-search
	 * order is still preserved (as long as the objects aren't also added to
	 * other dangling trees or the root tree).
	 * 
	 * 
	 * @created 17.03.2013
	 * @param object the object to get the index for
	 * @return the index of the object in its particular tree
	 */
	public synchronized int getIndex(TerminologyObject object) {

		// first index the root tree if the cache is not build yet
		if (indexCache == null) {
			KnowledgeBase base = object.getKnowledgeBase();
			indexCache = new HashMap<TerminologyObject, Integer>();
			TerminologyObject root = (object instanceof Solution)
					? base.getRootSolution()
					: base.getRootQASet();
			reindex(root);
		}

		// then check the index an reindex it's dangling parent tree(s)
		// if there are any
		Integer index = indexCache.get(object);
		if (index == null) {
			for (TerminologyObject root : getDanglingRoots(object)) {
				reindex(root);
			}
			index = indexCache.get(object);
		}
		if (index == null) {
			reindex(object);
			index = indexCache.get(object);
		}
		return index;
	}

	private List<TerminologyObject> getDanglingRoots(TerminologyObject object) {
		List<TerminologyObject> result = new LinkedList<TerminologyObject>();
		getDanglingRoots(object, new HashSet<TerminologyObject>(), result);
		return result;
	}

	private void getDanglingRoots(TerminologyObject object, Set<TerminologyObject> visited, List<TerminologyObject> result) {
		// avoid cycles
		if (visited.contains(object)) return;
		visited.add(object);

		// check if we are a dangling root
		TerminologyObject[] parents = object.getParents();
		if (parents.length == 0) {
			// add the object to the results
			result.add(object);
		}
		else {
			// otherwise continue recursively
			for (TerminologyObject parent : parents) {
				getDanglingRoots(parent, visited, result);
			}
		}
	}

	/**
	 * Traverses the hierarchy using a depth-first search and attaches an
	 * ordering number to each visited object..
	 */
	private void reindex(TerminologyObject root) {
		if (root == null) return;

		// terminate recursion in case of cyclic hierarchies
		if (indexCache.containsKey(root)) return;

		// index the item itself
		indexCache.put(root, maxIndex);
		maxIndex++;

		// and index its children recursively
		for (TerminologyObject child : root.getChildren()) {
			reindex(child);
		}
	}

}

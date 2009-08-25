package de.d3web.kernel.psMethods.setCovering.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.setCovering.Finding;
import de.d3web.kernel.psMethods.setCovering.PSMethodSetCovering;
import de.d3web.kernel.psMethods.setCovering.SCDiagnosis;
import de.d3web.kernel.psMethods.setCovering.SCNode;
import de.d3web.kernel.psMethods.setCovering.SCRelation;
import de.d3web.kernel.psMethods.setCovering.pools.SetPool;
import de.d3web.kernel.utilities.Utils;

/**
 * This Class represents the transitive closure of a SetCoveringModel. It
 * contains a calculate-Method which builds the closure from the given model.
 * 
 * @author bruemmer
 */
public class TransitiveClosure {

	private Map rows = null;
	private Map cols = null;

	private Set nodes = null;
	private Map scDiagnosesByNamedObjectId = null;

	public TransitiveClosure(Set nodes) {
		initialize(nodes);
		calculateClosure();
	}

	/**
	 * Initializes the closure with trivial connections.
	 */
	public void initialize(Set nodes) {

		this.nodes = nodes;

		this.rows = new HashMap();
		this.cols = new HashMap();

		this.scDiagnosesByNamedObjectId = new HashMap();

		Iterator nodesIter = nodes.iterator();

		while (nodesIter.hasNext()) {
			SCNode node = (SCNode) nodesIter.next();
			setPaths(node, node, new java.util.HashSet());

			List relations = node.getNamedObject().getKnowledge(PSMethodSetCovering.class,
					MethodKind.FORWARD);
			if (relations != null) {
				Iterator relationsIter = relations.iterator();
				while (relationsIter.hasNext()) {
					SCRelation relation = (SCRelation) relationsIter.next();
					SCNode target = relation.getTargetNode();
					//Set paths = SetPool.getInstance().getEmptySet();
					Set paths = new HashSet();
					paths.add(Utils.createList(new Object[]{relation}));
					setPaths(node, target, paths);
					if (!relation.getSourceNode().isLeaf()) {
						scDiagnosesByNamedObjectId.put(relation.getSourceNode().getNamedObject()
								.getId(), relation.getSourceNode());
					}
					if (!relation.getTargetNode().isLeaf()) {
						scDiagnosesByNamedObjectId.put(relation.getTargetNode().getNamedObject()
								.getId(), relation.getTargetNode());
					}
				}
			}
		}

	}

	public SCNode getFindingByNamedObjectAndAnswersEquality(Finding finding) {
		Iterator iter = nodes.iterator();
		while (iter.hasNext()) {
			SCNode node = (SCNode) iter.next();
			if (node.isLeaf()) {
				Finding other = (Finding) node;
				boolean nobEq = other.getNamedObject().equals(finding.getNamedObject());
				boolean ansEq = SetPool.getInstance().getFilledSet(other.getAnswers()).equals(
						SetPool.getInstance().getFilledSet(finding.getAnswers()));
				if (nobEq && ansEq) {
					return other;
				}
			}
		}
		return null;
	}

	/**
	 * Calculates the transitive closure for the specified List of SCNodes
	 */
	public void calculateClosure() {

		Object[] nodearray = nodes.toArray();

		for (int col = 0; col < nodearray.length; col++) {
			SCNode y = (SCNode) nodearray[col];
			for (int row = 0; row < nodearray.length; row++) {
				SCNode x = (SCNode) nodearray[row];
				if (existsPath(x, y) && !x.equals(y)) {
					Set pathsXY = getPaths(x, y);
					for (int n = 0; n < nodearray.length; n++) {
						SCNode node = (SCNode) nodearray[n];
						if (existsPath(y, node) && !y.equals(node)) {
							Set pathSet = getPaths(x, node);
							if (pathSet == null) {
								//pathSet = SetPool.getInstance().getEmptySet();
								pathSet = new java.util.HashSet();
							}
							Set pathsYNode = getPaths(y, node);
							Iterator pathXYIter = pathsXY.iterator();
							while (pathXYIter.hasNext()) {
								List pathXY = (List) pathXYIter.next();
								Iterator pathYNodeIter = pathsYNode.iterator();
								while (pathYNodeIter.hasNext()) {
									List pathYNode = (List) pathYNodeIter.next();
									List newPath = new LinkedList(pathXY);
									newPath.addAll(pathYNode);
									pathSet.add(newPath);
								}
							}
							setPaths(x, node, pathSet);
						}
					}
				}
			}
		}
	}

	/**
	 * @return the set of paths from source to target
	 */
	public Set getPaths(SCNode source, SCNode target) {
		Map col = getRelationsBySCNodesStartingAt(source);
		if (col != null) {
			return (Set) col.get(target);
		}
		return null;
	}

	/**
	 * Checks, if there exists a path from "from" to "to"
	 * 
	 * @param from
	 *            Source
	 * @param to
	 *            Target
	 * @return true iff there exists a path from Source to Target
	 */
	public boolean existsPath(SCNode from, SCNode to) {
		return getPaths(from, to) != null;
	}

	/**
	 * @return the row for the given node. The row contains all relations that
	 *         point on the given node
	 */
	public Map getRelationsBySCNodesLeadingTo(SCNode node) {
		return (Map) rows.get(node);
	}

	/**
	 * @return the column for the given node. The column contains all relations
	 *         starting at the given node and pointing at any descendant.
	 */
	public Map getRelationsBySCNodesStartingAt(SCNode node) {
		return (Map) cols.get(node);
	}

	private void setPaths(SCNode from, SCNode to, Set paths) {
		Map col = getRelationsBySCNodesStartingAt(from);
		if (col == null) {
			col = new HashMap();
			cols.put(from, col);
		}
		col.put(to, paths);

		Map row = getRelationsBySCNodesLeadingTo(to);
		if (row == null) {
			row = new HashMap();
			rows.put(to, row);
		}
		row.put(from, paths);
	}

	/**
	 * 
	 * @return all nodes occuring in the closure
	 */
	public Set getNodes() {
		return nodes;
	}

	/**
	 * 
	 * @return all nodes occuring in the closure filtered by the given class
	 */
	public Set getNodes(Class filter) {
		//Set ret = SetPool.getInstance().getEmptySet();
		Set ret = new java.util.HashSet();
		
		Iterator iter = nodes.iterator();
		while (iter.hasNext()) {
			Object node = iter.next();
			if (node.getClass() == filter) {
				ret.add(node);
			}
		}
		return ret;
	}

	/**
	 * Returns the matching SCDiagnosis
	 */
	public SCDiagnosis getSCDiagnosisByNamedObjectId(String nobId) {
		return (SCDiagnosis) scDiagnosesByNamedObjectId.get(nobId);
	}

}

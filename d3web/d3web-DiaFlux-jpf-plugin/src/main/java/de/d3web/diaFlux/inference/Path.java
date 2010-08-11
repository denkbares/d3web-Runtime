package de.d3web.diaFlux.inference;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.EdgeSupport;
import de.d3web.diaFlux.flow.IEdge;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.INodeData;
import de.d3web.diaFlux.flow.ISupport;
import de.d3web.diaFlux.flow.StartNode;

/**
 * @author Reinhard Hatko
 *
 * Created: 07.08.2010
 */
public class Path {
	
	private final LinkedList<Entry> entries;
	
	/**
	 * @param startNode
	 * @param session 
	 */
	public Path(StartNode startNode, ISupport support) {
		this.entries = new LinkedList<Entry>();
		this.entries.push(startNode.createEntry(null, support));
	}
	
	/**
	 * Returns the first Node of this path. This has to be either a StartNode or a SnapshotNode. Null if the path is empty.
	 * 
	 * @return
	 */
	public INode getFirstNode() {
		if (isEmpty()) {
			return null;
		} else {
			return entries.getFirst().getNode();
		}
	}
	
	public boolean propagate(Session session, Collection<PropagationEntry> changes) {
		
		boolean change = false;
		change |= maintainTruth(session, changes);
		change |= flow(session);
		
		return change;
		
	}
	
	

	private boolean flow(Session session) {

		FluxSolver.log("Start flowing from node: " + entries.getLast().getNode());

		boolean continueFlowing = true;
		boolean change = false;
		
		while (continueFlowing) {

			IEdge edge = selectNextEdge(session);

			if (edge == null) { // no edge to take
				FluxSolver.log("Staying in Node: " + this.entries.getLast().getNode());
				return change;
			}

			FluxSolver.log("Following edge '" + edge + "'.");

			continueFlowing = followEdge(session, edge);
			change |= continueFlowing;

		}
		return change;

	}
	
	/**
	 * Selects appropriate successor of {@code node} according to the current
	 * state of the case.
	 * 
	 * @param session
	 * 
	 * @return
	 */
	private IEdge selectNextEdge(Session session) {

		INode node = entries.getLast().getNode();

		Iterator<IEdge> edges = node.getOutgoingEdges().iterator();

		while (edges.hasNext()) {

			IEdge edge = edges.next();

			try {
				if (edge.getCondition().eval(session)) {
					return edge;
				}
			}
			catch (NoAnswerException e) {}
			catch (UnknownAnswerException e) {}
		}

		FluxSolver.log("No edge to take from node:" + node);
		return null;

	}

	/**
	 * Activates the given node coming from the given {@link NodeEntry}. Steps:
	 * 1. Sets the node to active. 2. Conducts its action 3. Add
	 * {@link NodeEntry} for node.
	 * 
	 * @param session
	 * @param currentNode
	 * @param entry the pathentry from where to activate the node
	 * @param edge the egde to take
	 * @return nextNode
	 */
	private boolean followEdge(Session session, IEdge edge) {

		INode currentNode = this.entries.getLast().getNode();
		
		if (currentNode != edge.getStartNode())
			throw new IllegalStateException("Not in the expected Node");

		INode nextNode = edge.getEndNode();
		INodeData nextNodeData = DiaFluxUtils.getNodeData(nextNode, session);
		
		EdgeSupport support = new EdgeSupport(edge);
		
		if (nextNodeData.isActive()) {
			FluxSolver.log("Node is already active: " + nextNode);
			nextNodeData.addSupport(session, support); //add support from current path
			return false; // TODO correct?
		}


		addPathEntryForNode(session, nextNode, support);

		doAction(session, nextNode);
		return true;

	}
	
	/**
	 * Adds a path entry for the current node. Predecessor's entry is removed by
	 * this method.
	 * 
	 * @param session
	 * @param currentEntry
	 * @param nextNode
	 * @param currentNode
	 * @return the new {@link NodeEntry} for node
	 */
	private Entry addPathEntryForNode(Session session, INode nextNode, ISupport support) {


		FluxSolver.log("Adding PathEntry for Node' " + nextNode + "' as successor of '"
				+ this.entries.getLast().getNode() + "'.");


		INodeData nodeData = DiaFluxUtils.getNodeData(nextNode, session);
		
		Entry newEntry = nextNode.createEntry(session, support);

		nodeData.addSupport(session, support);
		
		entries.add(newEntry);

		return newEntry;

	}

	
	//TMS

	private boolean maintainTruth(Session session, Collection<PropagationEntry> changes) {

		FluxSolver.log("Start maintaining truth.");

		List<int[]> wrongSubPathes = new LinkedList<int[]>();
		
		for (int i = 0; i < this.entries.size(); i++) {
			
			Entry entry = this.entries.get(i);
			boolean support = entry.checkSupport(session);
			
			if (!support) {
				INode node = entry.getNode();
				FluxSolver.log("Node is no longer supported: " + node);
				
				int lastIndex = findWrongSubpath(session, i);
				wrongSubPathes.add(0, new int[] {i, lastIndex}); //insert first, to iterate in reverse order later
				i = lastIndex;
				
			}

		}
		
		if (wrongSubPathes.isEmpty()) {
			FluxSolver.log("No TMS necessary");
			return false;
		} else {
			
			for (int[] is : wrongSubPathes) {
				collapseSubpath(is[0], is[1], session);
				
			}
			FluxSolver.log("Finished maintaining truth.");
			return true;
		}


	}
	
	private void collapseSubpath(int from, int to, Session session) {
		
		if (to < from)
			throw new IllegalArgumentException("Can not collapse path from " + from + " to " + to);
		
		
		for (int i = to; i >= from; i--){
			Entry entry = entries.get(i); //TODO can the entry also be removed before calling undo??
			INode node = entry.getNode();
			
			FluxSolver.removeSupport(session, node, entry.getSupport());
			
			undoAction(session, node);
			this.entries.remove(i);
			
		}
		
		
		
	}


	private int findWrongSubpath(Session session, int firstIndex) {
		Entry startEntry = this.entries.get(firstIndex);
		
		INode startingNode = startEntry.getNode();
		boolean active = DiaFluxUtils.getNodeData(startingNode, session).isActive();
		
		if (active)
			throw new IllegalStateException("Can not collapse Path of active Node: " + startingNode);
		
		int lastIndex;
		
		for (lastIndex = firstIndex; lastIndex < this.entries.size(); lastIndex++) {
			
			Entry entry = this.entries.get(firstIndex);
			boolean support = entry.checkSupport(session);
			
			if (support) {
				FluxSolver.log("Found node with support during TMS: " + entry);
				return lastIndex;
				
			}

		}
		return lastIndex - 1; //reached end -> lastIndex is one off
		
	}



	
	private void doAction(Session session, INode node) {
		FluxSolver.log("Doing action of node: " + node);
		node.doAction(session);
	}
	

	
	private void undoAction(Session session, INode node) {
		FluxSolver.log("Undoing action of node: " + node);
		node.undoAction(session);
	}



	public boolean isEmpty() {
		return entries.isEmpty();
	}


	public Iterator<? extends Entry> iterator() {
		return entries.iterator();
	}
	
	@Override
	public String toString() {
		if (isEmpty()) {
			return "empty path";
		} else {
			INode firstNode = getFirstNode();
			String name = firstNode.getFlow().getName();
			return "Path in flow '" + name + "' starting at '" + firstNode.getName() + "'";
		}
	}

}

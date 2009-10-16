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

package de.d3web.kernel.psMethods.diaFlux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.RuleAction;
import de.d3web.kernel.domainModel.ruleCondition.NoAnswerException;
import de.d3web.kernel.domainModel.ruleCondition.UnknownAnswerException;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.PropagationEntry;
import de.d3web.kernel.psMethods.diaFlux.flow.Flow;
import de.d3web.kernel.psMethods.diaFlux.flow.FlowData;
import de.d3web.kernel.psMethods.diaFlux.flow.FlowFactory;
import de.d3web.kernel.psMethods.diaFlux.flow.IEdge;
import de.d3web.kernel.psMethods.diaFlux.flow.IEdgeData;
import de.d3web.kernel.psMethods.diaFlux.flow.INode;
import de.d3web.kernel.psMethods.diaFlux.flow.INodeData;
import de.d3web.kernel.psMethods.diaFlux.flow.SnapshotNode;
import de.d3web.kernel.psMethods.diaFlux.flow.StartNode;

/**
 * 
 * @author Reinhard Hatko
 * Created: 10.09.2009
 *
 */
public class FluxSolver implements PSMethod {
	
	public static final MethodKind DIAFLUX = new MethodKind("DIAFLUX");
	
	private static FluxSolver instance;

	private FluxSolver() {
	}

	public static FluxSolver getInstance() {
		if (instance == null)
			instance = new FluxSolver();

		return instance;
	}

	
	@Override
	public void init(XPSCase theCase) {
		
		log("Initing FluxSolver with case: " + theCase);
		
		Flow flowDeclaration = getFlowDeclaration(theCase);
		
		if (flowDeclaration == null)
			return;
		
		INode startNode = flowDeclaration.getStartNodes().get(0);
		
//		addPathEntryForNode(theCase, null, null, startNode.getOutgoingEdges().get(0));
		
		addPathEntryForNode(theCase, null, null, FlowFactory.getInstance().createEdge("Pseudostart", null, startNode, ConditionTrue.INSTANCE));
		
	}

	private Flow getFlowDeclaration(XPSCase theCase) {
		List list = (List)theCase.getKnowledgeBase().getKnowledge(FluxSolver.class, DIAFLUX);
		
		if (list == null || list.isEmpty())
			return null;
		else 
			return (Flow) list.get(0);
	}

	private FlowData getFlowData(XPSCase theCase) {
		return (FlowData) theCase.getCaseObject(getFlowDeclaration(theCase));
	}
	
	
	/**
	 * Adds a path entry for the current node. Predecessor's entry is removed by this method.
	 * @param theCase 
	 * @param currentNode  
	 * @param currentEntry
	 * @param nextNode

	 * @return the new {@link PathEntry} for node
	 */
	private PathEntry addPathEntryForNode(XPSCase theCase, INode currentNode, PathEntry currentEntry, IEdge edge) {
		
		
		FlowData flow = getFlowData(theCase);
		
		INode nextNode = edge.getEndNode(); 
		log("Adding PathEntry for Node' " + nextNode + "' as successor of '" + currentNode + "'.");
		
		// both are null if a new flow is started (at first start, after snapshot, (after fork?))
		PathEntry stack = null;
		PathEntry predecessor = null;
		
		if (currentNode instanceof SnapshotNode) {
			//starts new flow -> stack = null, pred = null 
			
		} else if (currentNode != null) { //continue old flow 
			
			predecessor = currentEntry;
			
			if (nextNode instanceof StartNode) { // node is composed node -> new stack
				stack = currentEntry;
			} else { //normal node
				stack = currentEntry.getStack();
			}
		} 
		
		INodeData nodeData = flow.getDataForNode(nextNode);
		
		PathEntry newEntry = new PathEntry(predecessor, stack, nodeData, edge);
		
		if (currentEntry != null) { //entry for this flow already in pathends
			replacePathEnd(theCase, currentEntry, newEntry);
		} else { //new flow
			addPathEnd(theCase, newEntry);
		}
		
		
		return newEntry;
		
		
	}




	private void addPathEnd(XPSCase theCase, PathEntry newEntry) {
		log("Adding new PathEnd for node: " + newEntry.getNodeData().getNode());
		FlowData flow = getFlowData(theCase);
		
		List<PathEntry> pathEnds = flow.getPathEnds();
		pathEnds.add(newEntry);
		log("PathEnds after: " + pathEnds);
		
		newEntry.getNodeData().addSupport(newEntry);
	}


	private void replacePathEnd(XPSCase theCase, PathEntry currentEntry, PathEntry newEntry) {
		
		log("Replacing PathEnd '" + currentEntry + "' by '" + newEntry + "'.");

		FlowData flow = getFlowData(theCase);
		List<PathEntry> pathEnds = flow.getPathEnds();
		
		log("PathEnds before: " + pathEnds);
		
		boolean remove = pathEnds.remove(currentEntry);
		
		//Exception: fork? (would fail after first successor)
//		Assert.assertTrue("Predecessor '" + currentEntry + "' not found in PathEnds: " + pathEnds, remove);
		
		addPathEnd(theCase, newEntry);
		
	}
	


	/**
	 * Continues the flow from the supplied entry on. 
	 * At first the next node is selected by {@link #selectNextEdge(INode)}.
	 * If the next node is the current node flow execution stalls.
	 * Otherwise:
	 * If the next node is not yet active in the case {@link INodeData#isActive()},
	 * it is activated. Otherwise flow execution stalls.  
	 * @param theCase 
	 * @param startEntry
	 */
	private void flow(XPSCase theCase, PathEntry startEntry) {

		FlowData flow = getFlowData(theCase);
		log("Start flowing from node: " + startEntry.getNodeData().getNode());

		PathEntry currentEntry = startEntry;
		while (true) {
			INode currentNode = currentEntry.getNodeData().getNode();
			
			IEdge edge = selectNextEdge(theCase, currentNode);
			
			if (edge == null) { //no edge to take
				log("Staying in Node: " + currentNode);
				return;
			} else if (currentNode instanceof SnapshotNode){
				takeSnapshot(startEntry);
			}
			
			fireEdge(edge, flow);
			
			INodeData nextNodeData = flow.getDataForNode(edge.getEndNode());
			
			if (nextNodeData.isActive()) {
				//TODO what's next??? just stall?
				log("Stop flowing. Node is already active: " + currentEntry.getNodeData().getNode());
				
				return;
			}
			else {
				currentEntry = followEdge(theCase, currentNode, currentEntry, edge);
			}
 			
		}
//		log("Finished flowing.");
		
		
	}
	
	
	/**
	 * Takes a snapshot of the System when leaving the node that belongs to the given entry.
	 * @param entry
	 */
	private void takeSnapshot(PathEntry entry) {
//		Assert.assertTrue(entry.getNodeData().getNode() instanceof SnapshotNode);
		log("Taking snapshot at: " + entry.getNodeData().getNode());
		
		
	}



	/**
	 * Activates the given node coming from the given {@link PathEntry}.
	 * Steps:
	 * 1. Sets the node to active.
	 * 2. Conducts its action 
	 * 3. Add {@link PathEntry} for node.  
	 * @param theCase 
	 * @param currentNode TODO
	 * @param entry the pathentry from where to activate the node
	 * @param edge the egde to take
	 * @return nextNode
	 */
	private PathEntry followEdge(XPSCase theCase, INode currentNode, PathEntry entry, IEdge edge) {
		
		FlowData flow = getFlowData(theCase);
		INode nextNode = edge.getEndNode();
		INodeData nextNodeData = flow.getDataForNode(nextNode);
		
		log("Following edge '" + edge +"' to node '" + nextNode +"'.");
		
//		Assert.assertFalse(nextNodeData.isActive());
		if (nextNodeData.isActive())
			log("Node is already active: " + nextNode, Level.SEVERE);
		
		nextNodeData.setActive(true);
		
		RuleAction action = nextNodeData.getNode().getAction();
		
		doAction(theCase, action);
		
		
		PathEntry newPathEntry = addPathEntryForNode(theCase, currentNode, entry, edge);

		return newPathEntry;
		
		
	}

	private void doAction(XPSCase theCase, RuleAction action) {
		log("Starting action: " + action);
		action.doIt(theCase);
	}



	/**
	 * Selects appropriate successor of {@code node} according to the current state of the case.
	 * @param theCase 
	 * @param node
	 * 
	 * @return
	 */
	private IEdge selectNextEdge(XPSCase theCase, INode node) {
		
		FlowData flow = getFlowData(theCase);
		
		INodeData Node = flow.getDataForNode(node);
		Node.setActive(true);
		
		Iterator<IEdge> edges = node.getOutgoingEdges().iterator();
		
		while (edges.hasNext()) {
			
			IEdge edge = edges.next();
			
			try {
				if (edge.getCondition().eval(theCase)) {
					
					//ausser fork
					//disable for debugging
					//assertOtherEdgesFalse(theCase, edges); //all other edges' predicates must be false
					
					return edge;
					
				}
			} catch (NoAnswerException e) {
				// TODO 
			} catch (UnknownAnswerException e) {
				// TODO 
			}
		}
		
//		throw new UnsupportedOperationException("can not stay in node '" + nodeDeclaration + "'");
		
		log("No edge to take.");
		return null;
		
		
	}

	private void fireEdge(IEdge edge, FlowData flow) {
		log("Firing Edge: " + edge);
		IEdgeData edgeData = flow.getDataForEdge(edge);
		edgeData.fire(); 
		
	}
	
	

	private void assertOtherEdgesFalse(XPSCase theCase, Iterator<IEdge> edges) throws NoAnswerException, UnknownAnswerException {
		while (edges.hasNext()) { 
			if (edges.next().getCondition().eval(theCase)) {
				throw new IllegalStateException("");
			}
			
		}
	}


	

	@Override
	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
	
		log("Start propagating: " + changes);
		
		FlowData flow = getFlowData(theCase);
		
		for (PathEntry entry : new ArrayList<PathEntry>(flow.getPathEnds())) {
			
			maintainTruth(theCase, entry,  changes);
			
		}
		
		
		for (PathEntry entry : new ArrayList<PathEntry>(flow.getPathEnds())) {
			
			flow(theCase, entry);
		}
		log("Finished propagating.");
		
	}
	


	
	private void maintainTruth(XPSCase theCase, PathEntry startEntry, Collection<PropagationEntry> changes) {

		log("Start maintaining truth.");
		
		FlowData flow = getFlowData(theCase);
		PathEntry earliestWrongPathEntry = null;
		PathEntry entry = startEntry;
		
		while (entry != null) {
			
			IEdge edge = entry.getEdge();
			
			boolean eval;
			
			try {
				eval = edge.getCondition().eval(theCase);
				
			} catch (NoAnswerException e) {
				eval = false;
			} catch (UnknownAnswerException e) {
				eval = false;
			}
			
			if (!eval) {
				
				log("Edge is no longer true: " + edge);
				earliestWrongPathEntry = entry;
			}
			
			entry = entry.getPath();
		}
		
		if (earliestWrongPathEntry != null)
			// incoming edge to  earliest wrong entry is now false, so collapse back to its predecessor
			collapsePathUntilEntry(theCase, startEntry, flow, earliestWrongPathEntry.getPath()); 
		else
			log("No TMS necessary");
		
		log("Finished maintaining truth.");
		
	}

	private void collapsePathUntilEntry(XPSCase theCase, PathEntry startEntry,
			FlowData flow, PathEntry endEntry) {
		
		log("Collapsing path from '" + startEntry + "' back to " + endEntry);
		
		int counter = 0;
		PathEntry currentEntry = startEntry;
		
		while (currentEntry != endEntry) {
			IEdgeData edgeData = flow.getDataForEdge(currentEntry.getEdge());
			edgeData.unfire();
			
			INodeData data = currentEntry.getNodeData();
			data.setActive(false);
			data.removeSupport(currentEntry);
			
			undoAction(theCase, data.getNode().getAction());
			
			currentEntry = currentEntry.getPath();
			
			if (counter++ > 250)
				log("Endloss loop in collapsePath? Trying to reach '" + endEntry + "' starting from '" + startEntry + "'. Now being at '" + currentEntry + "'.");
			
		}
		replacePathEnd(theCase, startEntry, endEntry);
		
	}

	private void undoAction(XPSCase theCase, RuleAction action) {
		log("Undoing action: " + action);
		action.undo(theCase);
	}
	
	private void log(String message) {
		log(message, Level.INFO);
	}

	private void log(String message, Level level) {
//		Logging.getInstance().log(level, message, FlowchartPlugin.class.getSimpleName());
		Logger.getLogger(getClass().getName()).log(level, message);
	}



	@Override
	public DiagnosisState getState(XPSCase theCase, Diagnosis theDiagnosis) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public boolean isContributingToResult() {
		// TODO Auto-generated method stub
		return false;
	}


	
//	@Override
//	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
		

		
//		Set<IEdgeData> nowTrueEdges = new HashSet<IEdgeData>();
//		Set<IEdgeData> nowFalseEdges = new HashSet<IEdgeData>();
//		
//		for (PropagationEntry entry : changes) { //for all changed objects
//			
//			
//			NodeAndEdgeSet set = (NodeAndEdgeSet) entry.getObject().getKnowledge(FluxSolver.class, DIAFLUX).get(0);
//			
//			//check all edges where it is used...
//			for (IEdge edge : set.getEdges()) {
//				
//				IEdgeData edgeData = flow.getDataForEdge(edge);
//				if (edgeData.hasFired()) { //if the edge had fired
//					
//					try {
//						boolean eval = edge.getCondition().eval(theCase);
//						if (!eval) { //... and is no longer
//							nowFalseEdges.add(edgeData); //...put it in set NF
//						}
//					} catch (NoAnswerException e) { // if edge had fired and now has no answer anymore, it is now false
//						nowFalseEdges.add(edgeData);
//					} catch (UnknownAnswerException e) { //TODO right? There could be an "unknown"-Edge that was taken
//						nowFalseEdges.add(edgeData);
//					}
//				} else { //edge had not fired...
//					
//					try {
//						boolean eval = edge.getCondition().eval(theCase);
//						if (eval) {//...but is now
//							nowTrueEdges.add(edgeData); // put it in set NT
//						}
//					} catch (NoAnswerException e) { // an edge with no answer is not true
//					} catch (UnknownAnswerException e) { //TODO right? There could be an "unknown"-Edge that has to be taken
//					}
//					
//				}
//			}
//			
//			
//		}
//		for (IEdgeData edgeData : nowFalseEdges) {
//				
//				edgeData.unfire();
//				
//				
//				
//				
//			}
//	}
	
	
	
	
	
	

}

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
import de.d3web.kernel.dynamicObjects.XPSCaseObject;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.PropagationEntry;
import de.d3web.kernel.psMethods.diaFlux.actions.CallFlowAction;
import de.d3web.kernel.psMethods.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.kernel.psMethods.diaFlux.flow.EndNode;
import de.d3web.kernel.psMethods.diaFlux.flow.Flow;
import de.d3web.kernel.psMethods.diaFlux.flow.FlowData;
import de.d3web.kernel.psMethods.diaFlux.flow.FlowFactory;
import de.d3web.kernel.psMethods.diaFlux.flow.FlowSet;
import de.d3web.kernel.psMethods.diaFlux.flow.IEdge;
import de.d3web.kernel.psMethods.diaFlux.flow.IEdgeData;
import de.d3web.kernel.psMethods.diaFlux.flow.INode;
import de.d3web.kernel.psMethods.diaFlux.flow.INodeData;
import de.d3web.kernel.psMethods.diaFlux.flow.SnapshotNode;
import de.d3web.kernel.psMethods.diaFlux.flow.StartNode;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;

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
		
		if (!isFlowCase(theCase))
			return;
		
		log("Initing FluxSolver with case: " + theCase);
		
		Flow flowDeclaration = getStartFlowDeclaration(theCase);
		
		if (flowDeclaration == null) {
			log("No start flow found.", Level.WARNING);
			return;
		}
		
		INode startNode = flowDeclaration.getStartNodes().get(0);
//		System.out.println(flowDeclaration);
//		System.out.println("!!!!" + startNode.hashCode());
		
		insertNewPath(theCase, startNode);
		
//		System.out.println(flowDeclaration);
//		System.out.println("!!!!" + startNode.hashCode());
	}

	private PathEntry insertNewPath(XPSCase theCase, INode startNode) {
		IEdge edge = FlowFactory.getInstance().createEdge("Pseudostart", null, startNode, ConditionTrue.INSTANCE);
		
		IEdgeData edgeData = (IEdgeData) edge.createCaseObject(theCase);
		
		return addPathEntryForNode(theCase, null, edgeData);
	}
	
	// remove when Flowchart is indicated by rule. Just for testing
	private Flow getStartFlowDeclaration(XPSCase theCase) {
		List list = (List)theCase.getKnowledgeBase().getKnowledge(FluxSolver.class, DIAFLUX);
		
		if (list == null || list.isEmpty())
			return null;
		else 
			return ((FlowSet) list.get(0)).getByName("Main");
	}
	
	private boolean isFlowCase(XPSCase theCase) {
		
		List knowledge = (List) theCase.getKnowledgeBase().getKnowledge(FluxSolver.class, FluxSolver.DIAFLUX);
		if (knowledge == null)
			return false;
		
		FlowSet flowSet = (FlowSet) knowledge.get(0);
		
		return flowSet != null;
	}

	private DiaFluxCaseObject getFlowData(XPSCase theCase) {
		
		FlowSet flowSet = getFlowSet(theCase);

		return (DiaFluxCaseObject) theCase.getCaseObject(flowSet);
	}

	private FlowSet getFlowSet(XPSCase theCase) {
		
		FlowSet flowSet = (FlowSet) ((List) theCase.getKnowledgeBase().getKnowledge(FluxSolver.class, FluxSolver.DIAFLUX)).get(0);
		
		return flowSet;
	}
	
	
	/**
	 * Adds a path entry for the current node. Predecessor's entry is removed by this method.
	 * @param theCase 
	 * @param currentNode  
	 * @param currentEntry
	 * @param nextNode

	 * @return the new {@link PathEntry} for node
	 */
	private PathEntry addPathEntryForNode(XPSCase theCase, PathEntry currentEntry, IEdgeData edgeData) {
		
		INode currentNode;
		
		if (currentEntry != null)
			currentNode = currentEntry.getNode();
		else
			currentNode = null;
		
		INode nextNode = edgeData.getEdge().getEndNode(); 

		log("Adding PathEntry for Node' " + nextNode + "' as successor of '" + currentNode + "'.");
		
		
		// both are null if a new flow is started (at first start, after snapshot, (after fork?))
		PathEntry stack;
		PathEntry predecessor;
		
		if (currentNode instanceof SnapshotNode) {
			//starts new flow -> stack = null, pred = null 
			stack = null;
			predecessor = null;
			
		} else { //continue old flow 
			
//			if (currentNode == null)
//				log("IllegalState in addPathEntryForNode", Level.SEVERE);
			
			predecessor = currentEntry;
			
			if (nextNode instanceof StartNode) { // node is composed node -> new stack
				stack = currentEntry;
			} else { //normal node
				stack = currentEntry.getStack();
			}
		} 

		INodeData nodeData = getNodeData(nextNode, theCase);
		
		PathEntry newEntry = new PathEntry(predecessor, stack, nodeData, edgeData);
		
		if (currentEntry != null) { //entry for this flow already in pathends
			replacePathEnd(theCase, currentEntry, newEntry);
		} else { //new flow
			addPathEnd(theCase, newEntry);
		}
		
		return newEntry;
		
	}

	private void addPathEnd(XPSCase theCase, PathEntry newEntry) {
		log("Adding new PathEnd for node: " + newEntry.getNode());
		DiaFluxCaseObject caseObject = getFlowData(theCase);
		
		List<PathEntry> pathEnds = caseObject.getPathEnds();
		pathEnds.add(newEntry);
		log("PathEnds after: " + pathEnds);
		
		newEntry.getNodeData().addSupport(newEntry);
	}


	private void replacePathEnd(XPSCase theCase, PathEntry currentEntry, PathEntry newEntry) {
		
		log("Replacing PathEnd '" + currentEntry + "' by '" + newEntry + "'.");

		List<PathEntry> pathEnds = getFlowData(theCase).getPathEnds();
		
		log("PathEnds before: " + pathEnds);
		
		
		//Exception: fork? (would fail after first successor)
		boolean remove = pathEnds.remove(currentEntry);

		if (!remove)
			throw new IllegalStateException("Predecessor '" + currentEntry + "' not found in PathEnds: " + pathEnds);
		
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

		log("Start flowing from node: " + startEntry.getNode());

		PathEntry currentEntry = startEntry;
		 
		while (true) {
			
			IEdge edge = selectNextEdge(theCase, currentEntry);
			
			if (edge == null) { //no edge to take
				log("Staying in Node: " + currentEntry.getNode());
				return;
			} 
			
			fireEdge(edge, theCase);
			
			INodeData nextNodeData = getNodeData(edge.getEndNode(), theCase);
			
			if (nextNodeData.isActive()) {
				//what's next??? just stall?
				log("Stop flowing. Node is already active: " + currentEntry.getNode());
				return;
			}
			
			currentEntry = followEdge(theCase, currentEntry, edge);
			
			if (edge.getEndNode().getAction() instanceof CallFlowAction) {
				callFlow(edge.getEndNode(), currentEntry, theCase);	
				return;
			} else if (edge.getEndNode() instanceof EndNode) {
				currentEntry = returnFromFlow((EndNode) edge.getEndNode(), currentEntry, theCase);
				//TODO remove after debug phase
				if (currentEntry == null)
					return;
				
			} 
			
 			
		}
		
		
	}
	
	
	private PathEntry returnFromFlow(EndNode endNode, PathEntry currentEntry,
			XPSCase theCase) {
		
//		INode callFlowNode = currentEntry.getStack().getNode();
//		
//		IEdge edge = FlowFactory.getInstance().createEdge("RETURN", endNode, callFlowNode, ConditionTrue.INSTANCE);
		
		return null;
		
	}

	private void callFlow(INode subNode, PathEntry currentEntry, XPSCase theCase) {
		
		getFlowData(theCase).setContinueFlowing(true);
		CallFlowAction action = (CallFlowAction) subNode.getAction();
		
		INode startNode = findStartNode(action, theCase);
		

		insertNewPath(theCase, startNode);
		
	}

	
	/**
	 * returns the StartNode that is called by the supplied action
	 */
	private INode findStartNode(CallFlowAction action, XPSCase theCase) {
		String flowName = action.getFlowName();
		String startNodeName = action.getStartNodeName();
		
		FlowSet flowSet = getFlowSet(theCase);
		
		Flow subflow = flowSet.getByName(flowName);
		List<INode> startNodes = subflow.getStartNodes();
		
		for (INode iNode : startNodes) {
			if (((StartNode)iNode).getName().equalsIgnoreCase(startNodeName)) {
				return iNode;
				
			}
		}
		
		log("Startnode '" + startNodeName + "' of flow '" + flowName +"' not found.", Level.SEVERE);
		return null;
		
	}

//	private PathEntry callFlow(INode subNode, PathEntry currentEntry, XPSCase theCase) {
//		
//		CallFlowAction action = (CallFlowAction) subNode.getAction();
//		
//		INode startNode = findStartNode(action, theCase);
//		
//		
//		IEdge edge = FlowFactory.getInstance().createEdge("CALL", subNode, startNode, ConditionTrue.INSTANCE);
//		
//		return addPathEntryForNode(theCase, startNode, currentEntry, edge);
//	}
	
	/**
	 * Takes a snapshot of the System when leaving the node that belongs to the given entry.
	 * @param entry
	 */
	private void takeSnapshot(PathEntry entry) {
//		Assert.assertTrue(entry.getNodeData().getNode() instanceof SnapshotNode);
		log("Taking snapshot at: " + entry.getNode());
		
		
	}



	/**
	 * Activates the given node coming from the given {@link PathEntry}.
	 * Steps:
	 * 1. Sets the node to active.
	 * 2. Conducts its action 
	 * 3. Add {@link PathEntry} for node.  
	 * @param theCase 
	 * @param currentNode 
	 * @param entry the pathentry from where to activate the node
	 * @param edge the egde to take
	 * @return nextNode
	 */
	private PathEntry followEdge(XPSCase theCase, PathEntry entry, IEdge edge) {
		
		
		INode nextNode = edge.getEndNode();
		INodeData nextNodeData = getNodeData(nextNode, theCase);
		
		log("Following edge '" + edge +"' to node '" + nextNode +"'.");
		
//		Assert.assertFalse(nextNodeData.isActive());
		if (nextNodeData.isActive()) {
			log("Node is already active: " + nextNode, Level.SEVERE);
			return null; //TODO correct?
		}
		
		RuleAction action = nextNodeData.getNode().getAction();
		
		doAction(theCase, action);
		
		PathEntry newPathEntry = addPathEntryForNode(theCase, entry, getEdgeData(edge, theCase));

		return newPathEntry;
		
		
	}

	private INodeData getNodeData(INode nextNode, XPSCase theCase) {
		DiaFluxCaseObject caseObject = getFlowData(theCase);
		
		FlowData flowData = caseObject.getFlowDataFor(nextNode.getFlow().getId());
		
		INodeData dataForNode = flowData.getDataForNode(nextNode);
		return dataForNode;
	}
	
	private IEdgeData getEdgeData(IEdge edge, XPSCase theCase) {
		DiaFluxCaseObject caseObject = getFlowData(theCase);
		
		FlowData flowData = caseObject.getFlowDataFor(edge.getEndNode().getFlow().getId());
		
		
		IEdgeData dataForEdge = flowData.getDataForEdge(edge);
		return dataForEdge;
	}

	private void doAction(XPSCase theCase, RuleAction action) {
		log("Starting action: " + action);
		
		if (action instanceof CallFlowAction)
			;
		else 
			action.doIt(theCase);
	}



	/**
	 * Selects appropriate successor of {@code node} according to the current state of the case.
	 * @param theCase 
	 * @param currentEntry
	 * 
	 * @return
	 */
	private IEdge selectNextEdge(XPSCase theCase, PathEntry currentEntry) {
		
		INode node = currentEntry.getNode();
//		INodeData nodeData = getNodeData(node, theCase);
//		nodeData.setActive(true);
		
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
			} catch (UnknownAnswerException e) {
			}
		}
		
//		throw new UnsupportedOperationException("can not stay in node '" + nodeDeclaration + "'");
		
		log("No edge to take from node:" + node);
		return null;
		
		
	}
	

	private void fireEdge(IEdge edge, XPSCase theCase) {
		log("Firing Edge: " + edge);
		IEdgeData edgeData = getEdgeData(edge, theCase);
		edgeData.fire(); 
		
	}

//	private void assertOtherEdgesFalse(XPSCase theCase, Iterator<IEdge> edges) throws NoAnswerException, UnknownAnswerException {
//		while (edges.hasNext()) { 
//			if (edges.next().getCondition().eval(theCase)) {
//				throw new IllegalStateException("");
//			}
//			
//		}
//	}


	

	@Override
	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
		
		if (!isFlowCase(theCase))
			return;
		
	
		log("Start propagating: " + changes);
		
		DiaFluxCaseObject caseObject = getFlowData(theCase);
		caseObject.setContinueFlowing(true);
		
		//repeat until no new PathEnds are inserted. (As iteration happens over new lists)
		while (caseObject.isContinueFlowing()) {
			caseObject.setContinueFlowing(false);
			
//			System.out.println("**********" + caseObject.getPathEnds() + "@" + caseObject.hashCode());
			
			for (PathEntry entry : new ArrayList<PathEntry>(caseObject.getPathEnds())) {
				
				maintainTruth(theCase, entry,  changes);
				
			}
			
			for (PathEntry entry : new ArrayList<PathEntry>(caseObject.getPathEnds())) {
				
				flow(theCase, entry);
				
			}
			
		}
		log("Finished propagating.");
		
	}
	


	
	private void maintainTruth(XPSCase theCase, PathEntry startEntry, Collection<PropagationEntry> changes) {

		log("Start maintaining truth.");
		
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
			
			
			//TODO !!!check support from other edges
			//something like !eval && entry.getNodeData().getReferenceCounter() == 0
			// if in between there are subpathes that have support by other nodes
			// will they be collapsed too???
			// TODO ? search for and collapse only completely wrong subpathes??
			if (!eval) {
				
				log("Edge is no longer true: " + edge);
				earliestWrongPathEntry = entry;
			}
			
			entry = entry.getPath();
		}
		
		if (earliestWrongPathEntry != null) {
			// incoming edge to  earliest wrong entry is now false, so collapse back to its predecessor
			collapsePathUntilEntry(theCase, startEntry, earliestWrongPathEntry.getPath());
		} else
			log("No TMS necessary");
		
		log("Finished maintaining truth.");
		
	}

	private void collapsePathUntilEntry(XPSCase theCase, PathEntry startEntry,
			 PathEntry endEntry) {
		
		log("Collapsing path from '" + startEntry + "' back to " + endEntry);
		
		int counter = 0;
		PathEntry currentEntry = startEntry;
		
		while (currentEntry != endEntry) {
			
			IEdgeData edgeData = currentEntry.getEdgeData(); 
			edgeData.unfire();
			
			INodeData data = currentEntry.getNodeData();
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
		return theDiagnosis.getState(theCase, PSMethodHeuristic.class);
	}
	
	
	@Override
	public boolean isContributingToResult() {
		// TODo Auto-generated method stub
		return false;
	}



}

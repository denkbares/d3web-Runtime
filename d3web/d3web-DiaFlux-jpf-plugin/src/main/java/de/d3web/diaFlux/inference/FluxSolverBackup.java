/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.diaFlux.inference;


/**
 * 
 * @author Reinhard Hatko
 * @created: 10.09.2009
 * 
 */
public class FluxSolverBackup
// implements PSMethod
{

	// public static final MethodKind DIAFLUX = new MethodKind("DIAFLUX");
	//
	// private static FluxSolverBackup instance;
	//	
	// public FluxSolverBackup() {
	// instance = this;
	// }
	//	
	// /**
	// * @return the instance
	// */
	// public static FluxSolverBackup getInstance() {
	// return instance;
	// }
	//
	// @Override
	// public void init(Session theCase) {
	//
	// if (!isFlowCase(theCase)) return;
	//
	// log("Initing FluxSolver with case: " + theCase);
	//		
	// Rule rule = new Rule("FCIndication_", FluxSolverBackup.class);
	//		
	// rule.setAction(new IndicateFlowAction("Car Diagnosis", "Car Diagnosis"));
	// rule.setCondition(new CondAnd(new ArrayList<Condition>()));
	// rule.check(theCase);
	//
	// }
	//
	// public static void indicateFlowFromAction(Session session, StartNode
	// startNode, ISupport support) {
	// getNodeData(startNode, session).addSupport(support);
	//
	// addPathEntryForNode(session, null, startNode, support);
	// }
	//	
	// public static void indicateFlowFromNode(Session session, INode
	// composedNode, StartNode startNode, ISupport support) {
	//		
	// PathEntry pathEntry = findPathEndOfNode(session, composedNode);
	// getNodeData(startNode, session).addSupport(support);
	// addPathEntryForNode(session, pathEntry, startNode, support);
	// }
	//
	// private static PathEntry findPathEndOfNode(Session session, INode node) {
	// List<PathEntry> pathends = getFlowData(session).getPathEnds();
	//		
	// for (PathEntry pathEntry : pathends) {
	// if (pathEntry.getNode() == node)
	// return pathEntry;
	// }
	//		
	// log("No PathEnd found for Node: " + node, Level.SEVERE);
	// return null;
	// }
	//
	// public static boolean isFlowCase(Session theCase) {
	//
	// FlowSet flowSet = getFlowSet(theCase);
	//
	// return flowSet != null && !flowSet.getFlows().isEmpty();
	// }
	//
	// public static DiaFluxCaseObject getFlowData(Session theCase) {
	//
	// FlowSet flowSet = getFlowSet(theCase);
	//
	// return (DiaFluxCaseObject) theCase.getCaseObject(flowSet);
	// }
	//
	// public static FlowSet getFlowSet(Session theCase) {
	//
	// return getFlowSet(theCase.getKnowledgeBase());
	//
	// }
	//
	// public static void addFlow(Flow flow, KnowledgeBase base, String title) {
	//
	// List ks = (List) base.getKnowledge(FluxSolverBackup.class,
	// FluxSolverBackup.DIAFLUX);
	//
	// FlowSet flowSet;
	// if (ks == null) {
	// flowSet = new FlowSet(title);
	// base.addKnowledge(FluxSolverBackup.class, flowSet,
	// FluxSolverBackup.DIAFLUX);
	//
	// }
	// else {
	// flowSet = (FlowSet) ks.get(0);
	// }
	//
	// flowSet.put(flow);
	//
	// }
	//
	// public static FlowSet getFlowSet(KnowledgeBase knowledgeBase) {
	//
	// List knowledge = (List)
	// knowledgeBase.getKnowledge(FluxSolverBackup.class,
	// FluxSolverBackup.DIAFLUX);
	//
	// if (knowledge == null) return null;
	//
	// return (FlowSet) knowledge.get(0);
	//
	// }
	//
	// /**
	// * Adds a path entry for the current node. Predecessor's entry is removed
	// by
	// * this method.
	// *
	// * @param theCase
	// * @param currentEntry
	// * @param nextNode
	// * @param currentNode
	// * @return the new {@link PathEntry} for node
	// */
	// private static PathEntry addPathEntryForNode(Session theCase, PathEntry
	// currentEntry, INode nextNode, ISupport support) {
	//
	//		
	// INode currentNode;
	//
	// //is null for newly started flows
	// if (currentEntry != null) {
	// currentNode = currentEntry.getNode();
	// } else {
	// currentNode = null;
	// }
	//
	// log("Adding PathEntry for Node' " + nextNode + "' as successor of '"
	// + currentNode + "'.");
	//
	// // both are null if a new flow is started (at first start, after
	// // snapshot, (after fork?))
	// PathEntry stack;
	// PathEntry predecessor;
	//
	// if (currentNode instanceof SnapshotNode) {
	// // starts new flow -> stack = null, pred = null
	// stack = null;
	// predecessor = null;
	//
	// }
	// else { // continue old flow
	//
	// predecessor = currentEntry;
	//
	// if (nextNode instanceof StartNode) { // node is composed node -> new
	// // stack
	// stack = currentEntry;
	// }
	// else { // normal node
	// stack = currentEntry.getStack();
	// }
	// }
	//
	// INodeData nodeData = getNodeData(nextNode, theCase);
	//		
	// PathEntry newEntry = new PathEntry(predecessor, stack, nodeData,
	// support);
	//
	// if (currentEntry != null) { // entry for this flow already in pathends
	// replacePathEnd(theCase, currentEntry, newEntry);
	// }
	// else { // new flow
	// addPathEnd(theCase, newEntry);
	// }
	//
	// return newEntry;
	//
	// }
	//
	//	
	// private static void addPathEnd(Session theCase, PathEntry newEntry) {
	// getFlowData(theCase).setContinueFlowing(true);
	// log("Adding new PathEnd for node: " + newEntry.getNode());
	// DiaFluxCaseObject caseObject = getFlowData(theCase);
	//
	// List<PathEntry> pathEnds = caseObject.getPathEnds();
	// pathEnds.add(newEntry);
	// log("PathEnds after (" + pathEnds.size() + "): " + pathEnds);
	//
	// newEntry.getNodeData().addSupport(newEntry.getSupport());
	// }
	//
	// private static void replacePathEnd(Session theCase, PathEntry
	// currentEntry, PathEntry newEntry) {
	//
	// log("Replacing PathEnd '" + currentEntry + "' by '" + newEntry + "'.");
	//
	// List<PathEntry> pathEnds = getFlowData(theCase).getPathEnds();
	//
	// log("PathEnds before (" + pathEnds.size() + "): " + pathEnds);
	//
	// // Exception: fork? (would fail after first successor)
	// boolean remove = pathEnds.remove(currentEntry);
	//
	// if (!remove) throw new IllegalStateException("Predecessor '" +
	// currentEntry
	// + "' not found in PathEnds: " + pathEnds);
	//
	// if (newEntry != null) {
	// addPathEnd(theCase, newEntry);
	// }
	// else {
	// System.out.println("+++++TODO in FluxSolver.replacePathEnd()");
	// }
	//
	// }
	//
	// /**
	// * Continues the flow from the supplied entry on. At first the next node
	// is
	// * selected by {@link #selectNextEdge(INode)}. If the next node is the
	// * current node flow execution stalls. Otherwise: If the next node is not
	// * yet active in the case {@link INodeData#isActive()}, it is activated.
	// * Otherwise flow execution stalls.
	// *
	// * @param theCase
	// * @param startEntry
	// */
	// private void flow(Session theCase, PathEntry startEntry) {
	//
	// log("Start flowing from node: " + startEntry.getNode());
	//
	// PathEntry currentEntry = startEntry;
	//
	// // while (true) {
	// while (currentEntry != null) {
	//
	// IEdge edge = selectNextEdge(theCase, currentEntry);
	//
	// if (edge == null) { // no edge to take
	// log("Staying in Node: " + currentEntry.getNode());
	// return;
	// }
	//
	// log("Following edge '" + edge + "'.");
	//
	// currentEntry = followEdge(theCase, currentEntry, edge);
	//
	// }
	//
	// }
	//
	// /**
	// * Activates the given node coming from the given {@link PathEntry}.
	// Steps:
	// * 1. Sets the node to active. 2. Conducts its action 3. Add
	// * {@link PathEntry} for node.
	// *
	// * @param theCase
	// * @param currentNode
	// * @param entry the pathentry from where to activate the node
	// * @param edge the egde to take
	// * @return nextNode
	// */
	// private PathEntry followEdge(Session theCase, PathEntry entry, IEdge
	// edge) {
	//
	// INode nextNode = edge.getEndNode();
	// INodeData nextNodeData = getNodeData(nextNode, theCase);
	// NodeSupport support = new NodeSupport(entry.getNode(), edge);
	//		
	// if (nextNodeData.isActive()) {
	// log("Node is already active: " + nextNode, Level.INFO);
	// nextNodeData.addSupport(support); //add support from current path
	// return null; // TODO correct?
	// }
	//
	// doAction(theCase, nextNode);
	//
	// if (!(nextNode.getAction() instanceof ComposedNodeAction)) {
	//		
	// return addPathEntryForNode(theCase, entry, nextNode, support);
	// }
	//
	// return null;
	// // return newPathEntry;
	//
	// }
	//
	// public static INodeData getNodeData(INode nextNode, Session theCase) {
	// DiaFluxCaseObject caseObject = getFlowData(theCase);
	//
	// FlowData flowData =
	// caseObject.getFlowDataFor(nextNode.getFlow().getId());
	//
	// INodeData dataForNode = flowData.getDataForNode(nextNode);
	// return dataForNode;
	// }
	//
	// private void doAction(Session session, INode node) {
	// log("Doing action: " + node.getAction());
	// node.getAction().doIt(session, node, this);
	// }
	//	
	// public static boolean removeSupport(Session session, INode node, ISupport
	// support) {
	// boolean removed = getNodeData(node, session).removeSupport(support);
	//		
	// if (!removed)
	// System.out.println("Could not find support");
	//		
	// return removed;
	// }
	//	
	//	
	//
	// /**
	// * Selects appropriate successor of {@code node} according to the current
	// * state of the case.
	// *
	// * @param theCase
	// * @param currentEntry
	// *
	// * @return
	// */
	// private static IEdge selectNextEdge(Session theCase, PathEntry
	// currentEntry) {
	//
	// INode node = currentEntry.getNode();
	// // INodeData nodeData = getNodeData(node, theCase);
	// // nodeData.setActive(true);
	//
	// Iterator<IEdge> edges = node.getOutgoingEdges().iterator();
	//
	// while (edges.hasNext()) {
	//
	// IEdge edge = edges.next();
	//
	// try {
	// if (edge.getCondition().eval(theCase)) {
	// return edge;
	// }
	// }
	// catch (NoAnswerException e) {
	// }
	// catch (UnknownAnswerException e) {
	// }
	// }
	//
	// // throw new UnsupportedOperationException("can not stay in node '" +
	// // nodeDeclaration + "'");
	//
	// log("No edge to take from node:" + node);
	// return null;
	//
	// }
	//
	// // private void assertOtherEdgesFalse(Session theCase, Iterator<IEdge>
	// // edges) throws NoAnswerException, UnknownAnswerException {
	// // while (edges.hasNext()) {
	// // if (edges.next().getCondition().eval(theCase)) {
	// // throw new IllegalStateException("");
	// // }
	// //
	// // }
	// // }
	//
	// @Override
	// public void propagate(Session theCase, Collection<PropagationEntry>
	// changes) {
	//
	// if (!isFlowCase(theCase)) return;
	//
	// log("Start propagating: " + changes);
	//
	// // checkFCIndications(changes, theCase);
	//
	// DiaFluxCaseObject caseObject = getFlowData(theCase);
	// caseObject.setContinueFlowing(true);
	//
	// // repeat until no new PathEnds are inserted. (As iteration happens over
	// // new lists)
	// while (caseObject.isContinueFlowing()) {
	// caseObject.setContinueFlowing(false);
	//
	// for (PathEntry entry : new
	// ArrayList<PathEntry>(caseObject.getPathEnds())) {
	//
	// maintainTruth(theCase, entry, changes);
	//
	// }
	//
	// for (PathEntry entry : new
	// ArrayList<PathEntry>(caseObject.getPathEnds())) {
	//
	// flow(theCase, entry);
	//
	// }
	//
	// }
	// log("Finished propagating.");
	//
	// }
	//
	// private static void checkFCIndications(Collection<PropagationEntry>
	// changes, Session theCase) {
	//
	// for (PropagationEntry entry : changes) {
	//
	// TerminologyObject object = entry.getObject();
	// KnowledgeSlice knowledge = ((NamedObject)
	// object).getKnowledge(FluxSolverBackup.class,
	// MethodKind.FORWARD);
	// if (knowledge != null) {
	// RuleSet rs = (RuleSet) knowledge;
	// for (Rule rule : rs.getRules()) {
	// rule.check(theCase);
	// }
	// }
	// }
	// }
	//
	// private void maintainTruth(Session theCase, PathEntry startEntry,
	// Collection<PropagationEntry> changes) {
	//
	// log("Start maintaining truth.");
	//
	// PathEntry earliestWrongPathEntry = null;
	// PathEntry entry = startEntry;
	//
	// while (entry != null) {
	//
	// INodeData nodeData = entry.getNodeData();
	// List<ISupport> supports = nodeData.getSupports();
	//			
	// for (ISupport support : supports.toArray(new ISupport[0])) {
	// if (!support.isValid(theCase)) {
	// log("Support is no longer valid: " + support);
	// nodeData.removeSupport(support);
	// }
	// }
	//			
	//
	// // TODO !!!check support from other edges
	// // something like !eval && entry.getNodeData().getReferenceCounter()
	// // == 0
	// // if in between there are subpathes that have support by other
	// // nodes
	// // will they be collapsed too???
	// // TODO ? search for and collapse only completely wrong subpathes??
	// if (!nodeData.isActive()) {
	//				
	// earliestWrongPathEntry = entry;
	// }
	//
	// entry = entry.getPath();
	// }
	//
	// if (earliestWrongPathEntry != null) {
	// // incoming edge to earliest wrong entry is now false, so collapse
	// // back to its predecessor
	// collapsePathUntilEntry(theCase, startEntry,
	// earliestWrongPathEntry.getPath());
	// }
	// else {
	// log("No TMS necessary");
	// }
	//
	// log("Finished maintaining truth.");
	//
	// }
	//
	// private void collapsePathUntilEntry(Session theCase, PathEntry
	// startEntry,
	// PathEntry endEntry) {
	//
	// log("Collapsing path from '" + startEntry + "' back to " + endEntry);
	//
	// int counter = 0;
	// PathEntry currentEntry = startEntry;
	//
	// while (currentEntry != endEntry) {
	//
	// INodeData data = currentEntry.getNodeData();
	// data.removeSupport(currentEntry.getSupport());
	//			
	// //if node is no longer active (=supported), undo action
	// if (!data.isActive())
	// undoAction(theCase, data.getNode());
	//
	// currentEntry = currentEntry.getPath();
	//
	// if (counter++ > 250)
	// log("Endloss loop in collapsePath? Trying to reach '" + endEntry
	// + "' starting from '" + startEntry + "'. Now being at '"
	// + currentEntry + "'.");
	//
	// }
	// replacePathEnd(theCase, startEntry, endEntry);
	//
	// }
	//
	// private void undoAction(Session session, INode node) {
	// log("Undoing action: " + node);
	// node.getAction().undo(session, node, this);
	// }
	//
	// private static void log(String message) {
	// log(message, Level.INFO);
	// }
	//
	// private static void log(String message, Level level) {
	// Logger.getLogger(FluxSolverBackup.class.getName()).log(level, message);
	// }
	//
	// @Override
	// public boolean isContributingToResult() {
	// return false;
	// }
	//
	// @Override
	// public Fact mergeFacts(Fact[] facts) {
	// // diaflux does not derive own facts
	// return Facts.mergeError(facts);
	// }

}

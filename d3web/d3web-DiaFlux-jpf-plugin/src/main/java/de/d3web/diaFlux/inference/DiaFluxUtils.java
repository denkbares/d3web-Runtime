package de.d3web.diaFlux.inference;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.FlowSet;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.INodeData;
import de.d3web.diaFlux.flow.StartNode;

/**
 * @author Reinhard Hatko
 * 
 *         Created: 07.08.2010
 */
public class DiaFluxUtils {

	private DiaFluxUtils() {
	}

	public static FlowSet getFlowSet(KnowledgeBase knowledgeBase) {

		List knowledge = (List) knowledgeBase.getKnowledge(FluxSolver.class,
				FluxSolver.DIAFLUX);

		if (knowledge == null) return null;

		return (FlowSet) knowledge.get(0);

	}

	public static INodeData getNodeData(INode node, Session session) {
		DiaFluxCaseObject caseObject = getFlowData(session);
		return caseObject.getNodeData(node);
	}

	public static FlowSet getFlowSet(Session theCase) {

		return getFlowSet(theCase.getKnowledgeBase());

	}

	public static boolean isFlowCase(Session theCase) {

		if (theCase == null) return false;

		FlowSet flowSet = getFlowSet(theCase);

		return flowSet != null && !flowSet.getFlows().isEmpty();
	}

	public static DiaFluxCaseObject getFlowData(Session theCase) {

		FlowSet flowSet = getFlowSet(theCase);

		return (DiaFluxCaseObject) theCase.getCaseObject(flowSet);
	}

	public static void addFlow(Flow flow, KnowledgeBase base, String title) {

		List ks = (List) base.getKnowledge(FluxSolver.class, FluxSolver.DIAFLUX);

		FlowSet flowSet;
		if (ks == null) {
			flowSet = new FlowSet(title);
			base.addKnowledge(FluxSolver.class, flowSet, FluxSolver.DIAFLUX);

		}
		else {
			flowSet = (FlowSet) ks.get(0);
		}

		flowSet.put(flow);

	}

	/**
	 * returns the StartNode that is called by the supplied action
	 */
	public static StartNode findStartNode(Session theCase, String flowName, String startNodeName) {

		FlowSet flowSet = getFlowSet(theCase);

		Flow subflow = flowSet.getByName(flowName);

		if (subflow == null) {
			Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
					("Flowchart '" + flowName + "' not found."));
			return null;
		}

		List<StartNode> startNodes = subflow.getStartNodes();

		for (StartNode iNode : startNodes) {
			if (iNode.getName().equalsIgnoreCase(startNodeName)) {
				return iNode;

			}
		}

		Logger.getLogger(DiaFluxUtils.class.getName()).log(Level.SEVERE,
				("Startnode '" + startNodeName + "' of flow '" + flowName + "' not found."));
		return null;

	}

}

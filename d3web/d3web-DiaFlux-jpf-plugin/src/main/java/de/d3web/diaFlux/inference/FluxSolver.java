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

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;
import de.d3web.diaFlux.IndicateFlowAction;
import de.d3web.diaFlux.flow.DiaFluxCaseObject;
import de.d3web.diaFlux.flow.INode;
import de.d3web.diaFlux.flow.INodeData;
import de.d3web.diaFlux.flow.ISupport;
import de.d3web.diaFlux.flow.StartNode;

/**
 * 
 * @author Reinhard Hatko
 * @created: 10.09.2009
 * 
 */
public class FluxSolver implements PSMethod {

	public static final MethodKind DIAFLUX = new MethodKind("DIAFLUX");

	public FluxSolver() {
	}

	@Override
	public void init(Session theCase) {

		if (!DiaFluxUtils.isFlowCase(theCase)) return;

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				("Initing FluxSolver with case: " + theCase));

		Rule rule = new Rule("FCIndication_", FluxSolver.class);

		rule.setAction(new IndicateFlowAction("Car Diagnosis", "Car Diagnosis"));
		rule.setCondition(new CondAnd(new ArrayList<Condition>()));
		rule.check(theCase);

	}

	public static void indicateFlowFromAction(Session session, StartNode startNode, ISupport support) {
		INodeData nodeData = DiaFluxUtils.getNodeData(startNode, session);

		boolean active = nodeData.isActive();

		FluxSolver.addSupport(session, startNode, support);

		if (!active) { // if node was not active before adding support, start
						// new path
			DiaFluxCaseObject flowData = DiaFluxUtils.getFlowData(session);
			flowData.addPath(session, startNode, support);

		}

	}

	public static boolean removeSupport(Session session, INode node, ISupport support) {
		INodeData nodeData = DiaFluxUtils.getNodeData(node, session);
		boolean removed = nodeData.removeSupport(support);

		if (!removed) {
			Logger.getLogger(FluxSolver.class.getName()).log(Level.SEVERE,
					"Could not remove support '" + support + "' from node '" + node + "'.");

		}

		return removed;
	}

	public static boolean addSupport(Session session, INode node, ISupport support) {
		INodeData nodeData = DiaFluxUtils.getNodeData(node, session);
		boolean added = nodeData.addSupport(session, support);

		if (!added) {
			Logger.getLogger(FluxSolver.class.getName()).log(Level.SEVERE,
					"Could not add support '" + support + "' to node '" + node + "'.");

		}

		return added;
	}

	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {

		if (!DiaFluxUtils.isFlowCase(session)) return;

		DiaFluxCaseObject caseObject = DiaFluxUtils.getFlowData(session);

		if (caseObject.checkPropagationTime(session)) {

		}

		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				("Start propagating: " + changes));

		// checkFCIndications(changes, theCase);

		int i = 0;
		boolean continueFlowing = true;
		while (continueFlowing) {
			i++;
			continueFlowing = false;

			for (IPath path : new ArrayList<IPath>(caseObject.getPathes())) {

				continueFlowing |= path.propagate(session, changes);

				// if path collapsed completely, remove it.
				if (path.isEmpty()) caseObject.removePath(path);
			}

		}
		Logger.getLogger(FluxSolver.class.getName()).log(Level.INFO,
				"Finished propagating after " + i + " iterations.");

	}

	private static void checkFCIndications(Collection<PropagationEntry> changes, Session theCase) {

		for (PropagationEntry entry : changes) {

			TerminologyObject object = entry.getObject();
			KnowledgeSlice knowledge = ((NamedObject) object).getKnowledge(FluxSolver.class,
					MethodKind.FORWARD);
			if (knowledge != null) {
				RuleSet rs = (RuleSet) knowledge;
				for (Rule rule : rs.getRules()) {
					rule.check(theCase);
				}
			}
		}
	}

	@Override
	public boolean isContributingToResult() {
		return false;
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		// diaflux does not derive own facts
		return Facts.mergeError(facts);
	}

}

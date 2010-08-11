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

	private static FluxSolver instance;
	static int instances = 0;
	
	public FluxSolver() {
		instance = this;
		System.out.println("FluxSolver.FluxSolver(): " + ++instances);
	}
	
	/**
	 * @return the instance
	 */
	public static FluxSolver getInstance() {
		return instance;
	}

	@Override
	public void init(Session theCase) {

		if (!DiaFluxUtils.isFlowCase(theCase)) return;

		log("Initing FluxSolver with case: " + theCase);
		
		Rule rule = new Rule("FCIndication_", FluxSolver.class);
		
		rule.setAction(new IndicateFlowAction("Car Diagnosis", "Car Diagnosis"));
		rule.setCondition(new CondAnd(new ArrayList<Condition>()));
		rule.check(theCase);

	}

	public static void indicateFlowFromAction(Session session, StartNode startNode, ISupport support) {
		INodeData nodeData = DiaFluxUtils.getNodeData(startNode, session);
		
		boolean active = nodeData.isActive();
		
		nodeData.addSupport(session, support); //add support
		
		if (!active) { // if node was not active before adding support, start new path
			DiaFluxCaseObject flowData = DiaFluxUtils.getFlowData(session);
			flowData.addPath(session, startNode, support);
			
		}
		
	}
	
	public static void indicateFlowFromNode(Session session, INode composedNode, StartNode startNode, ISupport support) {
		DiaFluxUtils.getNodeData(startNode, session).addSupport(session, support);
		DiaFluxUtils.getFlowData(session).addPath(session, startNode, support);
		
		
	}


	public static boolean removeSupport(Session session, INode node, ISupport support) {
		boolean removed = DiaFluxUtils.getNodeData(node, session).removeSupport(support);
		
		if (!removed)
			System.out.println("Could not find support");
		
		return removed;
	}
	
	

	@Override
	public void propagate(Session theCase, Collection<PropagationEntry> changes) {

		if (!DiaFluxUtils.isFlowCase(theCase)) return;

		log("Start propagating: " + changes);

		checkFCIndications(changes, theCase);

		DiaFluxCaseObject caseObject = DiaFluxUtils.getFlowData(theCase);

		boolean continueFlowing = true;
		
		while (continueFlowing) {

			continueFlowing = false;
			
			for (Path path : new ArrayList<Path>(caseObject.getPathes())) {

				continueFlowing |= path.propagate(theCase, changes);
				
				if (path.isEmpty())
					caseObject.removePath(path);
			}

		}
		log("Finished propagating.");

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
	
	static void log(String message) {
		log(message, Level.INFO);
	}

	private static void log(String message, Level level) {
		Logger.getLogger(FluxSolver.class.getName()).log(level, message);
	}

}

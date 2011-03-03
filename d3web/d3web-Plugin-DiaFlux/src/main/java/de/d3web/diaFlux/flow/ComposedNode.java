/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.diaFlux.flow;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.diaFlux.inference.CallFlowAction;
import de.d3web.diaFlux.inference.DiaFluxUtils;
import de.d3web.diaFlux.inference.FluxSolver;

/**
 * 
 * @author Reinhard Hatko
 * @created 10.08.10
 */
public class ComposedNode extends AbstractNode {

	private final Condition precondition = new ComposedNodePrecondition();
	private final CallFlowAction action;

	public ComposedNode(String id, String name, CallFlowAction action) {
		super(id, name);
		this.action = action;

	}

	@Override
	public void execute(Session session, FlowRun run) {
		action.doIt(session, run, session.getPSMethodInstance(FluxSolver.class));

	}

	@Override
	public void retract(Session session, FlowRun run) {
		action.undo(session, run, session.getPSMethodInstance(FluxSolver.class));
	}

	// TODO: vb: add method getCalledFlow() -> Flow
	public String getCalledFlowName() {
		return action.getFlowName();
	}

	// TODO: vb: add method getCalledStartNode -> StartNode
	public String getCalledStartNodeName() {
		return action.getStartNodeName();
	}

	@Override
	public Condition getEdgePrecondition() {
		return precondition;
	}

	/**
	 * 
	 * @author Reinhard Hatko
	 * @created 03.03.2011
	 */
	private final class ComposedNodePrecondition implements Condition {


		@Override
		public Collection<? extends TerminologyObject> getTerminalObjects() {
			return Collections.emptyList();
		}

		@Override
		public boolean eval(Session session) throws NoAnswerException, UnknownAnswerException {
			List<FlowRun> runs = DiaFluxUtils.getDiaFluxCaseObject(session).getRuns();

			for (FlowRun run : runs) {

				Collection<Node> startNodes = run.getStartNodes();
				// if we are a start node, we can fire
				if (startNodes.contains(ComposedNode.this)) return true;
				// only other composed notes using the same flowcharts may
				// be blocked
				Flow flow = DiaFluxUtils.getFlowSet(session).getByName(action.getFlowName());
				for (Node node : flow.getNodes()) {
					if (startNodes.contains(node)) {
						return false;
					}
				}
			}

			return true;
		}

		@Override
		public Condition copy() {
			return this;
		}
	}


}

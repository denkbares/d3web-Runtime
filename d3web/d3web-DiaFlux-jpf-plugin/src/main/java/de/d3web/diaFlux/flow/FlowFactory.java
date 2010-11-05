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

package de.d3web.diaFlux.flow;

import java.util.ArrayList;
import java.util.List;

import de.d3web.abstraction.ActionSetValue;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSAction;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.diaFlux.ConditionTrue;
import de.d3web.diaFlux.inference.FluxSolver;

/**
 *
 * @author Reinhard Hatko
 *
 */
public final class FlowFactory {

	private static final FlowFactory instance;

	static {
		instance = new FlowFactory();
	}

	public static FlowFactory getInstance() {
		return instance;
	}

	private FlowFactory() {

	}

	public Flow createFlow(String id, String name, List<INode> nodes, List<IEdge> edges) {

		Flow flow = new Flow(id, name, nodes, edges);

		for (IEdge edge : edges) {

			Condition condition = edge.getCondition();

			if (condition != ConditionTrue.INSTANCE) {

				for (NamedObject nobject : condition.getTerminalObjects()) {

					EdgeMap slice = (EdgeMap) nobject.getKnowledge(FluxSolver.class,
							FluxSolver.DIAFLUX);

					if (slice == null) {
						slice = new EdgeMap("EdgeMap" + nobject.getId());
						nobject.addKnowledge(FluxSolver.class, slice, MethodKind.FORWARD);
					}

					slice.addEdge(edge);
				}

			}
		}

		return flow;

	}

	public INode createActionNode(String id, PSAction action) {
		return new ActionNode(id, action.toString(), action);

	}

	public IEdge createEdge(String id, INode startNode, INode endNode, Condition condition) {
		return new Edge(id, startNode, endNode, condition);
	}

	public INode createStartNode(String id, String name) {
		return new StartNode(id, name);
	}

	public INode createEndNode(String id, String name, PSAction action) {

		return new EndNode(id, name, action);
	}

	public INode createComposedNode(String id, String flowName, String startNodeName) {

		return new ComposedNode(id, flowName, startNodeName);
	}

	public INode createCommentNode(String id, String name) {

		return new CommentNode(id, name);
	}

	public INode createSnapshotNode(String id, String name) {
		return new SnapshotNode(id, name);
	}

	// Fix after Refactoring
	public ActionSetValue createSetValueAction() {
		Rule rule = new Rule("FlowchartRule" + System.currentTimeMillis(), FluxSolver.class);

		ActionSetValue action = new ActionSetValue();
		rule.setAction(action);
		rule.setCondition(new CondAnd(new ArrayList()));
		return action;
	}

}

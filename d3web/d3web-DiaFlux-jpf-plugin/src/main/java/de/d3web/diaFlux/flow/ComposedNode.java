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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.diaFlux.CallFlowAction;
import de.d3web.diaFlux.ConditionTrue;
import de.d3web.diaFlux.inference.DiaFluxUtils;

/**
 *
 * @author Reinhard Hatko
 * @created 10.08.10
 */
public class ComposedNode extends ActionNode {

	public ComposedNode(String id, String name, CallFlowAction action) {
		super(id, name, action);
	}


	@Override
	public void takeSnapshot(Session session, SnapshotNode snapshotNode, List<INode> nodes) {

		CallFlowAction action = (CallFlowAction) getAction();
		StartNode startNode = DiaFluxUtils.findStartNode(session,
				action.getFlowName(), action.getStartNodeName());

		// collects all exit nodes in the called flow that are that match an
		// active outgoing egde's condition
		// need to do this before the super call -> reseting of edges
		Collection<INode> exitNodes = findActiveExitNodes(session);

		super.takeSnapshot(session, snapshotNode, nodes);

		// if the start this CN calls is already snapshotted
		// then do nothing
		if (nodes.contains(startNode)) {
			return;
		}

		for (INode exitNode : exitNodes) {
			DiaFluxUtils.getPath(exitNode, session).takeSnapshot(session, snapshotNode, exitNode, nodes);

		}


	}


	private Collection<INode> findActiveExitNodes(Session session) {
		CallFlowAction action = (CallFlowAction) getAction();
		StartNode startNode = DiaFluxUtils.findStartNode(session,
				action.getFlowName(), action.getStartNodeName());
		Flow flow = startNode.getFlow();

		List<INode> result = new ArrayList<INode>();

		for (IEdge edge : getOutgoingEdges()) {

			EdgeData edgeData = DiaFluxUtils.getEdgeData(edge, session);

			// if the edge has not fired, the exit node is not active
			if (!edgeData.hasFired()) continue;

			Condition condition = edge.getCondition();

			// TODO ConditionTrue is set for still unknown conditions like
			// "processed"
			// need to add this here
			if (condition != ConditionTrue.INSTANCE) {
				List<? extends NamedObject> objects = condition.getTerminalObjects();
				QuestionChoice question = (QuestionChoice) objects.get(0);
				Value value = session.getBlackboard().getValue(question);

				// TODO this can be simplified by taking a closer look at the
				// condition
				// get the name of the value there
				if (UndefinedValue.isUndefinedValue(value)) continue;

				MultipleChoiceValue mcValue = (MultipleChoiceValue) value;

				if (mcValue != null) {
					for (ChoiceID id : mcValue.getChoiceIDs()) {
						Choice choice = id.getChoice(question);

						for (EndNode endnode : flow.getExitNodes()) {
							if (endnode.getName().equalsIgnoreCase(choice.getName())) result.add(endnode);

						}

					}
				}


			}

		}

		return result;

	}

}

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
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.MultipleChoiceValue;
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

		super.takeSnapshot(session, snapshotNode, nodes);

		CallFlowAction action = (CallFlowAction) getAction();
		StartNode startNode = DiaFluxUtils.findStartNode(session,
				action.getFlowName(), action.getStartNodeName());

		// StartNodeData nodeData = (StartNodeData)
		// DiaFluxUtils.getNodeData(startNode, session);

		if (nodes.contains(startNode)) return;

		Collection<INode> exitNodes = findExitNodes(session);

		for (INode exitNode : exitNodes) {
			DiaFluxUtils.getPath(exitNode, session).takeSnapshot(session, snapshotNode, exitNode, nodes);

		}

	}

	private Collection<INode> findExitNodes(Session session) {
		CallFlowAction action = (CallFlowAction) getAction();
		StartNode startNode = DiaFluxUtils.findStartNode(session,
				action.getFlowName(), action.getStartNodeName());
		Flow flow = startNode.getFlow();

		List<INode> result = new ArrayList<INode>();

		for (IEdge edge : getOutgoingEdges()) {

			EdgeData edgeData = DiaFluxUtils.getEdgeData(edge, session);

			if (edgeData.hasFired()) {
				Condition condition = edge.getCondition();

				//
				if (condition != ConditionTrue.INSTANCE) {
					List<? extends NamedObject> objects = condition.getTerminalObjects();
					QuestionChoice question = (QuestionChoice) objects.get(0);
					MultipleChoiceValue value = (MultipleChoiceValue) session.getBlackboard().getValue(
							question);

					if (value != null) {
						for (ChoiceID id : value.getChoiceIDs()) {
							Choice choice = id.getChoice(question);

							for (EndNode endnode : flow.getExitNodes()) {
								if (endnode.getName().equalsIgnoreCase(choice.getName())) result.add(endnode);

							}

						}
					}

				}

			}

		}

		return result;

	}

}

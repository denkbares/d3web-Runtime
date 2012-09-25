/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.d3web.test.diaflux.anomalies;

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.flow.ComposedNode;
import de.d3web.diaFlux.flow.Flow;
import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.flow.StartNode;
import de.d3web.test.SimpleTest;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;
import de.d3web.testing.Utils;

public class UnusedStartNodeTest extends SimpleTest {

	@Override
	public Message check(KnowledgeBase kb, String[] args, String[]... ignores) throws InterruptedException {
		String errormsg = "";
		if (null != kb) {
			List<Flow> flowcharts =
					kb.getManager().getObjects(Flow.class);

			List<StartNode> allStartNodes = new LinkedList<StartNode>();
			List<ComposedNode> cNodes = new LinkedList<ComposedNode>();

			// List of all StartNodes except for autostarting Flowchart
			for (Flow flow : flowcharts) {
				if (!flow.isAutostart()) {
					List<StartNode> startNodes = flow.getStartNodes();
					for (StartNode start : startNodes) {
						allStartNodes.add(start);
					}
				}
				Utils.checkInterrupt();
			}

			// List of all ComposedNodes
			for (Flow flow : flowcharts) {
				List<Node> nodes = flow.getNodes();
				for (Node node : nodes) {
					if (node.getClass().equals(ComposedNode.class)) {
						ComposedNode cNode = (ComposedNode) node;
						cNodes.add(cNode);
					}
				}
				Utils.checkInterrupt();
			}

			// Comparing Flowchartcallings with Startnodenames
			for (ComposedNode cNode : cNodes) {
				String flowName = cNode.getCalledFlowName();
				String startName = cNode.getCalledStartNodeName();

				for (int i = 0; i < allStartNodes.size(); i++) {
					StartNode sNode = allStartNodes.get(i);
					if (sNode.getFlow().getName().equals(flowName)) {
						if (sNode.getName().equals(startName)) {
							allStartNodes.remove(sNode);
						}
					}
				}
				Utils.checkInterrupt();
			}
			if (!allStartNodes.isEmpty()) {
				String redStarts = "";
				for (StartNode node : allStartNodes) {
					redStarts += "in " + node.getFlow().getName() + " node " + node.getName();
				}
				errormsg = redStarts + " is redundant";
			}
		}
		if (errormsg.isEmpty()) {
			return SUCCESS;
		}
		return new Message(Type.FAILURE, errormsg);
	}

	@Override
	public String getDescription() {
		return "Finds start nodes (except auto start) that are never called.";
	}

}

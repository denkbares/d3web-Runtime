/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.diaFlux.test;

import java.util.List;

import org.junit.Test;

import de.d3web.diaFlux.flow.Node;
import de.d3web.diaFlux.inference.DiaFluxUtils;

/**
 * 
 * @author Reinhard Hatko
 * @created 03.12.2010
 */
public class SnapshotTMS1Test extends AbstractDiaFluxTest {

	private static final String FILE = "SnapshotTMS1Test.d3web";

	public SnapshotTMS1Test() {
		super(FILE);
	}

	@Test
	public void testSnapshotTMS() throws Exception {

		assertNodeStates(Flow1, start1, nodeQ1);

		// Set QuestOC1 to Answ1
		setChoiceValue(quest1, answer1);

		assertNodeStates(Flow1, start1, nodeQ1, nodeQ2);

		// Set QuestOC2 to Answ2 -> reaches Exit
		setChoiceValue(quest2, answer2);

		assertNodeStates(Flow1, start1, nodeQ1, nodeQ2, exit1);

		// Re-Set QuestOC2 to Answ1 ->reaches snapshot
		setChoiceValue(quest2, answer1);

		assertNodeStates(Flow1, snapshot1, nodeQ3);

		// Set QuestOC3 to Answ1
		setChoiceValue(quest3, answer1);

		assertNodeStates(Flow1, snapshot1, nodeQ3, nodeQ2_2);

		// Set QuestOC2 to Answ3
		setChoiceValue(quest2, answer3);

		assertNodeStates(Flow1, snapshot1, nodeQ3, nodeQ2_2, nodeQ1, nodeQ2);

		// Set QuestOC2 to back to answ1 -> path retracted to nodeQ2_2
		setChoiceValue(quest2, answer1);

		assertNodeStates(Flow1, snapshot1, nodeQ3, nodeQ2_2);

		List<Node> nodes = DiaFluxUtils.getFlowSet(session).get(Flow1).getNodes();

		// TODO could be easier to get the node, having the id
		for (Node node : nodes) {
			if (node.getID().equalsIgnoreCase(nodeQ3)) {
				// node should not have gained ValidSupport from a Snapshot
				// TODO fix undoing of snapshots and activate
				// List<ISupport> supports = DiaFluxUtils.getNodeData(node,
				// session).getSupports();
				// Assert.assertEquals(1, supports.size());
				break;
			}
		}

	}

}

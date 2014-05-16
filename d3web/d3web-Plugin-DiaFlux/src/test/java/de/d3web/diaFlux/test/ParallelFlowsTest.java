/*
 * Copyright (C) 2014 denkbares GmbH, Germany
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

import org.junit.Test;

/**
 * @created Albrecht Striffler (denkbares GmbH)
 */
public class ParallelFlowsTest extends AbstractDiaFluxTest {

	private static final String FILE = "ParallelFlowsTest.d3web";

	public ParallelFlowsTest() {
		super(FILE);
		this.setNodeMatchingMode(MatchingMode.NAME);
	}

	@Test
	public void testFlow() throws Exception {

		assertNodeStates("Flow Start", "Start", "Flow 1");
		assertNodeStates("Flow 1", "Start", "Trigger");

		setNumValue("Trigger", 1);
		setChoiceValue("Mode", "Both");

		assertNodeStates("Flow Start", "Flow 1");
		assertNodeStates("Flow 1", "Snapshot", "Flow 3");
		assertNodeStates("Flow 2a");
		assertNodeStates("Flow 2b");
		assertNodeStates("Flow 3", "Start", "Trigger");
		assertNodeStates("Flow 4");
		assertNodeStates("Flow 5");
		assertNodeStates("Flow 6");

		setNumValue("Trigger", 2);
		assertNodeStates("Flow Start", "Flow 1", "Exit");
		assertNodeStates("Flow 1", "Snapshot", "Flow 3", "Exit");
		assertNodeStates("Flow 2a");
		assertNodeStates("Flow 2b");
		assertNodeStates("Flow 3", "Start", "Trigger", "Exit");
		assertNodeStates("Flow 4");
		assertNodeStates("Flow 5");
		assertNodeStates("Flow 6");
	}

}

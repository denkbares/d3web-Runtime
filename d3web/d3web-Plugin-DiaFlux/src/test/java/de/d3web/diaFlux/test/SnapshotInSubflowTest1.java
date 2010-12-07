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

import org.junit.Test;

/**
 * 
 * @author Reinhard Hatko
 * @created 04.12.2010
 */
public class SnapshotInSubflowTest1 extends AbstractDiaFluxTest {

	private static final String FILE = "SnapshotInSubflowTest1.d3web";
	private static final int TORTURE_LIMIT = 50;

	public SnapshotInSubflowTest1() {
		super(FILE);
	}

	@Test
	public void testSnapshotReachedFirstTime() throws Exception {

		doFirstCycle();

	}

	public void doFirstCycle() {

		assertNodeStates(Flow1, start1, nodeQ1);
		assertNodeStates(Flow2);

		// Quest1 -> Answ1
		setAnswer(quest1, answer1);

		assertNodeStates(Flow1, start1, nodeQ1, composed1);
		assertNodeStates(Flow2, start1, nodeQ2);

		// Quest2 -> Answ1 => Snapshot
		setAnswer(quest2, answer1);

		assertNodeStates(Flow1, composed1);
		assertNodeStates(Flow2, snapshot1, nodeQ3);

		// Quest3 -> Answ1
		setAnswer(quest3, answer1);

		assertNodeStates(Flow1, composed1, nodeQ4);
		assertNodeStates(Flow2, snapshot1, nodeQ3, exit1);

		// Quest4 -> Answ1
		setAnswer(quest4, answer1);

		assertNodeStates(Flow1, composed1, nodeQ4, nodeQ1);
		assertNodeStates(Flow2, snapshot1, nodeQ3, exit1);

		// Quest1 -> Answ1 => Snapshot
		setAnswer(quest1, answer1);

		assertNodeStates(Flow1, composed1);
		assertNodeStates(Flow2, snapshot1, nodeQ3);
	}

	@Test
	public void testSnapshotReachedSecondTime() throws Exception {

		doFirstCycle();

		doSecondCycle();

	}

	public void doSecondCycle() {

		// Quest3 -> Answ1
		setAnswer(quest3, answer1);

		assertNodeStates(Flow1, composed1, nodeQ4, nodeQ1);
		assertNodeStates(Flow2, snapshot1, nodeQ3, exit1);

		// Quest1 -> Answ1 => Snapshot
		setAnswer(quest1, answer1);

		assertNodeStates(Flow1, composed1);
		assertNodeStates(Flow2, snapshot1, nodeQ3);

	}

	@Test
	public void testTorture() throws Exception {
		doFirstCycle();

		for (int i = 0; i < TORTURE_LIMIT; i++) {
			doSecondCycle();
		}

	}

}

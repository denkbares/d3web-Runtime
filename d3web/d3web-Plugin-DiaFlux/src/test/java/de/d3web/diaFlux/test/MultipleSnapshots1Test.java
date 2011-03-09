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
 * @created 07.12.2010
 */
public class MultipleSnapshots1Test extends AbstractDiaFluxTest {

	private static final String FILE = "MultipleSnapshots1Test.d3web";

	public MultipleSnapshots1Test() {
		super(FILE);
	}

	@Test
	public void testFirstCycle() throws Exception {

		doFirstCycle();

	}

	public void doFirstCycle() {
		assertNodeStates(Flow1, start1, nodeQ1);

		// Quest1 -> Answ1 => 2 Snapshots
		setChoiceValue(quest1, answer1);

		assertNodeStates(Flow1, snapshot1, nodeQ2, snapshot2, nodeQ3);

		// Quest2 -> Answ1
		setChoiceValue(quest2, answer1);

		assertNodeStates(Flow1, snapshot1, nodeQ2, nodeQ4, snapshot2, nodeQ3);

		// Quest3 -> Answ1
		setChoiceValue(quest3, answer1);

		assertNodeStates(Flow1, snapshot1, nodeQ2, nodeQ4, snapshot2, nodeQ3);

		// Quest4 -> Answ2
		setChoiceValue(quest4, answer2);

		assertNodeStates(Flow1, snapshot1, nodeQ2, nodeQ4, snapshot2, nodeQ3, exit1);

		// Quest4 -> Answ1 => Snapshot after TMS
		setChoiceValue(quest4, answer1);

		assertNodeStates(Flow1, snapshot3, nodeQ1);
	}

	@Test
	public void testSecondCycle() throws Exception {

		doFirstCycle();
		doSecondCycle();

	}

	@Test
	public void testTorture() throws Exception {
		doFirstCycle();

		for (int i = 0; i < TORTURE_LIMIT; i++) {
			doSecondCycle();
		}

	}

	public void doSecondCycle() {
		// Quest1 -> Answ1 => 2 Snapshots
		setChoiceValue(quest1, answer1);

		assertNodeStates(Flow1, snapshot1, nodeQ2, snapshot2, nodeQ3, nodeQ4);

		// Quest4 -> Answ1 => Snapshot
		setChoiceValue(quest4, answer1);

		assertNodeStates(Flow1, snapshot3, nodeQ1);
	}

}

/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
 * @created 02.03.2011
 */
public class SnapshotSequenceInSubFlowTest extends AbstractDiaFluxTest {

	private static final String FILE = "SnapshotSequenceInSubFlowTest.d3web";

	public SnapshotSequenceInSubFlowTest() {
		super(FILE);
	}

	@Test
	public void testFlow() throws Exception {

		assertNodeStates(Flow1, start1, composed1);
		assertNodeStates(Flow2);
		assertNodeStates(Flow3, start1, nodeQ1);
		assertNodeStates(Flow4);

		// Quest1 -> Answ1 => activate flow2, take snapshot
		setChoiceValue(quest1, answer1);

		assertNodeStates(Flow1, composed1, composed2);
		assertNodeStates(Flow2, snapshot1, exit1);
		assertNodeStates(Flow3, composed1, exit1);
		assertNodeStates(Flow4, start1, nodeQ2);

		// Quest2 -> Answ1 => activate flow2, take snapshot
		setChoiceValue(quest2, answer1);

		assertNodeStates(Flow1, composed2, exit1);
		assertNodeStates(Flow2, snapshot1, exit1);
		assertNodeStates(Flow3);
		assertNodeStates(Flow4, composed1, exit1);

	}

}

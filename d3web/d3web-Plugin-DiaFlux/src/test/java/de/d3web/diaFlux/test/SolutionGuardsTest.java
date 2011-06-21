/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.diaFlux.test;

import org.junit.Test;

import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * 
 * Tests the guards on edges that start at solution nodes, namely "established",
 * "excluded", "confirmed", and "rejected".
 * 
 * @author Reinhard Hatko
 * @created 16.05.2011
 */
public class SolutionGuardsTest extends AbstractDiaFluxTest {

	private static final String FILE = "SolutionGuardsTest.d3web";

	public SolutionGuardsTest() {
		super(FILE);
	}

	@Test
	public void testEstablished() throws Exception {

		assertNodeStates(Flow1, start1, solution1);

		session.getBlackboard().addValueFact(
				new DefaultFact(sol1, new Rating(Rating.State.ESTABLISHED),
						new Object(), PSMethodAbstraction.getInstance()));

		assertNodeStates(Flow1, start1, solution1, exit1);

	}

	@Test
	public void testExcluded() throws Exception {

		assertNodeStates(Flow1, start1, solution1);

		session.getBlackboard().addValueFact(
				new DefaultFact(sol1, new Rating(Rating.State.EXCLUDED),
						new Object(), PSMethodAbstraction.getInstance()));

		assertNodeStates(Flow1, start1, solution1, exit2);

	}

	@Test
	public void testConfirmed() throws Exception {

		assertNodeStates(Flow1, start1, solution1);

		session.getBlackboard().addValueFact(
				new DefaultFact(sol1, new Rating(Rating.State.ESTABLISHED),
						new Object(), PSMethodUserSelected.getInstance()));

		assertNodeStates(Flow1, start1, solution1, exit1, exit3);

	}

	@Test
	public void testRejected() throws Exception {

		assertNodeStates(Flow1, start1, solution1);

		session.getBlackboard().addValueFact(
				new DefaultFact(sol1, new Rating(Rating.State.EXCLUDED),
						new Object(), PSMethodUserSelected.getInstance()));

		assertNodeStates(Flow1, start1, solution1, exit2, "exit4");

	}

}

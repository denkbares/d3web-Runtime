package de.d3web.diaFlux.test;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.Timeout;

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

/**
 * 
 * @author Reinhard Hatko
 * @created 02.03.2011
 */
public class EndlessCircleTest extends AbstractDiaFluxTest {

	@Rule
	public MethodRule timeout = new Timeout(1000);

	private static final String FILE = "EndlessCircleTest.d3web";

	public EndlessCircleTest() {
		super(FILE);
	}

	@Test
	public void testFlow() {

		assertNodeStates(Flow1, comment1, snapshot1, nodeQ6_2);

		assertNumValue(quest6, 15);

		setChoiceValue(quest1, answer1);

		assertNodeStates(Flow1, comment1, snapshot1, nodeQ6_2);

		assertNumValue(quest6, 25);

		setChoiceValue(quest1, answer2);

		assertNodeStates(Flow1, comment1, snapshot1, nodeQ6_2);

		assertNumValue(quest6, 35);

	}

}

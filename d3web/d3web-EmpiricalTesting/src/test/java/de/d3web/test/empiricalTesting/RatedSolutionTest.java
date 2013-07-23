/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.test.empiricalTesting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.d3web.empiricaltesting.RatedSolution;

/**
 * 
 * @author jochenreutelshofer
 * @created 22.07.2013
 */
public class RatedSolutionTest {

	@Test
	public void testEquals() {
		RatedSolution sol1 = new RatedSolution(null, null);
		assertFalse(sol1.equals(null));
		assertFalse(sol1.equals(Boolean.FALSE));
		assertTrue(sol1.equals(sol1));
	}

}

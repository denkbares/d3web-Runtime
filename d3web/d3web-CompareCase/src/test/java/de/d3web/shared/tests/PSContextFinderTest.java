/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
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

package de.d3web.shared.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.d3web.shared.Abnormality;
import de.d3web.shared.PSContextFinder;
import de.d3web.shared.Weight;
import de.d3web.shared.comparators.QuestionComparator;
import de.d3web.shared.comparators.oc.QuestionComparatorYN;

/**
 * Unit test for {@link PSContextFinder}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 28.08.2010
 */
public class PSContextFinderTest {

	private static PSContextFinder finder;

	@Before
	public void setUp() throws Exception {
		finder = PSContextFinder.getInstance();
		// to completley cover the getInstance() method, repeat the method call
		finder = PSContextFinder.getInstance();
	}

	/**
	 * Test method for {@link de.d3web.shared.PSContextFinder#findPSContext(java.lang.Class)}.
	 */
	@Test
	public void testFindPSContext() {
		assertThat(finder.findPSContext(Abnormality.class).equals(
				(new Abnormality()).getProblemsolverContext()), is(true));
		assertThat(finder.findPSContext(Weight.class).equals(
				(new Weight()).getProblemsolverContext()), is(true));
		assertThat(finder.findPSContext(QuestionComparator.class).equals(
				(new QuestionComparatorYN()).getProblemsolverContext()), is(true));
	}

}

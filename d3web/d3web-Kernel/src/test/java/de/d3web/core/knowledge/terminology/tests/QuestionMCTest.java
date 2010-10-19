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

package de.d3web.core.knowledge.terminology.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionMC;

/**
 * Unit test for {@link QuestionMC}
 * 
 * @author Marc-Oliver Ochlast (denkbares GmbH)
 * @created 24.08.2010
 */
public class QuestionMCTest {

	QuestionMC questionMC;

	@Before
	public void setUp() throws Exception {
		questionMC = new QuestionMC("questionMC");
		questionMC.setName("questionMCtext");
	}

	/**
	 * Tests the getAlternatives() method of QuestionMC
	 * 
	 * @see QuestionMC#getAllAlternatives()
	 * 
	 * @created 24.08.2010
	 */
	@Test
	public void testGetAlternatives() {
		// alternatives is null ==> empty list is returned:
		assertThat(questionMC.getAllAlternatives().size(), is(0));
		// now add a new answer alternative
		Choice choice = new Choice("choice");
		questionMC.addAlternative(choice);
		// and assure that getAlternatives() is 1
		assertThat(questionMC.getAllAlternatives().size(), is(1));
	}

	@Test
	public void testToString() {
		// Test the toString() method
		String string = questionMC.toString();
		assertThat(string, notNullValue());
		assertThat(string.length(), is(not(0)));
	}
}

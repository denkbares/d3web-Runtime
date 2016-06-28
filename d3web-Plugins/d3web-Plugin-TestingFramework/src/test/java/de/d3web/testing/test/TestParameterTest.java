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
package de.d3web.testing.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import de.d3web.testing.TestParameter;
import de.d3web.testing.TestParameter.Mode;
import de.d3web.testing.TestParameter.Type;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 17.07.2013
 */
public class TestParameterTest {

	@Test
	public void testBasicFields() {
		String name = "name";
		String description = "description";
		String option1 = "option1";
		String option2 = "option2";
		TestParameter p = new TestParameter(name, Mode.Optional, description, option1, option2);

		assertEquals("\"name\" (Enum, Optional): description", p.toString());
		assertEquals(description, p.getDescription());
		assertEquals(name, p.getName());
		assertEquals(Type.Enum, p.getType());
		assertEquals(Mode.Optional, p.getMode());
		assertEquals(Arrays.asList(new String[] {
				option1, option2 }), Arrays.asList(p.getOptions()));
		assertTrue(p.checkParameterValue(option1));

	}
}

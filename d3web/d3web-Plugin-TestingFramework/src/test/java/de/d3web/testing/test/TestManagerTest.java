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

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import de.d3web.plugin.test.InitPluginManager;
import de.d3web.testing.TestManager;
import de.d3web.testing.TestParameter;

/**
 * 
 * @author jochenreutelshofer
 * @created 17.07.2013
 */
public class TestManagerTest {

	@Test
	public void testPluginRetrieval() {
		try {
			InitPluginManager.init();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		List<String> allTestNames = TestManager.findAllTestNames();
		assertEquals(1, allTestNames.size());
		assertTrue(allTestNames.contains("TestTest"));

		List<de.d3web.testing.Test<?>> allTests = TestManager.findAllTests();
		assertEquals(1, allTests.size());
		assertTrue(allTests.get(0).getName().equals("TestTest"));

		de.d3web.testing.Test<?> test = allTests.get(0);
		List<TestParameter> ignoreSpecification = test.getIgnoreSpecification();
		assertEquals(1, ignoreSpecification.size());

		List<TestParameter> parameterSpecification = test.getParameterSpecification();
		assertEquals(1, parameterSpecification.size());

	}
}

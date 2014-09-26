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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import de.d3web.empiricaltesting.TestPersistence;
import de.d3web.plugin.test.InitPluginManager;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 22.07.2013
 */
public class SequentialTestCaseTest {

	@Test
	public void testPersistenceReadWrite() {
		try {
			InitPluginManager.init();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		KnowledgeBase kb = null;
		try {
			kb = PersistenceManager.getInstance().load(
					new File("./src/test/resources/Car faults diagnosis.d3web"));
			List<SequentialTestCase> loadedCases = TestPersistence.getInstance().loadCases(
					new FileInputStream(new File(
							"./src/test/resources/Demo_-_Test_Cases_testcase-2.xml")), kb);

			TestCase testCase = new TestCase();
			testCase.setKb(kb);
			testCase.setRepository(loadedCases);
			assertTrue(testCase.isConsistent());

			SequentialTestCase sequentialTestCaseA = loadedCases.get(0);
			SequentialTestCase sequentialTestCaseB = sequentialTestCaseA.flatClone();
			SequentialTestCase sequentialTestCaseC = loadedCases.get(1);

			// test equals
			assertFalse(sequentialTestCaseA.equals(null));
			assertFalse(sequentialTestCaseA.equals(Boolean.FALSE));
			assertTrue(sequentialTestCaseA.equals(sequentialTestCaseA));
			assertTrue(sequentialTestCaseA.equals(sequentialTestCaseB));

			// testTo
			assertTrue(sequentialTestCaseA.testTo(sequentialTestCaseA));
			assertTrue(sequentialTestCaseA.testTo(sequentialTestCaseB));
			assertFalse(sequentialTestCaseA.testTo(sequentialTestCaseC));
			assertFalse(sequentialTestCaseA.testTo(null));
			assertFalse(sequentialTestCaseA.testTo(Boolean.FALSE));

			// toString
			String expectedToString = "Leaking air intake system (Demo): < (\n\t"
					+
					"Findings:[Driving = [insufficient power on partial load]]; \n\t"
					+
					"Expected:[Leaking air intake system (500)], []; \n\t"
					+
					"Derived:[]; \n\t"
					+
					")>, < (\n\t"
					+
					"Findings:[Driving = [unsteady idle speed], Driving = [weak acceleration], Fuel = -?-]; \n\t"
					+
					"Expected:[Clogged air filter (SUGGESTED)], []; \n\t"
					+
					"Derived:[]; \n\t"
					+
					")>, < (\n\t"
					+
					"Findings:[Check: Air filter. = ok, Average mileage /100km = 10.0, Real mileage  /100km = 12.0, Driving = [insufficient power on full load]]; \n\t"
					+
					"Expected:[Leaking air intake system (ESTABLISHED)], [Num. Mileage evaluation = 120.0, Mileage evaluation = slightly increased, Driving = [unsteady idle speed, weak acceleration]]; \n\t"
					+
					"Derived:[]; \n\t" +
					")>";
			String actualToString = sequentialTestCaseA.toString();
			assertEquals(
					actualToString,
					expectedToString);
		}
		catch (FileNotFoundException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		catch (XMLStreamException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		catch (IOException e) {
			assertFalse(true);
			e.printStackTrace();
		}
	}
}

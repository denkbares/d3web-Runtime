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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;
import de.d3web.testing.TestResult;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 17.07.2013
 */
public class TestResultTest {

	private static final String KEY = "key";
	private static final String KEY2 = "key2";
	private static final String TESTNAME = "testname";
	private static final String TESTNAME2 = "testname2";
	private static final String CONFIGURATION_PARAMETERS = "some configuration parameters";

	@Test
	public void testToString() {
		TestResult result = createTestResult();
		assertEquals(
				"testname (configuration: some configuration parameters, successes: 0): {key: FAILURE: m1, key2: ERROR: null}",
				result.toString());

		assertTrue(result.hasConfiguration());
	}

	@Test
	public void testExpectedMessageKeySet() {
		TestResult result = createTestResult3();
		Collection<String> testObjectsWithExpectedOutcome = result.getTestObjectsWithExpectedOutcome();

		assertEquals(1, testObjectsWithExpectedOutcome.size());

		assertTrue(testObjectsWithExpectedOutcome.contains(KEY));
	}

	@Test
	public void testCompareTo() {
		TestResult result = createTestResult();
		TestResult result2 = createTestResult2();

		assertTrue(result.compareTo(result2) < 0);

		// result3 with same content as result1
		TestResult result3 = createTestResult();
		assertTrue(result.compareTo(result3) == 0);

	}

	@Test
	public void testEqualsHashcode() {
		TestResult result = createTestResult();
		TestResult result2 = createTestResult2();
		// result3 with same content as result1
		TestResult result3 = createTestResult();

		Set<TestResult> set = new HashSet<>();
		set.add(result);
		assertTrue(set.contains(result3));
		assertFalse(result.equals(result2));
		assertTrue(result.equals(result));
		assertFalse(result.equals(null));
		assertFalse(result.equals(2));
	}

	private static TestResult createTestResult() {
		TestResult result = new TestResult(TESTNAME, new String[] { CONFIGURATION_PARAMETERS });
		result.addUnexpectedMessage(KEY, new Message(Type.FAILURE, "m1"));
		result.addUnexpectedMessage(KEY2, new Message(Type.ERROR));
		return result;
	}

	private static TestResult createTestResult2() {
		TestResult result = new TestResult(TESTNAME2, new String[] { "other config" });
		return result;
	}

	private static TestResult createTestResult3() {
		TestResult result = new TestResult(TESTNAME, new String[] { "third config" });
		result.addExpectedMessage(KEY, new Message(Type.SUCCESS));
		return result;
	}
}

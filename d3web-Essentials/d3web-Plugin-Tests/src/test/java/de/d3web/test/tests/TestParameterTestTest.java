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
package de.d3web.test.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.denkbares.plugin.test.InitPluginManager;
import de.d3web.testing.ArgsCheckResult;
import de.d3web.testing.TestObjectProviderManager;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 13.08.2013
 */
public class TestParameterTestTest {

	void setup() {
		try {
			InitPluginManager.init();
			TestObjectProviderManager.registerTestObjectProvider(new JUnitTestKnowledgeBaseProvider());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testParameters() {

		setup();

		TestParameterTest test = new TestParameterTest();
		ArgsCheckResult checkArgsFail = test.checkArgs(new String[] { "foo" });
		assertTrue(checkArgsFail.hasError());

		TestParameterTest test2 = new TestParameterTest();
		ArgsCheckResult checkArgsTooMany = test2.checkArgs(new String[] {
				TestParameterTest.O1, TestParameterTest.O3 });
		// too many arguments
		assertTrue(checkArgsTooMany.hasError());

		TestParameterTest test3 = new TestParameterTest();
		ArgsCheckResult checkArgsValid = test3.checkArgs(new String[] {
				TestParameterTest.O2 });
		assertFalse(checkArgsValid.hasError());

	}

	@Test
	public void testIgnoreParameters() {
		setup();

		TestParameterTest test = new TestParameterTest();
		ArgsCheckResult checkIgnoreFail = test.checkIgnore(new String[] { "foo" });
		assertTrue(checkIgnoreFail.hasError());

		TestParameterTest test2 = new TestParameterTest();
		ArgsCheckResult checkIgnoreTooMany = test2.checkIgnore(new String[] {
				TestParameterTest.I1, TestParameterTest.I3 });
		// too many arguments
		assertTrue(checkIgnoreTooMany.hasError());

		TestParameterTest test3 = new TestParameterTest();
		ArgsCheckResult checkIgnoreValid = test3.checkArgs(new String[] {
				TestParameterTest.O2 });
		assertFalse(checkIgnoreValid.hasError());

	}

}

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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.d3web.testing.ArgsCheckResult;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 17.07.2013
 */
public class ArgsCheckResultTester {

	@Test
	public void testResultFactories() {
		ArgsCheckResult emptyTestDeclaration = ArgsCheckResult.emptyTestDeclaration();
		assertTrue(emptyTestDeclaration.hasWarning());
		assertTrue(emptyTestDeclaration.hasWarning(0));

		String testName = "testName";
		ArgsCheckResult noTestObjectIdentifier = ArgsCheckResult.noTestObjectIdentifier(testName);
		assertTrue(noTestObjectIdentifier.hasError());
		assertTrue(noTestObjectIdentifier.hasError(0));
		assertTrue(noTestObjectIdentifier.getMessage(0).contains(testName));
		assertTrue(noTestObjectIdentifier.getFirstErrorMessage().contains(testName));

		String identifier = "id";
		ArgsCheckResult invalidTestObjectIdentifier = ArgsCheckResult.invalidTestObjectIdentifier(
				identifier,
				testName);
		assertTrue(invalidTestObjectIdentifier.hasError());
		assertTrue(invalidTestObjectIdentifier.hasError(0));
		assertTrue(invalidTestObjectIdentifier.getMessage(0).contains(identifier));
		assertTrue(invalidTestObjectIdentifier.getMessage(0).contains(testName));

	}
}

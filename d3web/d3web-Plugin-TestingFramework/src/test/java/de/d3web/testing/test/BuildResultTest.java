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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.d3web.testing.BuildResult;
import de.d3web.testing.TestResult;

/**
 * 
 * @author jochenreutelshofer
 * @created 17.07.2013
 */
public class BuildResultTest {

	@Test
	public void testEqualsHashCode() {
		Date buildDate = new Date();
		int buildDuration = 1000;
		ArrayList<TestResult> testResults = new ArrayList<TestResult>();
		testResults.add(new TestResult("testName", new String[] { "config" }));
		BuildResult build1 = BuildResult.createBuildResult(buildDuration, buildDate,
				testResults, 0, false);
		BuildResult build2 = BuildResult.createBuildResult(buildDuration, buildDate,
				testResults, 0, false);

		Set<BuildResult> set = new HashSet<BuildResult>();
		set.add(build1);
		assertTrue(set.contains(build2));
	}
}

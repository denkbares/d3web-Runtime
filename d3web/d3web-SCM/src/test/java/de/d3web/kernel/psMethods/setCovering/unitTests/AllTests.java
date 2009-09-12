/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.kernel.psMethods.setCovering.unitTests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author bates TestClass-container for all SCM-TestCases
 */
public class AllTests extends TestCase {

	public static void main(String[] args) {
		junit.swingui.TestRunner
				.main(new String[]{"de.d3web.kernel.psMethods.setCovering.unitTests.AllTests"});
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Suite for all tests for SCM");
		suite.addTest(SCRelationFactoryTest.suite());
		suite.addTest(PoolTest.suite());
		suite.addTest(SCObjectsTest.suite());
		suite.addTest(SimilarityTest.suite());
		suite.addTest(SCMPersistenceTest.suite());
		suite.addTest(TransitiveClosureTest.suite());
		suite.addTest(TransitivePropagationTest.suite());
		suite.addTest(PropagateTest.suite());
		suite.addTest(HypothesisTest.suite());
		suite.addTest(SortedCollectionsTest.suite());
		suite.addTest(HypothesesGenerationStrategiesTest.suite());
		suite.addTest(HypothesesGenerationTest.suite());
		suite.addTest(CoveringCheckTest.suite());
		suite.addTest(RemoveKnowledgeSliceTest.suite());
		
		return suite;
	}
}

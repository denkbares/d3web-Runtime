/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.persistence.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Container class for all persistence tests. The suite accessable by this class
 * contains all test suites registered here. If main() has been started, all
 * registered tests will be executed. Creation date: (05.09.2000 15:59:01)
 * 
 * @author mike / merz
 */

public class TestAll {

	/**
	 * @return a test suite containing all registered tests Creation date:
	 *         (05.09.2000 16:01:23)
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("Suite for all tests");

		suite.addTest(ActionTest.suite());
		suite.addTest(ConditionTest.suite());
		suite.addTest(SolutionTest.suite());
		suite.addTest(QContainerTest.suite());
		suite.addTest(QuestionTests.suite());
		suite.addTest(CostTest.suite());
		suite.addTest(DCMarkupTest.suite());
		suite.addTest(PropertyTest.suite());
		suite.addTest(RuleComplexTest.suite());
		suite.addTest(KnowledgeBaseExportTest.suite());
		suite.addTest(TestKfz.suite());

		return suite;
	}
}
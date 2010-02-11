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

package de.d3web.persistence.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author merz
 *
 * !!! propertytest missing !!!
 */
public class QuestionTests extends TestCase {

	/**
	 * Constructor for QuestionTests.
	 * @param arg0
	 */
	public QuestionTests(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(QuestionTests.suite());
	}
	
	public static Test suite() {
		de.d3web.core.session.D3WebCase.TRACE = true;
		TestSuite suite = new junit.framework.TestSuite("Suite for question-tests");
		
		suite.addTest(QuestionNumTest.suite());
		suite.addTest(QuestionChoiceTest.suite());
		suite.addTest(QuestionTextTest.suite());
		suite.addTest(QuestionDateTest.suite());
		
		return suite;
	}
}

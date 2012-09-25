/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.testing;

/**
 * Class holding a test instance ready to be executed with all specified
 * declarations.
 * 
 * @author Jochen Reutelshöfer, Volker Belli (denkbares GmbH)
 * @created 30.05.2012
 */
public class TestSpecification<T> {

	private final Test<T> test;
	private final String testObject;
	private final String[] args;
	private final String[][] ignores;

	public TestSpecification(Test<T> test, String testObject, String[] args, String[][] ignores) {
		this.test = test;
		this.args = args;
		this.testObject = testObject;
		this.ignores = ignores;
	}

	public Test<T> getTest() {
		return test;
	}

	public String[] getArguments() {
		return args;
	}

	public String[][] getIgnores() {
		return ignores;
	}

	public String getTestName() {
		return test.getName();
	}

	public String getTestObject() {
		return testObject;
	}

}

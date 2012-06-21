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
 * Interface for tests. Implement this class to create a test to be used within
 * the TestingFramework.
 * 
 * @param <T> type of the test object (e.g., KnowledgeBase)
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 04.05.2012
 */
public interface Test<T> {

	public static final String PLUGIN_ID =
			"d3web-Plugin-TestingFramework";

	public static final String EXTENSION_POINT_ID = "Test";

	/**
	 * The test is implemented within this method. The object to be tested
	 * (testObject) is passed to the execute method by the TestingFramework.
	 * 
	 * @param testObject object to be tested (e.g., a knowledge base)
	 * @param args optional additional config parameters for the test
	 * @return a Message containing information about the outcome of the test
	 * @throws NullPointerException if testObject or args is null
	 */
	Message execute(T testObject, String[] args);

	/**
	 * Checks the config parameters for correctness. If the parameters are
	 * sufficient, they will be passed to the execute method. If they are rated
	 * to be insufficient, the execute method is not called, but a test result
	 * message will be generated accordingly.
	 * 
	 * @param args
	 * @return
	 * @throws NullPointerException if testObject or args is null
	 */
	ArgsCheckResult checkArgs(String[] args);

	/**
	 * Defines the class of the test-object to be tested by the execute method.
	 * This method is used to retrieve the test object from the
	 * TestObjectProvider.
	 * 
	 * @return
	 */
	Class<T> getTestObjectClass();

}

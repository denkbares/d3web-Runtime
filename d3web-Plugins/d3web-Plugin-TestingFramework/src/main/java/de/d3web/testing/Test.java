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

import java.util.List;

/**
 * Interface for tests. Implement this class to create a test to be used within the
 * TestingFramework.
 *
 * @param <T> type of the test object (e.g., KnowledgeBase)
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 04.05.2012
 */
public interface Test<T> {

	String PLUGIN_ID =
			"d3web-Plugin-TestingFramework";

	String EXTENSION_POINT_ID = "Test";

	/**
	 * Returns the name of the test. The implementations may ensure that the name is identical to
	 * the name used in the declaration of the corresponding extension.
	 *
	 * @return the name of this test
	 * @created 15.09.2012
	 */
	String getName();

	/**
	 * The test is implemented within this method. The object to be tested (testObject) is passed to
	 * the execute method by the TestingFramework.
	 *
	 * @param specification the specification of the particular test, including parameters, ignores, ...
	 * @param testObject object to be tested (e.g., a knowledge base)
	 * @return a Message containing information about the outcome of the test
	 * @throws NullPointerException if testObject, args or ignores is null
	 */
	Message execute(TestSpecification<T> specification, T testObject) throws InterruptedException;

	/**
	 * This method is called to calculate a new summary message based in the specified test results
	 * containing the previously created messages of this test. The implementations of this method
	 * shall call TestResult.setSummary(...) to initialize or update the summary before the method
	 * returns. A default implementation of this method can be found at
	 * TestingUtils.updateSummary(...);
	 *
	 * @param specification the specification used for that test
	 * @param result the test result to be evaluated and updated
	 */
	void updateSummary(TestSpecification<?> specification, TestResult result);

	/**
	 * Checks the config parameters for correctness. If the parameters are sufficient, they will be
	 * passed to the execute method. If they are rated to be insufficient, the execute method is not
	 * called, but a test result message will be generated accordingly.
	 *
	 * @param args the test arguments to be checked
	 * @return the check results
	 * @throws NullPointerException if testObject or args is null
	 */
	ArgsCheckResult checkArgs(String[] args);

	/**
	 * Checks the ignore parameters for correctness. The ignore parameters are the parameters
	 * followed by a @ignore tag, defined right after the @test definition. If the parameters are
	 * sufficient, they will be passed to the execute method. If they are rated to be insufficient,
	 * the execute method is not called, but a test result message will be generated accordingly.
	 *
	 * @param args the ignore arguments to be checked
	 * @return the check results
	 * @throws NullPointerException if testObject or args is null
	 */
	ArgsCheckResult checkIgnore(String[] args);

	/**
	 * Defines the class of the test-object to be tested by the execute method. This method is used
	 * to retrieve the test object from the TestObjectProvider.
	 *
	 * @return the class of the test objects to be potentially evaluated by this test
	 */
	Class<T> getTestObjectClass();

	/**
	 * This method returns some documentation or description about the test. The text may contain
	 * html markup and is intended to be shown to the user.
	 *
	 * @return the test description
	 */
	String getDescription();

	/**
	 * Returns the list of expected parameters of this test. This method must not return null. If no
	 * parameters are allowed, the method returns an empty list.
	 *
	 * @return the list of expected parameters
	 * @created 31.07.2012
	 */
	List<TestParameter> getParameterSpecification();

	/**
	 * Returns the list of expected parameters for the "ignore"-definitions of this test. This
	 * method must not return null. If no ignores are allowed, the method returns an empty list.
	 *
	 * @return the list of expected parameters
	 * @created 31.07.2012
	 */
	List<TestParameter> getIgnoreSpecification();
}

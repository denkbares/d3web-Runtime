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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.d3web.testing.Message.Type;

/**
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 04.05.2012
 */
public class TestResult implements Comparable<TestResult> {

	private final String[] configuration;
	private final String testName;

	private final Map<String, Message> unexpectedMessages = Collections.synchronizedMap(new TreeMap<String, Message>());

	private int successfullTestObjectRuns = 0;

	/**
	 * Creates a new TestResult for the specified test with the specified
	 * arguments.
	 * 
	 * @param testName name of the test
	 * @param configuration configuration parameters, if there are any, null or
	 *        empty array otherwise
	 */
	public TestResult(String testName, String[] configuration) {
		this.configuration = Arrays.copyOf(configuration, configuration.length);
		this.testName = testName;
	}

	private TestResult(String testName, String[] configuration, Map<String, Message> unexpectedMessages, int successfulRuns) {
		this(testName, configuration);
		this.unexpectedMessages.putAll(unexpectedMessages);
		this.successfullTestObjectRuns = successfulRuns;
	}

	public static TestResult createTestResult(String testName, String[] configuration, Map<String, Message> unexpectedMessages, int successfulRuns) {
		return new TestResult(testName, configuration, unexpectedMessages, successfulRuns);
	}

	public String getTestName() {
		return testName;
	}

	public boolean isSuccessful() {
		return getType() == Message.Type.SUCCESS;
	}

	/**
	 * Returns the arguments/parameters with which the test was executed.
	 * 
	 * @created 22.05.2012
	 * @return
	 */
	public String[] getConfiguration() {
		return configuration;
	}

	private String getConfigurationString() {
		if (configuration == null) return "";
		StringBuilder result = new StringBuilder();
		for (String item : configuration) {
			if (result.length() > 0) result.append(" ");
			result.append(item);
		}
		return result.toString();
	}

	@Override
	public int compareTo(TestResult tr) {
		if (testName != tr.getTestName()) {
			return testName.compareTo(tr.getTestName());
		}
		return getConfigurationString().compareTo(tr.getConfigurationString());
	}

	/**
	 * Returns if the test result described by this object has a configuration
	 * 
	 * @created 30.05.2011
	 * @return if this result has a configuration
	 */
	public boolean hasConfiguration() {
		return this.configuration != null && !(this.configuration.length == 0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (String configString : configuration) {
			result = prime * result + ((configString == null) ? 0 : configString.hashCode());
		}
		result = prime * result
				+ ((unexpectedMessages == null) ? 0 : unexpectedMessages.hashCode());
		result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
		result = prime * result + successfullTestObjectRuns;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		TestResult other = (TestResult) obj;
		if (configuration == null) {
			if (other.configuration != null) return false;
		}
		else if (!Arrays.equals(configuration, other.configuration)) return false;
		Collection<String> otherTestObjectNames = other.getTestObjectsWithUnexpectedOutcome();
		if (getTestObjectsWithUnexpectedOutcome().size() != otherTestObjectNames.size()) return false;
		if (getType() != other.getType()) return false;
		if (successfullTestObjectRuns != other.getSuccessfullTestObjectRuns()) return false;
		if (!unexpectedMessages.equals(other.unexpectedMessages)) return false;
		return true;
	}

	/**
	 * Returns the number of test objects that have been successfully tested by
	 * this test.
	 * 
	 * 
	 * @created 26.11.2012
	 * @return
	 */
	public int getSuccessfullTestObjectRuns() {
		return successfullTestObjectRuns;
	}

	/**
	 * Summarized type of the contained messages.
	 * 
	 * @created 22.05.2012
	 * @return
	 */
	public Type getType() {
		Type t = Message.Type.SUCCESS;
		for (String testObjectName : this.unexpectedMessages.keySet()) {
			Message test = unexpectedMessages.get(testObjectName);
			if (test == null || test.getType().equals(Type.ERROR)) {
				return Type.ERROR;
			}
			if (test.getType().equals(Type.FAILURE)) {
				t = Type.FAILURE;
			}
		}
		return t;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getTestName() + " (" + getConfigurationString() + "): {");
		boolean first = true;
		for (Entry<String, Message> messageEntry : this.unexpectedMessages.entrySet()) {
			if (first) first = false;
			else result.append(", ");
			result.append(messageEntry.getKey() + ": " + messageEntry.getValue());
		}
		result.append("}");
		return result.toString();
	}

	/**
	 * Adds a test object/message pair to this TestResult. Note: If the type of
	 * the message is "SUCCESS" then the message text will be ignored.
	 * 
	 * @created 14.08.2012
	 * @param testObjectName
	 * @param message
	 */
	public void addUnexpectedMessage(String testObjectName, Message message) {
		if (testObjectName == null) throw new NullPointerException("TestObjectName cannot be null");
		if (message == null) throw new NullPointerException("Message cannot be null");
		if (message.getType().equals(Message.Type.SUCCESS)) {
			throw new InputMismatchException("success logged as full message: "
					+ message.toString());
		}
		this.unexpectedMessages.put(testObjectName, message);
	}

	public Collection<String> getTestObjectsWithUnexpectedOutcome() {
		return unexpectedMessages.keySet();
	}

	public Message getUnexpectedMessageForTestObject(String testObjectName) {
		return unexpectedMessages.get(testObjectName);
	}

	/**
	 * Increments the number of successful test object runs of this test result
	 * 
	 * @created 26.11.2012
	 */
	public void incSuccessfulTestObjectRuns() {
		successfullTestObjectRuns++;
	}
}

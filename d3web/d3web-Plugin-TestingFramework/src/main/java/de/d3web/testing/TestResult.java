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
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 04.05.2012
 */
public class TestResult implements Comparable<TestResult> {

	private final String[] configuration;
	private final String testName;

	private Message summary = null;

	private final Map<String, Message> unexpectedMessages = Collections.synchronizedMap(new TreeMap<String, Message>());
	private final Map<String, Message> expectedMessages = Collections.synchronizedMap(new TreeMap<String, Message>());

	/**
	 * Creates a new TestResult for the specified test with the specified arguments.
	 *
	 * @param testName name of the test
	 * @param configuration configuration parameters, if there are any, null or empty array
	 * otherwise
	 */
	public TestResult(String testName, String[] configuration) {
		if (configuration == null) throw new NullPointerException();
		this.configuration = Arrays.copyOf(configuration, configuration.length);
		this.testName = testName;
	}

	// private TestResult(String testName, String[] configuration, Map<String,
	// Message> unexpectedMessages, int successfulRuns) {
	// this(testName, configuration);
	// this.unexpectedMessages.putAll(unexpectedMessages);
	// for (int i = 0; i < successfulRuns; i++) {
	// expectedMessages.put(generateUnknownTestObjectString(i),
	// Message.SUCCESS);
	// }
	// }

	private TestResult(String testName, String[] configuration, Map<String, Message> unexpectedMessages, Map<String, Message> expectedMessages, Message summary) {
		this(testName, configuration);
		this.unexpectedMessages.putAll(unexpectedMessages);
		this.expectedMessages.putAll(expectedMessages);
		this.summary = summary;
	}

	public static TestResult createTestResult(String testName, String[] configuration, Map<String, Message> unexpectedMessages, int successfulRuns, Message summary) {
		Map<String, Message> expectedMessages = new TreeMap<String, Message>();
		for (int i = 0; i < successfulRuns; i++) {
			expectedMessages.put(generateUnknownTestObjectString(i), Message.SUCCESS);
		}
		return new TestResult(testName, configuration, unexpectedMessages, expectedMessages, summary);
	}

	public static TestResult createTestResult(String testName, String[] configuration, Map<String, Message> unexpectedMessages, Map<String, Message> expectedMessages, Message summary) {
		return new TestResult(testName, configuration, unexpectedMessages, expectedMessages, summary);
	}

	public String getTestName() {
		return testName;
	}

	public boolean isSuccessful() {
		return summary != null && summary.getType() == Message.Type.SUCCESS;
	}

	/**
	 * Returns the arguments/parameters with which the test was executed.
	 *
	 * @return the test configuration (arguments)
	 * @created 22.05.2012
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
	 * @return if this result has a configuration
	 * @created 30.05.2011
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
		result = prime * result + ((getSummary() == null) ? 0 : getSummary().hashCode());
		result = prime * result + getSuccessfullyTestedObjects();
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
		if (getTestObjectsWithUnexpectedOutcome().size() != otherTestObjectNames.size())
			return false;
		if (getSummary() == null) {
			if (other.getSummary() != null) return false;
		}
		else if (!getSummary().equals(other.getSummary())) return false;
		return getSuccessfullyTestedObjects() == other.getSuccessfullyTestedObjects()
				&& unexpectedMessages.equals(other.unexpectedMessages);
	}

	/**
	 * Returns the number of test objects that have been successfully tested by this test.
	 *
	 * @return the number of successfully tested objects
	 * @created 26.11.2012
	 */
	public int getSuccessfullyTestedObjects() {
		return this.expectedMessages.size();
	}

	/**
	 * Gets the summary of the test result.
	 *
	 * @return the summarized messages
	 * @created 22.05.2012
	 */
	public Message getSummary() {
		return summary;
	}

	/**
	 * Sets the summary of this test result.
	 *
	 * @param summary the new summarized type of the messages
	 * @created 22.05.2012
	 */
	public void setSummary(Message summary) {
		this.summary = summary;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String configurationString = getConfigurationString();
		configurationString = configurationString.isEmpty() ? "none" : configurationString;
		result.append(getTestName())
				.append(" (configuration: ").append(configurationString)
				.append(", successes: ").append(getSuccessfullyTestedObjects())
				.append("): {");
		boolean first = true;
		for (Entry<String, Message> messageEntry : this.unexpectedMessages.entrySet()) {
			if (first) first = false;
			else result.append(", ");
			result.append(messageEntry.getKey()).append(": ").append(messageEntry.getValue());
		}
		result.append("}");
		return result.toString();
	}

	/**
	 * Adds a test object/message pair to this TestResult. The message must not have the message
	 * type Type#SUCCESS.
	 *
	 * @param testObjectName the test object name
	 * @param message the fail or error message
	 * @created 14.08.2012
	 */
	public void addUnexpectedMessage(String testObjectName, Message message) {
		if (testObjectName == null) throw new NullPointerException("TestObjectName cannot be null");
		if (message == null) throw new NullPointerException("Message cannot be null");
		if (message.getType().equals(Message.Type.SUCCESS)) {
			throw new IllegalArgumentException("wrong failed/error message type: " + message);
		}
		this.unexpectedMessages.put(testObjectName, message);
	}

	/**
	 * Adds a test object/message pair to this TestResult. The message must be a success message of
	 * type#SUCCESS. Note: The message text of "SUCCESS" message will be ignored.
	 *
	 * @param testObjectName the test object name
	 * @param message the success message
	 * @created 14.08.2012
	 */
	public void addExpectedMessage(String testObjectName, Message message) {
		if (testObjectName == null) throw new NullPointerException("TestObjectName cannot be null");
		if (message == null) throw new NullPointerException("Message cannot be null");
		if (!message.getType().equals(Message.Type.SUCCESS)) {
			throw new IllegalArgumentException("wrong success message type: " + message);
		}
		this.expectedMessages.put(testObjectName, message);
	}

	public Collection<String> getTestObjectsWithUnexpectedOutcome() {
		return unexpectedMessages.keySet();
	}

	public Collection<String> getTestObjectsWithExpectedOutcome() {
		return expectedMessages.keySet();
	}

	public Message getMessageForTestObject(String testObjectName) {
		if (unexpectedMessages.containsKey(testObjectName)) {
			return unexpectedMessages.get(testObjectName);

		}
		else {
			return expectedMessages.get(testObjectName);
		}
	}

	/**
	 * Increments the number of successful test object runs of this test result
	 *
	 * @created 26.11.2012
	 */
	// public void incSuccessfulTestObjectRuns() {
	// this.expectedMessages.put(generateUnknownTestObjectString(expectedMessages.size()),
	// Message.SUCCESS);
	// }
	private static String generateUnknownTestObjectString(int i) {
		return "unknown-TestObject-" + i;
	}
}

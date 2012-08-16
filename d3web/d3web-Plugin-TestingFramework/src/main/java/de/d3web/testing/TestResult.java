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
import java.util.HashMap;
import java.util.Map;

import de.d3web.testing.Message.Type;

/**
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 04.05.2012
 */
public class TestResult implements Comparable<TestResult> {

	private final String[] args;
	private final String testName;

	private final Map<String, Message> messages = new HashMap<String, Message>();

	public TestResult(String testName, String[] arguments) {
		this.args = arguments;
		this.testName = testName;
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
	 * 
	 * @created 22.05.2012
	 * @return
	 */
	public String[] getArguments() {
		return args;
	}

	@Override
	public int compareTo(TestResult tr) {
		if(testName != tr.getTestName()) {
			return testName.compareTo(tr.getTestName());
		}
		return args.toString().compareTo(tr.getArguments().toString());
	}

	/**
	 * Returns if the test result described by this object has a configuration
	 * 
	 * @created 30.05.2011
	 * @return if this result has a configuration
	 */
	public boolean hasConfiguration() {
		return this.args != null && !(this.args.length == 0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((args == null) ? 0 : args.hashCode());
		result = prime * result + ((messages == null) ? 0 : hashCode());
		result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		TestResult other = (TestResult) obj;
		if (args == null) {
			if (other.getArguments() != null) return false;
		}
		else if (! Arrays.equals(args,other.args)) return false;
		Collection<String> otherTestObjectNames = other.getTestObjectNames();
		if (getTestObjectNames().size() != otherTestObjectNames.size()) return false;
		// TODO: compare each message ?!
		if (getType() != other.getType()) return false;
		return true;
	}

	/**
	 * Summarized type of the contained messages.
	 * 
	 * @created 22.05.2012
	 * @return
	 */
	public Type getType() {
		Type t = Message.Type.SUCCESS;
		for (String testObjectName : this.messages.keySet()) {
			Message test = messages.get(testObjectName);
			if (test.getType().equals(Type.ERROR)) {
				return Type.ERROR;
			}
			if (test.getType().equals(Type.FAILURE)) {
				t = Type.FAILURE;
			}
		}
		return t;
	}

	/**
	 * Adds a test object/message pair to this TestResult.
	 * Note: If the type of the message is "SUCCESS" then the message text will be ignored.
	 * 
	 * @created 14.08.2012
	 * @param testObjectName
	 * @param message 
	 */
	public void addMessage(String testObjectName, Message message) {
		this.messages.put(testObjectName, message);
	}

	public Collection<String> getTestObjectNames() {
		return messages.keySet();
	}

	public Message getMessage(String testObjectName) {
		return messages.get(testObjectName);
	}
}

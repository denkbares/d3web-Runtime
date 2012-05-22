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
package cc.denkbares.testing;

import cc.denkbares.testing.Message.Type;

/**
 * 
 * @author jochenreutelshofer
 * @created 04.05.2012
 */
public class TestResultImpl implements TestResult, Comparable<TestResult> {

	private final Message message;
	private final String configuration;
	private final String testName;

	public TestResultImpl(Message message, String testName, String configuration) {
		this.message = message;
		this.configuration = configuration;
		this.testName = testName;
	}

	@Override
	public String getTestName() {
		return testName;
	}

	public boolean isSuccessful() {
		return message.getType() == Message.Type.SUCCESS;
	}

	@Override
	public Message getMessage() {
		return message;
	}

	@Override
	public String getConfiguration() {
		return configuration;
	}

	@Override
	public String toString() {
		return getMessage().toString() + " - " + configuration.toString();
	}

	/**
	 * SUCCESSFUL < FAILED < ERROR
	 */
	@Override
	public int compareTo(TestResult tr) {
		return message.getType().compareTo(tr.getMessage().getType());
	}

	/**
	 * Returns if the test result described by this object has a message
	 * 
	 * @created 30.05.2011
	 * @return if this result has a message
	 */
	public boolean hasMessage() {
		return this.message != null && !(this.message.getText().length() == 0);
	}

	/**
	 * Returns if the test result described by this object has a configuration
	 * 
	 * @created 30.05.2011
	 * @return if this result has a configuration
	 */
	public boolean hasConfiguration() {
		return this.configuration != null && !this.configuration.isEmpty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((message.getType() == null) ? 0 : message.getType().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		TestResult other = (TestResult) obj;
		if (configuration == null) {
			if (other.getConfiguration() != null) return false;
		}
		else if (!configuration.equals(other.getConfiguration())) return false;
		if (message == null) {
			if (other.getMessage() != null) return false;
		}
		else if (!message.equals(other.getMessage())) return false;
		if (message.getType() != other.getMessage().getType()) return false;
		return true;
	}

	@Override
	public Type getType() {
		return message.getType();
	}

}

/*
 * Copyright (C) 2020 denkbares GmbH, Germany
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
import java.util.List;

/**
 * @author Stefan Olbrecht (Service Mate GmbH)
 * @created 20.02.20
 */
public interface ResultSizeTest {

	String WARNING = "warning";
	String GREATER_THAN = ">";
	String SMALLER_THAN = "<";
	String EQUAL = "=";

	String[] comparators = new String[] { EQUAL, SMALLER_THAN, GREATER_THAN };

	default List<TestParameter> getParameters() {
		TestParameter comparator = new TestParameter("comparator", TestParameter.Mode.Mandatory,
				"how to compare the result size against the expected size", comparators);
		TestParameter expectedSize = new TestParameter("expected size", TestParameter.Type.Number,
				TestParameter.Mode.Mandatory, "expected size to compare with");
		TestParameter warning = new TestParameter("warning", TestParameter.Mode.Optional,
				"show warning instead of failure if this test fails", WARNING);
		return Arrays.asList(comparator, expectedSize, warning);
	}

	default Comparator getComparator(String[] args, int startIndex) {
		String comparator = args[startIndex];
		int number = Integer.parseInt(args[startIndex + 1]);

		Message.Type messageTypeTestFailed = Message.Type.FAILURE;
		if (args.length > (startIndex + 2) && args[startIndex + 2] != null && WARNING.equalsIgnoreCase(args[startIndex + 2])) {
			messageTypeTestFailed = Message.Type.WARNING;
		}
		return new Comparator(comparator, number, messageTypeTestFailed);
	}

	default Message getMessageGreater(Message.Type type, int expected, int actual) {
		return new Message(type, "Result size should be greater than: " + expected + " but was: " + actual);
	}

	default Message getMessageSmaller(Message.Type type, int expected, int actual) {
		return new Message(type, "Result size should be smaller than: " + expected + " but was: " + actual);
	}

	default Message getMessageEqual(Message.Type type, int expected, int actual) {
		return new Message(type, "Result size should be: " + expected + " but was: " + actual);
	}

	class Comparator {

		private final String comparator;
		private final int number;
		private final Message.Type messageType;

		public Comparator(String comparator, int number, Message.Type messageType) {
			this.comparator = comparator;
			this.number = number;
			this.messageType = messageType;
		}

		public String getComparator() {
			return comparator;
		}

		public int getNumber() {
			return number;
		}

		public Message.Type getMessageType() {
			return messageType;
		}
	}
}

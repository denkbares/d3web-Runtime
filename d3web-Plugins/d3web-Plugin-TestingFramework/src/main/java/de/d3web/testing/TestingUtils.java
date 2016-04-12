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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;

import de.d3web.testing.Message.Type;

/**
 * General utility class of the Testing Framework.
 *
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 11.06.2012
 */
public class TestingUtils {

	/**
	 * Checks whether the calling thread has been interrupted and throws InterruptedException in
	 * case.
	 *
	 * @throws InterruptedException
	 * @created 16.08.2012
	 */
	public static void checkInterrupt() throws InterruptedException {
		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
	}

	public static Type getSummarizedType(TestResult testResult) {
		// return error if we have no test objects
		if (testResult.getSuccessfullyTestedObjects() == 0
				&& testResult.getTestObjectsWithUnexpectedOutcome().isEmpty()) {
			return Type.ERROR;
		}
		Type t = Message.Type.SUCCESS;
		for (String testObjectName : testResult.getTestObjectsWithUnexpectedOutcome()) {
			Message test = testResult.getMessageForTestObject(testObjectName);
			if (test == null || test.getType().equals(Type.ERROR)) {
				return Type.ERROR;
			}
			if (test.getType().equals(Type.FAILURE)) {
				t = Type.FAILURE;
			}
		}
		return t;
	}

	public static void updateSummary(TestResult result) {
		result.setSummary(new Message(getSummarizedType(result)));
	}

	public static Message createFailure(String failedMessage, Collection<String> erroneousObjects, Class<?> objectClass) {
		ArrayList<MessageObject> messageObject = new ArrayList<MessageObject>();
		for (String object : erroneousObjects) {
			messageObject.add(new MessageObject(object, objectClass));
		}
		String nameList = createTextFromList(erroneousObjects);
		return new Message(Type.FAILURE, failedMessage + "\n" + nameList, messageObject);
	}

	public static String createTextFromList(Collection<String> list) {
		if (list.isEmpty()) return "";

		StringBuilder htmlList = new StringBuilder();
		for (String listItem : list) {
			htmlList.append("* ").append(listItem);
			htmlList.append("\n");
		}
		htmlList.deleteCharAt(htmlList.length() - 1);
		return htmlList.toString();
	}

	/**
	 * Compiles ignores to a list of {@link Pattern}. Patterns are applied case-insensitive.
	 *
	 * @param ignores the patterns as string to compile
	 * @return the list of patterns
	 */
	public static Collection<Pattern> compileIgnores(String[]... ignores) {
		Collection<Pattern> ignorePatterns = new LinkedList<Pattern>();
		for (String[] ignore : ignores) {
			ignorePatterns.add(Pattern.compile(ignore[0], Pattern.CASE_INSENSITIVE));
		}
		return ignorePatterns;
	}

	/**
	 * Checks if a string should be ignored based on a list of {@link Pattern}s.
	 *
	 * @param object the name of the object to test
	 * @param ignorePatterns the ignores
	 * @return s if the object should be ignored
	 * @created 06.03.2013
	 */
	public static boolean isIgnored(String object, Collection<Pattern> ignorePatterns) {
		for (Pattern pattern : ignorePatterns) {
			if (pattern.matcher(object).matches()) return true;
		}
		return false;
	}

	/**
	 * Filters the specified strings (test object names) and returns a new list without the strings
	 * to be ignored according to the ignorePatterns.
	 *
	 * @param objects the test object names
	 * @param ignorePatterns the object name patterns to be ignored
	 * @return a new list, containing only the strings not to be ignored
	 * @created 27.05.2013
	 */
	public static Collection<String> filterIgnored(Collection<String> objects, Collection<Pattern> ignorePatterns) {
		Collection<String> result = new ArrayList<String>(objects);

		for (String string : objects) {

			if (isIgnored(string, ignorePatterns)) {
				result.remove(string);
			}

		}
		return result;
	}
}

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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Class to parse and validate a single test declaration.
 *
 * @author Jochen Reutelshöfer, Volker Belli (denkbares GmbH)
 * @created 11.06.2012
 */
public class TestParser {

	public static final String IGNORE_REGEX = "^\\s*ignore[:\\s]\\s*(.*?)\\s*$";
	public static final Pattern IGNORE_PATTERN = Pattern.compile(IGNORE_REGEX,
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	public static final Pattern PARAMETER_PATTERN = Pattern.compile("(?:[^(\"|\\s)]+|\".+?\")");

	private String declaration;
	private final List<String> ignoreParameters = new LinkedList<String>();
	private ArgsCheckResult parameterCheckResult;
	private final List<ArgsCheckResult> ignoreCheckResults = new LinkedList<ArgsCheckResult>();
	private TestSpecification<?> testSpecification;

	/**
	 * Creates an TestParser object from a test declaration line. The first argument (separated by
	 * whitespace) is the (class) name of the test. Then any further arguments for the test
	 * execution follow while each argument is quoted by double quotes. (The first argument in
	 * quotes will later by used to retrieve the test object from the test object providers.)
	 *
	 * @param testDeclaration the test declaration
	 * @created 13.06.2012
	 */
	@SuppressWarnings({
			"unchecked", "rawtypes" })
	public TestParser(String testDeclaration) {
		// remove end-line comments
		// testDeclaration = testDeclaration.replaceAll("(\\s+|^\\s*)//[^\n\r]*$", "");
		this.declaration = testDeclaration;

		// parse ignores and remove them from test's command line
		Matcher ignoreMatcher = IGNORE_PATTERN.matcher(testDeclaration);
		if (ignoreMatcher.find()) {
			this.declaration = testDeclaration.substring(0, ignoreMatcher.start());
			do {
				int start = ignoreMatcher.start(1);
				int end = ignoreMatcher.end(1);
				ignoreParameters.add(testDeclaration.substring(start, end));
			} while (ignoreMatcher.find());
		}

		// parse test's command line
		List<String> paramters = splitParameters(this.declaration);
		if (paramters.isEmpty()) {
			this.parameterCheckResult = ArgsCheckResult.emptyTestDeclaration();
			return;
		}

		// get the name of the test
		// and remove it from the parameters
		String testName = paramters.remove(0);
		Test<?> test = TestManager.findTest(testName);
		if (test == null) {
			this.parameterCheckResult = ArgsCheckResult.classNotFoundResult(testName);
			return;
		}

		// get the test object identifier
		// and remove it from the parameters
		if (paramters.isEmpty()) {
			this.parameterCheckResult = ArgsCheckResult.noTestObjectIdentifier(testName);
			return;
		}
		// check whether test object identifier string is a valid regex
		String testObject = paramters.remove(0);
		try {
			Pattern.compile(testObject);
		}
		catch (PatternSyntaxException e) {
			// create message and return no executable test
			this.parameterCheckResult = ArgsCheckResult.invalidTestObjectIdentifier(
					testObject, testName);
			return;
		}

		// check arguments and create error messages if necessary
		String[] params = paramters.toArray(new String[paramters.size()]);
		this.parameterCheckResult = test.checkArgs(params);

		// check ignores and create error messages if necessary
		String[][] ignores = new String[ignoreParameters.size()][];
		int index = 0;
		for (String ignore : ignoreParameters) {
			List<String> args = splitParameters(ignore);
			String[] array = args.toArray(new String[args.size()]);
			ignores[index++] = array;
			ignoreCheckResults.add(test.checkIgnore(array));
		}

		this.testSpecification = new TestSpecification(test, testObject, params, ignores);
	}

	/**
	 * Splits the given parameter String into its single parameters. Parameters containing white
	 * spaces have to be quoted, the quotes will not be part of that parameter.
	 *
	 * @param parameters the parameters as a String
	 * @return the single parameters from the given String in a List
	 * @created 23.09.2012
	 */
	public static List<String> splitParameters(String parameters) {
		Matcher matcher = PARAMETER_PATTERN.matcher(parameters);
		List<String> paramters = new ArrayList<String>();
		while (matcher.find()) {
			String parameter = matcher.group();
			if (parameter.startsWith("\"") && parameter.endsWith("\"")) {
				parameter = parameter.substring(1, parameter.length() - 1);
			}
			paramters.add(parameter);
		}
		return paramters;
	}

	/**
	 * Utility method to concatenate test parameters following the same rules by which they are
	 * split.
	 *
	 * @param parameters the parameters to be concatenated
	 * @return the concatenated parameters
	 * @created 23.09.2012
	 */
	public static String concatParameters(String... parameters) {
		return concatParameters(0, parameters);
	}

	/**
	 * Utility method to concatenate test parameters following the same rules by which they are
	 * split.
	 *
	 * @param parameters the parameters to be concatenated
	 * @return the concatenated parameters
	 * @created 23.09.2012
	 */
	public static String concatParameters(int startIndex, String... parameters) {
		StringBuilder concatenated = new StringBuilder();
		boolean first = true;
		for (String parameter : parameters) {
			if (--startIndex >= 0) continue;
			if (first) first = false;
			else concatenated.append(" ");
			boolean quote = parameter.contains(" ");
			if (quote) concatenated.append("\"");
			concatenated.append(parameter);
			if (quote) concatenated.append("\"");
		}
		return concatenated.toString();
	}

	public String getTestDeclaration() {
		return declaration;
	}

	public List<String> getIgnoreCommands() {
		return Collections.unmodifiableList(ignoreParameters);
	}

	public ArgsCheckResult getParameterCheckResult() {
		return parameterCheckResult;
	}

	public List<ArgsCheckResult> getIgnoreCheckResults() {
		return Collections.unmodifiableList(ignoreCheckResults);
	}

	public TestSpecification<?> getTestSpecification() {
		return testSpecification;
	}
}

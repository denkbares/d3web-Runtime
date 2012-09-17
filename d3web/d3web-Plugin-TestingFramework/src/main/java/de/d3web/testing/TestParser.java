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

	private String testCommand;
	private List<String> ignoreCommands = new LinkedList<String>();
	private ArgsCheckResult parameterCheckResult;
	private List<ArgsCheckResult> ignoreCheckResults = new LinkedList<ArgsCheckResult>();
	private ExecutableTest executableTest;

	/**
	 * Creates an ExecutableTest object from a test command line string. The
	 * first argument (separated by whitespace) is the (class) name of the test.
	 * Then any further arguments for the test execution follow while each
	 * argument is quoted by double quotes. (The first argument in quotes will
	 * later by used to retrieve the test object from the test object
	 * providers.)
	 * 
	 * @created 13.06.2012
	 * @param testCommand
	 * @param msgs
	 * @return
	 */
	public TestParser(String testDeclaration) {
		this.testCommand = testDeclaration;

		// parse ignores and remove them from test's command line
		Matcher ignoreMatcher = IGNORE_PATTERN.matcher(testDeclaration);
		if (ignoreMatcher.find()) {
			testCommand = testDeclaration.substring(0, ignoreMatcher.start());
			do {
				int start = ignoreMatcher.start(1);
				int end = ignoreMatcher.end(1);
				ignoreCommands.add(testDeclaration.substring(start, end));
			} while (ignoreMatcher.find());
		}

		// parse test's command line
		List<String> paramters = splitParameter(testCommand);
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
		String[][] ignores = new String[ignoreCommands.size()][];
		int index = 0;
		for (String ingore : ignoreCommands) {
			List<String> args = splitParameter(ingore);
			String[] array = args.toArray(new String[args.size()]);
			ignores[index++] = array;
			ignoreCheckResults.add(test.checkIgnore(array));
		}

		this.executableTest = new ExecutableTest(testName, test, testObject, params, ignores);
	}

	private static List<String> splitParameter(String command) {
		Matcher matcher = PARAMETER_PATTERN.matcher(command);
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

	public String getTestCommand() {
		return testCommand;
	}

	public List<String> getIgnoreCommands() {
		return Collections.unmodifiableList(ignoreCommands);
	}

	public ArgsCheckResult getParameterCheckResult() {
		return parameterCheckResult;
	}

	public List<ArgsCheckResult> getIgnoreCheckResults() {
		return Collections.unmodifiableList(ignoreCheckResults);
	}

	public ExecutableTest getExecutableTest() {
		return executableTest;
	}
}

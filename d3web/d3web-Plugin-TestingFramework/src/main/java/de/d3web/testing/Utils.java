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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 11.06.2012
 */
public class Utils {

	/**
	 * Creates an ExecutableTest object from a test command line string. The
	 * first argument (separated by whitespace) is the (class) name of the test.
	 * Then any further arguments for the test execution follow while each
	 * argument is quoted by double quotes. (The first argument in quotes will
	 * later by used to retrieve the test object from the test object
	 * providers.)
	 * 
	 * @created 13.06.2012
	 * @param command
	 * @param msgs
	 * @return
	 */
	public static ExecutableTest createExecutableTest(String command, List<ArgsCheckResult> msgs) {
		Pattern pattern = Pattern.compile("(?:\\w+|\".+?\")");
		Matcher matcher = pattern.matcher(command);
		if (matcher.find()) {
			// get the name of the test
			String testName = matcher.group();
			// get the parameters of the test
			List<String> testParamters = new ArrayList<String>();
			while (matcher.find()) {
				String parameter = matcher.group();
				if (parameter.startsWith("\"") && parameter.endsWith("\"")) {
					parameter = parameter.substring(1, parameter.length() - 1);
				}
				testParamters.add(parameter);
			}
			Test<?> test = TestManager.findTest(testName);
			if (test != null) {
				String[] args = testParamters.toArray(new String[] {});

				// check arguments and create error messages if
				// necessary
				testArguments(msgs, testName, test, args);
				
				// check whether test object identifier string is a valid regex 
				String testObjectIdentifierString = args[0];
				try {
					Pattern.compile(testObjectIdentifierString);
				} catch (PatternSyntaxException e) {
					// is not a valid regex, create message and return no executable test
					msgs.add(ArgsCheckResult.invalidTestObjectIdentifier(testObjectIdentifierString, testName));
					return null;
				}
				
				return new ExecutableTest(test, args);
			}
			else {
				msgs.add(ArgsCheckResult.classNotFoundResult(testName));
			}
		}

		return null;
	}

	private static void testArguments(List<ArgsCheckResult> msgs, String testName, Test<?> test, String[] args) {
		if(args.length < 1) {
			throw new IllegalArgumentException("At least a test object identifier has to be defined");
		}
		
		args = Arrays.copyOfRange(args, 1, args.length);
		ArgsCheckResult argsCheckResult = test.checkArgs(args);
		msgs.add(argsCheckResult);
	}

}

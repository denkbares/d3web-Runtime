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

/**
 * Class to represent the results for checking arguments of a test. This class
 * is used for both, test arguments, and ignore arguments for tests.
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 04.05.2012
 */
public class ArgsCheckResult {

	private final String[] args;
	private final String[] errors;
	private final String[] warnings;

	/**
	 * Creates a new ArgsCheckResult for the specified arguments.
	 * 
	 * @param args the arguments that have been checked
	 */
	public ArgsCheckResult(String[] args) {
		this.args = args;
		if (args.length > 0) {
			errors = new String[args.length];
			warnings = new String[args.length];
		}
		else {
			// we still need a place to store errors
			errors = new String[1];
			warnings = new String[1];
		}
	}

	public static ArgsCheckResult emptyTestDeclaration() {
		ArgsCheckResult result = new ArgsCheckResult(new String[0]);
		result.setWarning(0, "The test declaration is empty. No test to be executed.");
		return result;
	}

	public static ArgsCheckResult classNotFoundResult(String testName) {
		ArgsCheckResult result = new ArgsCheckResult(new String[0]);
		result.setError(0, "Test '" + testName + "' does not exist");
		return result;
	}

	public static ArgsCheckResult noTestObjectIdentifier(String testName) {
		ArgsCheckResult result = new ArgsCheckResult(new String[0]);
		result.setError(0, "Missing test object identifier for test '" + testName + "'");
		return result;
	}

	public static ArgsCheckResult invalidTestObjectIdentifier(String identifier, String testname) {
		ArgsCheckResult result = new ArgsCheckResult(new String[] { identifier });
		result.setError(0, "Test object identifier for test '" + testname
				+ "' is not a valid regular expression: '" + identifier + "'");
		return result;
	}

	public String[] getArguments() {
		return args;
	}

	/**
	 * This method signals that the argument at the specified index is erroneous
	 * and attach the error with a message. If the argument already has a
	 * warning or error attached, the previous messgae will be overwritten.
	 * <p>
	 * Usually the allowed indexes are between 0 (inclusively) and the number of
	 * arguments (exclusively). If no arguments are specified, index 0 is still
	 * able to be used, e.g. to signal a missing argument
	 * 
	 * @created 17.09.2012
	 * @param argIndex the index of the argument
	 * @param message the message to be attached
	 * @throws IndexOutOfBoundsException if a wrong index is specified
	 */
	public void setError(int argIndex, String message) {
		if (argIndex < 0 || argIndex >= errors.length) {
			throw new IndexOutOfBoundsException("out of range for argument array");
		}
		errors[argIndex] = message;
	}

	/**
	 * This method signals that the argument at the specified index created some
	 * warning and attach the warning with a message. If the argument already
	 * has a error attached, the warning will be ignored. If the argument
	 * already has a warning attached, the previous warning will be overwritten.
	 * <p>
	 * Usually the allowed indexes are between 0 (inclusively) and the number of
	 * arguments (exclusively). If no arguments are specified, index 0 is still
	 * able to be used, e.g. to signal a missing argument.
	 * 
	 * @created 17.09.2012
	 * @param argIndex the index of the argument
	 * @param message the message to be attached
	 * @throws IndexOutOfBoundsException if a wrong index is specified
	 */
	public void setWarning(int argIndex, String message) {
		if (argIndex < 0 || argIndex >= errors.length) {
			throw new IndexOutOfBoundsException("out of range for argument array");
		}
		warnings[argIndex] = message;
	}

	/**
	 * Checks if the argument at the specified index is erroneous.
	 * <p>
	 * Usually the allowed indexes are between 0 (inclusively) and the number of
	 * arguments (exclusively). If no arguments are specified, index 0 is still
	 * able to be used, e.g. to signal a missing argument.
	 * 
	 * @created 17.09.2012
	 * @param argIndex the index of the argument to be checked
	 * @throws IndexOutOfBoundsException if a wrong index is specified
	 */
	public boolean hasError(int argIndex) {
		if (argIndex < 0 || argIndex >= errors.length) {
			throw new IndexOutOfBoundsException("out of range for argument array");
		}
		return errors[argIndex] != null;
	}

	public boolean hasError() {
		for (int i = 0; i < errors.length; i++) {
			if (errors[i] != null) return true;
		}
		return false;
	}

	/**
	 * Checks if the argument at the specified index created a warning.
	 * <p>
	 * Usually the allowed indexes are between 0 (inclusively) and the number of
	 * arguments (exclusively). If no arguments are specified, index 0 is still
	 * able to be used, e.g. to signal a missing argument
	 * 
	 * @created 17.09.2012
	 * @param argIndex the index of the argument to be checked
	 * @throws IndexOutOfBoundsException if a wrong index is specified
	 */
	public boolean hasWarning(int argIndex) {
		if (argIndex < 0 || argIndex >= errors.length) {
			throw new IndexOutOfBoundsException("out of range for argument array");
		}
		return warnings[argIndex] != null;
	}

	public boolean hasWarning() {
		for (int i = 0; i < warnings.length; i++) {
			if (warnings[i] != null) return true;
		}
		return false;
	}

	/**
	 * Returns the attached message for the argument at the specified index.
	 * <p>
	 * Usually the allowed indexes are between 0 (inclusively) and the number of
	 * arguments (exclusively). If no arguments are specified, index 0 is still
	 * able to be used, e.g. to signal a missing argument
	 * 
	 * @created 17.09.2012
	 * @param argIndex the index of the argument to be checked
	 * @throws IndexOutOfBoundsException if a wrong index is specified
	 */
	public String getMessage(int argIndex) {
		if (errors[argIndex] != null) {
			return errors[argIndex];
		}
		return warnings[argIndex];
	}

	public String getFirstErrorMessage() {
		for (int i = 0; i < errors.length; i++) {
			if (errors[i] != null) return errors[i];
		}
		return null;
	}
}

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

/**
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 04.05.2012
 */
public class ArgsCheckResult {

	private final String[] args;
	private String[] errors;
	private final String[] warnings;

	public ArgsCheckResult(String[] args) {
		this.args = args;
		errors = new String[args.length];
		warnings = new String[args.length];

		// we still need a place to store errors
		if (args.length == 0) {
			errors = new String[1];
		}
	}

	public String[] getArguments() {
		return args;
	}

	public void setError(int argIndex, String message) {
		if (argIndex < 0 || argIndex >= errors.length) {
			throw new IllegalArgumentException("out of range for argument array");
		}
		errors[argIndex] = message;
	}

	public void setWarning(int argIndex, String message) {
		if (argIndex < 0 || argIndex >= errors.length) {
			throw new IllegalArgumentException("out of range for argument array");
		}
		warnings[argIndex] = message;
	}

	public boolean hasError(int argIndex) {
		if (argIndex < 0 || argIndex >= errors.length) {
			throw new IllegalArgumentException("out of range for argument array");
		}
		return errors[argIndex] != null;
	}

	public boolean hasError() {
		for (int i = 0; i < errors.length; i++) {
			if (errors[i] != null) return true;
		}
		return false;
	}

	public boolean hasWarning() {
		for (int i = 0; i < warnings.length; i++) {
			if (warnings[i] != null) return true;
		}
		return false;
	}

	public boolean hasWarning(int argIndex) {
		if (argIndex < 0 || argIndex >= errors.length) {
			throw new IllegalArgumentException("out of range for argument array");
		}
		return warnings[argIndex] != null;
	}

	public String getMessage(int argIndex) {
		if (errors[argIndex] != null) {
			return errors[argIndex];
		}
		else {
			return warnings[argIndex];
		}
	}

}

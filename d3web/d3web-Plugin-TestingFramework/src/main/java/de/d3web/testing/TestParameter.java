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

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Represents a test parameter.
 * 
 * @author Jochen Reutelshoefer (denkbares GmbH)
 * @created 31.07.2012
 */
public class TestParameter {

	public enum Type {
		String, Regex, Number, Enum
	};

	public enum Mode {
		Mandatory, Optional
	};

	private final Type type;
	private final String name;
	private final Mode mode;
	private final String description;
	private final String[] options;

	public TestParameter(String name, Type type, Mode mode, String description) {
		this(name, type, mode, description, new String[0]);
	}

	/**
	 * Creates a parameter of Type Enum with given options.
	 * 
	 * @param options the enum options
	 */
	public TestParameter(String name, Mode mode, String description, String... options) {
		this(name, Type.Enum, mode, description, options);
	}

	private TestParameter(String name, Type type, Mode mode, String description, String... options) {
		if (options.length == 0 && type == Type.Enum) {
			throw new IllegalArgumentException("Options must be specified for enumerations.");
		}

		this.type = type;
		this.name = name;
		this.mode = mode;
		this.description = description;
		this.options = options;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "\"" + name + "\" (" + type.toString() + ", " + mode.toString() + "): "
				+ description;
	}

	/**
	 * Checks whether the passed string value complies to the type of this
	 * parameter.
	 * 
	 * 
	 * @created 15.10.2012
	 * @param value
	 * @return Compliance of the string to the parameter type
	 */
	public boolean checkParameterValue(String value) {

		if (value == null || value.trim().length() == 0) {
			return false;
		}

		// check whether it is a valid regex
		if (type.equals(Type.Regex)) {
			try {
				Pattern.compile(value);
			}
			catch (PatternSyntaxException e) {
				return false;
			}
		}
		// check whether it is a valid number
		else if (type.equals(Type.Number)) {
			try {
				Double.parseDouble(value);
			}
			catch (NumberFormatException e) {
				return false;
			}
		}
		// check, if a valid option has been supplied for
		else if (type.equals(Type.Enum)) {
			for (String string : options) {
				if (value.equalsIgnoreCase(string))
					return true;
			}
			return false;
		}

		// hence ok
		return true;
	}

	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public Mode getMode() {
		return mode;
	}

	public String[] getOptions() {
		return options;
	}

}

/*
 * Copyright (C) 2015 denkbares GmbH
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
package de.d3web.utils;

import java.lang.reflect.InvocationTargetException;

import de.d3web.strings.Strings;

/**
 * Utility class for instantiating textual constructor calls using a specific {@link ClassLoader}.
 * The {@link ClassLoader} that shall has to be specified in the constructor.
 * The {@link #newInstance} method does the actual instantiation of constructor calls.
 * The instantiation supports the definition of primitive arguments
 * {@link String}, double, int.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 08.06.15
 */
public class Instantiation {

	private final ClassLoader classLoader;
	private InstantiationContext context;

	public Instantiation(ClassLoader classLoader) {
		if (classLoader == null) {
			throw new NullPointerException("classLoader can't be null.");
		}
		this.classLoader = classLoader;
	}

	public InstantiationContext getContext() {
		return context;
	}

	public void setContext(InstantiationContext context) {
		this.context = context;
	}

	/**
	 * Tries to invoke a constructor of the specified constructor call . The arguments are parsed
	 * from the constructor call expression. If there is no such constructor or if the constructor
	 * cannot be accessed, null is returned. If the constructor can be called, but fails with an
	 * exception, an InvocationTargetException is thrown.
	 *
	 * Example constructor calls are:
	 * <ul>
	 *     <li>java.util.ArrayList</li>
	 *     <li>java.util.ArrayList(5)</li>
	 * </ul>
	 *
	 * @param constructorCall A constructor call that may contain primitive arguments.
	 * @return the created instance
	 *
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 */
	public Object newInstance(String constructorCall) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {

		String className = constructorCall;

		// allow arguments to class as constructor parameters
		Object[] argValues = new Object[0];
		Class<?>[] argTypes = new Class[0];
		int argIndex = constructorCall.indexOf('(');
		if (argIndex > 0) {

			// parse constructor call
			className = constructorCall.substring(0, argIndex).trim();
			String argsString = constructorCall.substring(argIndex + 1, constructorCall.lastIndexOf(')'));

			if (!argsString.isEmpty()) {

				String[] arguments = argsString.trim().split("\\s*,\\s*");

				// init arguments and argument types
				argValues = new Object[arguments.length];
				argTypes = new Class[arguments.length];
				for (int i = 0; i < arguments.length; i++) {
					String argument = arguments[i];
					// accept string types
					if (argument.matches("'.*'|\".*\"")) {
						argTypes[i] = String.class;
						argValues[i] = Strings.unquote(argument.substring(1, argument.length() - 1),
								argument.charAt(0));
					}
					// accept integer arguments
					else if (argument.matches("\\d+")) {
						argTypes[i] = Integer.class;
						argValues[i] = new Integer(argument);
					}
					// accept double arguments
					else if (argument.matches("\\d+\\.\\d+")) {
						argTypes[i] = Double.class;
						argValues[i] = new Double(argument);
					}
					// allow other types as required
					// if not, an exception if thrown
					else {
						String message = "The constructor '" + constructorCall +
								"' has an unsupported argument type. " +
								(context != null ? "Please check: '" + context.getOrigin() + "'. " : "");
						Log.severe(message);
						throw new IllegalArgumentException(message);
					}
				}

			}
		}

		Class<?> clazz = classLoader.loadClass(className);
		// instantiate with parameters
		if (argValues.length > 0) {
			return clazz.getConstructor(argTypes).newInstance(argValues);
		}
		// instantiate normally
		return clazz.newInstance();
	}

}

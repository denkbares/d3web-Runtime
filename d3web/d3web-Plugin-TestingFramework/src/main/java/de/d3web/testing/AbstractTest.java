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
import java.util.List;

/**
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 30.05.2012
 */
public abstract class AbstractTest<T> implements de.d3web.testing.Test<T> {

	private final List<TestParameter> parameters = new ArrayList<TestParameter>();

	@Override
	public List<TestParameter> getParameterSpecification() {
		return Collections.unmodifiableList(parameters);
	}

	protected void addParameter(String name, TestParameter.Type type, TestParameter.Mode mode, String description) {
		parameters.add(new TestParameter(name, type, mode, description));
	}

	protected int getNumberOfMandatoryParameters() {
		int count = 0;
		for (TestParameter p : parameters) {
			if (p.getMode().equals(TestParameter.Mode.Mandatory)) {
				count++;
			}
		}
		return count;
	}

	@Override
	public ArgsCheckResult checkArgs(String[] args) {

		ArgsCheckResult r = new ArgsCheckResult(args);
		if (args.length < getNumberOfMandatoryParameters()) {
			r.setError(0,
					"Not enough arguments for execution of test '"
							+ this.getClass().getSimpleName()
							+ "'. Expected " + parameters.size() + " argument"
							+ (parameters.size() == 1 ? "" : "s") + ", but found "
							+ args.length + ".");
			return r;
		}

		for (int i = 0; i < args.length; i++) {

			// check whether array might be longer than registered number of
			// arguments
			if (i >= parameters.size()) {
				r.setWarning(args.length - 1,
						"Too many arguments passend for test '" + this.getClass().getSimpleName()
								+ "': Expected a maximum of " + parameters.size() + " argument"
								+ (parameters.size() == 1 ? "" : "s") + ", but found "
								+ args.length + ".");
				return r;
			}

			// get parameter no. i and check arg value
			TestParameter parameter = parameters.get(i);
			boolean ok = parameter.checkParameterValue(args[i]);
			if (!ok) {
				r.setError(i,
						"Argument passend as '" + parameter.getName()
								+ "' is not a valid " + parameter.getType().toString()
								+ " argument: \"" + args[i] + "\".");
			}
		}

		return r;
	}

}

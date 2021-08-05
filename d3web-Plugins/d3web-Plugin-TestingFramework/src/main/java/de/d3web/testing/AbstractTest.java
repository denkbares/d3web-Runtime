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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.denkbares.strings.Strings;

/**
 * Abstract implementation of a test that easily enables basic argument checking.
 *
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 30.05.2012
 */
public abstract class AbstractTest<T> implements Test<T> {

	private final List<TestParameter> argParameters = new ArrayList<>();
	private final List<TestParameter> ignoreParameters = new ArrayList<>();

	@Override
	public final List<TestParameter> getParameterSpecification() {
		return Collections.unmodifiableList(argParameters);
	}

	@Override
	public final List<TestParameter> getIgnoreSpecification() {
		return Collections.unmodifiableList(ignoreParameters);
	}

	protected void addParameter(TestParameter parameter) {
		argParameters.add(parameter);
	}

	protected void addParameter(String name, TestParameter.Mode mode, String description, String... options) {
		argParameters.add(new TestParameter(name, mode, description, options));
	}

	protected void addParameter(String name, TestParameter.Mode mode, String description, Enum<?>... options) {
		argParameters.add(new TestParameter(name, mode, description, Strings.names(options)));
	}

	protected void addParameter(String name, TestParameter.Type type, TestParameter.Mode mode, String description) {
		argParameters.add(new TestParameter(name, type, mode, description));
	}

	protected void addIgnoreParameter(String name, TestParameter.Type type, TestParameter.Mode mode, String description) {
		ignoreParameters.add(new TestParameter(name, type, mode, description));
	}

	protected void addIgnoreParameter(String name, TestParameter.Mode mode, String description, String... options) {
		ignoreParameters.add(new TestParameter(name, mode, description, options));
	}

	@Override
	public ArgsCheckResult checkArgs(String[] args) {
		return checkParameter(this, args, argParameters, false);
	}

	@Override
	public ArgsCheckResult checkIgnore(String[] args) {
		return checkParameter(this, args, ignoreParameters, true);
	}

	@Override
	public Message execute(TestSpecification<T> specification, T testObject) throws InterruptedException {
		return execute(testObject, specification.getArguments(), specification.getIgnores());
	}

	/**
	 * Optional method to provide backward compatibility for tests.
	 * Overwrite either this method or (better) #execute(TestSpecification&lt;T&gt;, T)
	 *
	 * @param testObject the object to be tested
	 * @param args       the arguments of the test specification
	 * @param ignores    the ignore arguments of the test specification
	 * @return the result of the test as a message
	 * @throws InterruptedException if the user has interrupted the test
	 * @see #execute(TestSpecification, Object)
	 */
	public Message execute(T testObject, String[] args, String[]... ignores) throws InterruptedException {
		return null;
	}

	private static ArgsCheckResult checkParameter(Test<?> test, String[] args, List<TestParameter> parameters, boolean ignoreParams) {
		ArgsCheckResult r = new ArgsCheckResult(args);
		int minParamCount = getNumberOfMandatoryParameters(parameters);
		if (args.length < minParamCount) {
			r.setError(0,
					"Not enough " + (ignoreParams ? "ignore " : "")
							+ "arguments for execution of test '"
							+ test.getName()
							+ "'. Expected " + parameters.size() + " argument"
							+ (parameters.size() == 1 ? "" : "s") + ", but found "
							+ args.length + ".");
			return r;
		}

		for (int i = 0; i < args.length; i++) {

			// check whether array might be longer than registered number of
			// arguments
			if (i >= parameters.size()) {
				r.setError(args.length - 1,
						"Too many " + (ignoreParams ? "ignore " : "")
								+ "arguments passed for test '" + test.getName()
								+ "': Expected " + parameters.size() + " argument"
								+ (parameters.size() == 1 ? "" : "s") + ", but found "
								+ args.length + ": " + Arrays.toString(args) + ".");
				return r;
			}

			// get parameter no. i and check arg value
			TestParameter parameter = parameters.get(i);
			boolean ok = parameter.checkParameterValue(args[i]);
			if (!ok) {
				r.setError(i, (ignoreParams ? "Ignore a" : "A") + "rgument passed as '"
						+ parameter.getName()
						+ "' is not a valid " + parameter.getType()
						+ " argument: \"" + args[i] + "\".");
			}
		}

		return r;
	}

	@Override
	public String getName() {
		return TestManager.getTestName(this);
	}

	@Override
	public void updateSummary(TestSpecification<?> specification, TestResult result) {
		TestingUtils.updateSummary(result);
	}

	private static int getNumberOfMandatoryParameters(List<TestParameter> parameters) {
		int count = 0;
		for (TestParameter p : parameters) {
			if (p.getMode() == TestParameter.Mode.Mandatory) {
				count++;
			}
		}
		return count;
	}

	@NotNull
	protected Message getMessage(List<MessageObject> messageObjects, Collection<String> messages, Message.Type messageType) {
		return getMessage(null, messageObjects, messages, messageType);
	}

	@NotNull
	protected Message getMessage(String suffix, List<MessageObject> messageObjects, Collection<String> messages, Message.Type messageType) {
		if (messages.isEmpty()) return new Message(Message.Type.SUCCESS, suffix);
		String message = "The following " + Strings.pluralOf(messages.size(), messageType.toString().toLowerCase()) +
				" were detected:\n* " + getMessageStringFromCollection(messages)
				+ (Strings.isBlank(suffix) ? "" : ("\n\n" + suffix));
		return new Message(messageType, message, messageObjects);
	}

	protected String getMessageStringFromCollection(Collection<String> messages) {
		return messages.stream()
				.map(s -> Strings.trimRight(s).replace("\n", "\n** "))
				.collect(Collectors.joining("\n* "));
	}

	/**
	 * enables rendering of test-objects with expected outcome (good case).
	 * <p>
	 * usually only test-objects with unexpected outcome (bad case) are rendered.
	 *
	 * @return whether to render test-objects with expected outcome
	 */
	public boolean renderResultsOfSucceededTests() {
		return false;
	}
}

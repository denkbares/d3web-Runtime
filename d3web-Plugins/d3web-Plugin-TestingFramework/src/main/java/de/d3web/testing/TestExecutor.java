package de.d3web.testing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.denkbares.collections.DefaultMultiMap;
import com.denkbares.collections.MultiMap;
import com.denkbares.progress.ParallelProgress;
import com.denkbares.progress.ProgressListener;
import com.denkbares.utils.Stopwatch;
import de.d3web.testing.Message.Type;

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

/**
 * A TestExecutor executes a set of tests. It can use multiple threads to execute the testing tasks in parallel. It uses
 * updates a ProgressListener on finished tasks.
 *
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 04.05.2012
 */
public class TestExecutor {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestExecutor.class);

	private static final AtomicLong THREAD_NUMBER = new AtomicLong();
	private static final ExecutorService DEFAULT_EXECUTOR = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors(),
			r -> new Thread(r, "Default-Test-Executor-" + THREAD_NUMBER.incrementAndGet()));
	private static final ExecutorService DEFAULT_SUB_EXECUTOR = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors(),
			r -> new Thread(r, "Default-SubTest-Executor-" + THREAD_NUMBER.incrementAndGet()));

	private final Collection<TestObjectProvider> objectProviders;
	private final List<TestSpecification<?>> specifications;
	private final ProgressListener progressListener;
	private final BuildResult build;
	private final HashMap<TestSpecification<?>, TestResult> testResults = new HashMap<>();
	private final Set<FutureTestTask> futures = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private final ExecutorService executor;
	private final ExecutorService subExecutor;
	private final double priority;
	private volatile boolean aborted;
	private volatile boolean initialized = false;

	/**
	 * Returns the current build or null if the build has been terminated.
	 *
	 * @return the current build
	 * @created 14.08.2012
	 */
	public synchronized BuildResult getBuildResult() {
		if (!isShutdown()) {
			LOGGER.warn("Build not yet finished, build result not available!");
			return null;
		}
		return build;
	}

	/**
	 * Creates a new TestExecutor.
	 *
	 * @param providers      the provider for the test objects
	 * @param specifications the specifications of the tests
	 * @param listener       the progress listener
	 */
	public TestExecutor(Collection<TestObjectProvider> providers, List<TestSpecification<?>> specifications, ProgressListener listener) {
		this(providers, specifications, listener, DEFAULT_EXECUTOR, DEFAULT_SUB_EXECUTOR, 10);
	}

	/**
	 * Creates a new TestExecutor with the given executor service.
	 *
	 * @param executorService the executor service to be used when running the tests
	 * @param providers       the provider for the test objects
	 * @param specifications  the specifications of the tests
	 * @param listener        the progress listener
	 * @param priority        the priority with which the tests should be executed in the executorService (in case there
	 *                        are multiple TestExecutor for the same {@link ExecutorService}
	 */
	public TestExecutor(Collection<TestObjectProvider> providers, List<TestSpecification<?>> specifications, ProgressListener listener, ExecutorService executorService, ExecutorService subExecutorService, double priority) {
		this.objectProviders = providers;
		this.specifications = specifications;
		this.progressListener = listener;
		this.build = new BuildResult();
		this.executor = executorService;
		this.subExecutor = subExecutorService;
		this.priority = priority;
	}

	/**
	 * Runs the tests given by the task list using the provided TestObjectProvider. A BuildResultSet with the given
	 * build-number is created.
	 *
	 * @created 22.05.2012
	 */
	public void run() {
		long buildStartTime = System.currentTimeMillis();

		// checks the given tests of validity and only
		// returns the valid ones
		Collection<TestSpecification<?>> validSpecifications = getValidSpecifications();

		// creates and returns a CallableTest for each Test and TestObject
		Map<TestSpecification<?>, Collection<RunnableTest<?>>> callableTests =
				getCallableTestsMap(validSpecifications);
		try {
			initTest(callableTests);
			startTests(callableTests);
			initialized = true;
		}
		finally {
			awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
			updateTestResults(callableTests);
			build.setBuildDuration(System.currentTimeMillis() - buildStartTime);
		}
	}

	private void updateTestResults(Map<TestSpecification<?>, Collection<RunnableTest<?>>> callableTests) {
		MultiMap<TestResult, TestResult> groups = new DefaultMultiMap<>();
		TestResult currentGroup = null;
		for (TestSpecification<?> specification : callableTests.keySet()) {
			Collection<RunnableTest<?>> ct = callableTests.get(specification);
			if (this.aborted) {
				for (RunnableTest<?> runnableTest : ct) {
					if (!runnableTest.hasStarted()) {
						runnableTest.testResult.addUnexpectedMessage(runnableTest.testObjectName,
								new Message(Type.ABORTED, "Test was aborted"));
					}
				}
			}
			if (specification.getTest() instanceof TestGroup) {
				// if this is a group, remember the group, but do nothing,
				// because we will update the groups later on
				currentGroup = createTestResult(specification);
			}
			else {
				if (ct.isEmpty()) continue;
				TestResult testResult = ct.iterator().next().testResult;
				// update test and add to group (if there is any)
				specification.getTest().updateSummary(specification, testResult);
				if (currentGroup != null) groups.put(currentGroup, testResult);
			}
		}
		// finally update the groups
		for (TestResult group : groups.keySet()) {
			Message summarizedResult = TestingUtils.getSummarizedResult(
					groups.getValues(group).toArray(new TestResult[0]));
			group.setSummary(summarizedResult);
		}
	}

	private void initTest(Map<TestSpecification<?>, Collection<RunnableTest<?>>> callableTestsBySpecification) {
		List<RunnableTest<?>> runnableTests = callableTestsBySpecification.values()
				.stream()
				.flatMap(Collection::stream).toList();

		// collect all the complexities from all tests
		float[] complexities = new float[runnableTests.size()];
		for (int i = 0; i < runnableTests.size(); i++) {
			RunnableTest<?> runnableTest = runnableTests.get(i);
			Test<?> test = runnableTest.specification.getTest();
			complexities[i] = test instanceof ProgressingTest ?
					((ProgressingTest) test).getComputationalComplexity() : ProgressingTest.DEFAULT_COMPUTATION_COMPLEXITY;
		}

		// create ParallelProgressListener and set the listener of the sub tasks accordingly
		ParallelProgress parallelProgress = new ParallelProgress(this.progressListener, complexities);
		for (int i = 0; i < runnableTests.size(); i++) {
			runnableTests.get(i).setProgressListener(parallelProgress.getSubTaskProgressListener(i));
		}

		for (RunnableTest<?> runnableTest : runnableTests) {
			runnableTest.registerCallable();
		}
	}

	private float getNumberOfTests(Map<TestSpecification<?>, Collection<RunnableTest<?>>> callablesMap) {
		int count = 0;
		for (Collection<RunnableTest<?>> callablesOfTest : callablesMap.values()) {
			count += callablesOfTest.size();
		}
		return count;
	}

	private <T> Collection<TestObjectContainer<T>> getTestObjects(TestSpecification<T> specification) {

		Collection<TestObjectContainer<T>> allTestObjects = new LinkedList<>();
		Test<T> test = specification.getTest();
		Class<T> objectClass = test.getTestObjectClass();
		if (objectClass == null) return Collections.emptyList();

		String testObjectID = specification.getTestObject();
		for (TestObjectProvider testObjectProvider : objectProviders) {
			allTestObjects.addAll(testObjectProvider.getTestObjects(objectClass, testObjectID));
		}
		return allTestObjects;
	}

	@SuppressWarnings("UnnecessaryLabelOnContinueStatement")
	private Collection<TestSpecification<?>> getValidSpecifications() {
		Collection<TestSpecification<?>> validTests = new ArrayList<>();
		prepareTests:
		for (TestSpecification<?> specification : specifications) {
			String[] testArgs = specification.getArguments();
			String testObjectID = specification.getTestObject();
			Test<?> test = specification.getTest();

			// check arguments and create error if erroneous
			ArgsCheckResult argsCheckResult = test.checkArgs(testArgs);
			if (argsCheckResult.hasError()) {
				TestResult testResult = toTestResult(test, testObjectID, argsCheckResult, specification.isSoftTest());
				build.addTestResult(testResult);
				continue prepareTests;
			}

			// check ignores and create error if erroneous
			for (String[] ignoreArgs : specification.getIgnores()) {
				ArgsCheckResult ignoreCheckResult = test.checkIgnore(ignoreArgs);
				if (ignoreCheckResult.hasError()) {
					TestResult testResult = toTestResult(test, testObjectID, ignoreCheckResult, specification.isSoftTest());
					build.addTestResult(testResult);
					continue prepareTests;
				}
			}
			validTests.add(specification);
		}
		return validTests;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<TestSpecification<?>, Collection<RunnableTest<?>>> getCallableTestsMap(Collection<TestSpecification<?>> validSpecifications) {
		// Some weird non-generic stuff here to satisfy both Eclipse and Intellij compilers
		Map<TestSpecification<?>, Collection<RunnableTest<?>>> callableTests = new LinkedHashMap<>();
		for (TestSpecification<?> specification : validSpecifications) {
			Collection futuresForCallableTest = getCallableTests(specification);
			callableTests.put(specification, futuresForCallableTest);
		}
		return callableTests;
	}

	private <T> Collection<RunnableTest<T>> getCallableTests(TestSpecification<T> specification) {
		// prepare test objects
		Collection<TestObjectContainer<T>> testObjects = getTestObjects(specification);

		// create (empty) result for test execution
		TestResult testResult = createTestResult(specification);
		build.addTestResult(testResult);

		// create callable tests to be executed
		return getCallableTests(specification, testObjects, testResult);
	}

	private <T> TestResult createTestResult(TestSpecification<T> specification) {
		TestResult testResult = testResults.get(specification);
		if (testResult == null) {
			String[] testArgs = specification.getArguments();
			String testName = specification.getTestName();
			String[] config = new String[testArgs.length + 1];
			System.arraycopy(testArgs, 0, config, 1, testArgs.length);
			config[0] = specification.getTestObject();
			testResult = new TestResult(testName, config);
			testResult.setSoftTest(specification.isSoftTest());
			testResults.put(specification, testResult);
		}
		return testResult;
	}

	private <T> Collection<RunnableTest<T>> getCallableTests(
			final TestSpecification<T> specification,
			Collection<TestObjectContainer<T>> testObjects, final TestResult testResult) {

		if (specification.getTest() instanceof TestGroup) {
			return Collections.emptyList();
		}

		Collection<RunnableTest<T>> result = new HashSet<>();
		boolean noTestObjects = true;

		for (final TestObjectContainer<T> testObjectContainer : testObjects) {
			noTestObjects = false;
			String testObjectName = testObjectContainer.getTestObjectName();
			T testObject = testObjectContainer.getTestObject();
			RunnableTest<T> runnableTest = new RunnableTest<>(specification, testObjectName, testObject, testResult);
			result.add(runnableTest);
		}

		// if no test can be applied, we assume test to be skipped
		if (noTestObjects) {
			testResult.setSummary(new Message(Type.SKIPPED, "No test objects available for this test."));
		}
		return result;
	}

	private void startTests(Map<TestSpecification<?>, Collection<RunnableTest<?>>> callablesMap) {
		// finally run execute on callable tests
		outerLoop:
		for (TestSpecification<?> specification : callablesMap.keySet()) {
			Collection<RunnableTest<?>> runnableTests = callablesMap.get(specification);
			specification.prepareExecution();
			for (RunnableTest<?> runnableTest : runnableTests) {
				try {
					FutureTestTask task = new FutureTestTask(runnableTest, priority);
					executor.execute(task);
					futures.add(task);
				}
				catch (RejectedExecutionException e) {
					// it is possible that the executor is shut down during or
					// before adding the tests to the executor... we just catch it
					LOGGER.debug("Rejected execution of " + runnableTest.specification.getTestName() + ": " + runnableTest.testObjectName);
				}

				try {
					TestingUtils.checkInterrupt();
				}
				catch (InterruptedException e) {
					shutDownNow();
					break outerLoop;
				}
			}
		}
	}

	private void setTerminateStatus() {
		aborted = true;
		progressListener.updateProgress(1f, "Aborted, please wait...");
	}

	public boolean isShutdown() {
		return initialized && futures.isEmpty();
	}

	/**
	 * Terminates/aborts all running tests as fast as possible. This method does not wait until the tests really have
	 * been terminated, use {@link TestExecutor#awaitTermination()} for this purpose.
	 *
	 * @created 21.09.2012
	 */
	public void shutDownNow() {
		// aborted, so discard build
		setTerminateStatus();
		// System.out.println("Terminating executor");
		futures.forEach(f -> f.cancel(f.mayInterrupt()));
	}

	/**
	 * Blocks/waits until all running tests are done, tests are not aborted or shutdown by calling this method. Given
	 * timeout defines the maximum waited time until the method returns.
	 *
	 * @created 17.12.2013
	 */
	public void awaitTermination(long timeout, TimeUnit unit) {
		futures.forEach(f -> {
			try {
				f.get(timeout, unit);
			}
			catch (InterruptedException e) {
				LOGGER.info("Interrupted while waiting for test shut down");
			}
			catch (ExecutionException e) {
				LOGGER.error("Exception while waiting for test shut down", e);
			}
			catch (TimeoutException | CancellationException ignore) {
				// as expected...
			}
		});
	}

	/**
	 * Blocks/waits until all running tests are done, tests are not aborted by calling this method.
	 *
	 * @created 17.12.2013
	 */
	public void awaitTermination() {
		awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	}

	private <T> TestResult toTestResult(Test<T> test, String testObjectName, ArgsCheckResult checkResult) {
		Message message = null;
		String[] arguments = checkResult.getArguments();
		for (int i = 0; i < arguments.length; i++) {
			if (checkResult.hasError(i)) {
				message = new Message(Type.ERROR,
						"Invalid argument " + arguments[i] + ": " + checkResult.getMessage(i));
				break;
			}
		}
		if (arguments.length == 0 && checkResult.hasError(0)) {
			message = new Message(Type.ERROR, checkResult.getMessage(0));
		}

		TestResult result = new TestResult(test.getName(), checkResult.getArguments());
		if (message != null) {
			result.addUnexpectedMessage(testObjectName, message);
			result.setSummary(new Message(Type.ERROR, message.getText()));
		}
		return result;
	}

	private <T> TestResult toTestResult(Test<T> test, String testObjectName, ArgsCheckResult checkResult, boolean isSoftTest) {
		TestResult result = toTestResult(test, testObjectName, checkResult);
		result.setSoftTest(isSoftTest);
		return result;
	}

	class FutureTestTask extends FutureTask<Void> implements Comparable<FutureTestTask> {

		private final Runnable runnable;
		private final double priority;

		public FutureTestTask(Runnable runnable, double priority) {
			super(runnable, null);
			this.runnable = runnable;
			this.priority = priority;
		}

		@Override
		public Void get() throws InterruptedException, ExecutionException {
			if (aborted) return null;
			Void value = super.get();
			cleanup();
			return value;
		}

		public boolean mayInterrupt() {
			if (this.runnable instanceof RunnableTest<?> runnableTest) {
				return runnableTest.mayInterrupt();
			}
			else {
				return false;
			}
		}

		@Override
		public Void get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			if (aborted) return null;
			Void value = super.get(timeout, unit);
			cleanup();
			return value;
		}

		private void cleanup() {
			// update progress listener as task has been finished
			if (this.runnable instanceof RunnableTest<?> runnableTest) {
				runnableTest.testFinished();
			}
			futures.remove(this);
		}

		@Override
		public int compareTo(@NotNull TestExecutor.FutureTestTask other) {
			return Double.compare(other.priority, this.priority);
		}
	}

	class RunnableTest<T> implements Runnable {

		private final String testObjectName;
		private final TestSpecification<T> specification;
		private final T testObject;
		private final TestResult testResult;
		private ProgressListener progressListener = null;
		private volatile Stopwatch started = null;
		private final Set<FutureTestTask> subTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());

		public RunnableTest(TestSpecification<T> specification, String testObjectName, T testObject, TestResult testresult) {
			this.specification = specification;
			this.testObject = testObject;
			this.testObjectName = testObjectName;
			this.testResult = testresult;
		}

		public String getTestObjectName() {
			return testObjectName;
		}

		public TestSpecification<T> getSpecification() {
			return specification;
		}

		public boolean mayInterrupt() {
			return specification.getTest().allowInterrupt();
		}

		public void setProgressListener(ProgressListener progressListener) {
			this.progressListener = progressListener;
			Test<T> test = specification.getTest();
			if (test instanceof ProgressingTest) {
				((ProgressingTest) test).setProgressListener(progressListener);
			}
		}

		public void registerCallable() {
			Test<T> test = specification.getTest();
			if (test instanceof ParallelizedTest parallelizedTest) {
				parallelizedTest.registerTestTaskConsumer(new ParallelizedTest.TestTaskHandler() {
					@Override
					public void accept(ParallelizedTest.TestTask testTask) {
						FutureTestTask task = new FutureTestTask(() -> {
							try {
								testTask.run();
							}
							catch (InterruptedException e) {
								LOGGER.error("Interrupted test sub task", e);
							}
						}, priority);
						subExecutor.execute(task);
						futures.add(task);
						subTasks.add(task);
					}

					@Override
					public void awaitSubtasks() {
						try {
							for (FutureTestTask subTask : subTasks) {
								subTask.get();
							}
						}
						catch (InterruptedException | ExecutionException e) {
							testResult.addUnexpectedMessage(testObjectName, new Message(Type.ABORTED, "Sub-test was aborted"));
						}
					}
				});
			}
		}

		public void testStarted() {
			started = new Stopwatch();
			progressListener.updateProgress(0, specification.getTestName() + ": " + testObjectName);
		}

		public void testFinished() {
			progressListener.updateProgress(1f, null);
		}

		@Override
		public void run() {
			testStarted();

			try {
				if (testObject == null) {
					testResult.addUnexpectedMessage(testObjectName, new Message(
							Message.Type.ERROR, "Test-object was null."));
					return;
				}
				Test<T> test = specification.getTest();
				Message message = test.execute(specification, testObject);

				for (FutureTestTask subTask : subTasks) {
					subTask.get();
				}

				if (TestExecutor.this.aborted) {
					testResult.addUnexpectedMessage(testObjectName, new Message(Type.ABORTED, "Test was aborted"));
				}
				else {
					if (message.getType() == Type.SUCCESS) {
						testResult.addExpectedMessage(testObjectName, message);
					}
					else {
						testResult.addUnexpectedMessage(testObjectName, message);
					}
				}
			}
			catch (InterruptedException e) {
				testResult.addUnexpectedMessage(testObjectName, new Message(Type.ABORTED, "Test was aborted"));
			}
			catch (Throwable e) { // NOSONAR
				// must catch throwable here to also handle unexpected errors
				// such as StackOverflow, memory issues or invalid plugins (linkage) errors
				String message = "Unexpected error in test " + specification.getTestName() + ", during testing '" + testObjectName + "': "
								 + e.getClass().getName() + ": " + e.getMessage();
				testResult.addUnexpectedMessage(testObjectName, new Message(Message.Type.ERROR, message + ": " + e));
				LOGGER.warn(message);
			}
			finally {
				testResult.setRunTime(this.started.getTime());
				testFinished();
			}
		}

		public boolean hasStarted() {
			return started != null;
		}
	}
}

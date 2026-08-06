/*
 * Copyright (C) 2026 denkbares GmbH, Germany
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
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, see the FSF site:
 * http://www.fsf.org.
 */

package de.d3web.testing.test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import de.d3web.testing.AbstractTest;
import de.d3web.testing.Message;
import de.d3web.testing.ParallelizedTest;
import de.d3web.testing.TestExecutor;
import de.d3web.testing.TestObjectContainer;
import de.d3web.testing.TestObjectProvider;
import de.d3web.testing.TestSpecification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestExecutorCancellationTest {

	@Test(timeout = 5000)
	public void waitsUntilInterruptedParallelTaskActuallyStops() throws Exception {
		CountDownLatch taskStarted = new CountDownLatch(1);
		CountDownLatch taskInterrupted = new CountDownLatch(1);
		CountDownLatch allowTaskToExit = new CountDownLatch(1);
		CountDownLatch taskExited = new CountDownLatch(1);
		InterruptibleParallelTest test = new InterruptibleParallelTest(
				taskStarted, taskInterrupted, allowTaskToExit, taskExited);

		ExecutorService mainTestExecutor = Executors.newSingleThreadExecutor();
		ExecutorService subTestExecutor = Executors.newSingleThreadExecutor();
		ExecutorService runner = Executors.newSingleThreadExecutor();
		Future<?> execution = null;
		try {
			TestSpecification<String> specification = new TestSpecification<>(
					test, "test-object", new String[0], new String[0][]);
			TestExecutor executor = new TestExecutor(
					List.of(providerFor("test-object")),
					List.of(specification),
					(percent, message) -> {
					},
					mainTestExecutor,
					subTestExecutor,
					10);

			execution = runner.submit(executor::run);
			assertTrue("Parallel task did not start", taskStarted.await(2, TimeUnit.SECONDS));

			executor.shutDownNow();

			assertTrue("Parallel task was not interrupted", taskInterrupted.await(2, TimeUnit.SECONDS));
			assertFalse("Parallel task exited before the controlled cleanup completed",
					taskExited.await(100, TimeUnit.MILLISECONDS));
			assertFalse("TestExecutor returned while a cancelled parallel task was still running", execution.isDone());

			allowTaskToExit.countDown();
			execution.get(2, TimeUnit.SECONDS);
			assertTrue("Parallel task did not exit", taskExited.await(1, TimeUnit.SECONDS));
			assertTrue("Executor still reports running tasks", executor.isShutdown());
		}
		finally {
			allowTaskToExit.countDown();
			mainTestExecutor.shutdownNow();
			subTestExecutor.shutdownNow();
			runner.shutdownNow();
		}
	}

	private static TestObjectProvider providerFor(String object) {
		return new TestObjectProvider() {
			@Override
			public <T> List<TestObjectContainer<T>> getTestObjects(Class<T> clazz, String name) {
				return List.of(new TestObjectContainer<>(name, clazz.cast(object)));
			}
		};
	}

	private static class InterruptibleParallelTest extends AbstractTest<String> implements ParallelizedTest {

		private final CountDownLatch taskStarted;
		private final CountDownLatch taskInterrupted;
		private final CountDownLatch allowTaskToExit;
		private final CountDownLatch taskExited;
		private TestTaskHandler taskHandler;

		private InterruptibleParallelTest(CountDownLatch taskStarted,
									  CountDownLatch taskInterrupted,
									  CountDownLatch allowTaskToExit,
									  CountDownLatch taskExited) {
			this.taskStarted = taskStarted;
			this.taskInterrupted = taskInterrupted;
			this.allowTaskToExit = allowTaskToExit;
			this.taskExited = taskExited;
		}

		@Override
		public Message execute(TestSpecification<String> specification, String testObject) {
			taskHandler.accept(() -> {
				taskStarted.countDown();
				try {
					Thread.sleep(Long.MAX_VALUE);
				}
				catch (InterruptedException e) {
					taskInterrupted.countDown();
					allowTaskToExit.await();
				}
				finally {
					taskExited.countDown();
				}
			});
			taskHandler.awaitSubtasks();
			return Message.SUCCESS;
		}

		@Override
		public void registerTestTaskConsumer(TestTaskHandler testTaskConsumer) {
			this.taskHandler = testTaskConsumer;
		}

		@Override
		public String getName() {
			return "interruptible-parallel-test";
		}

		@Override
		public Class<String> getTestObjectClass() {
			return String.class;
		}

		@Override
		public String getDescription() {
			return "Test fixture for parallel cancellation.";
		}
	}
}

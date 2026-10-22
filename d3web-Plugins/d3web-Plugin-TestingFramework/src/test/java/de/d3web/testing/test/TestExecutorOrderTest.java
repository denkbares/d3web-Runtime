package de.d3web.testing.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Test;

import de.d3web.testing.AbstractTest;
import de.d3web.testing.Message;
import de.d3web.testing.TestExecutor;
import de.d3web.testing.TestObjectContainer;
import de.d3web.testing.TestObjectProvider;
import de.d3web.testing.TestSpecification;

import static org.junit.Assert.assertEquals;

/**
 * The test objects of a specification must start in the order of their provider, also when they wait in the
 * priority queue of the executor service.
 */
public class TestExecutorOrderTest {

	private static final int OBJECTS = 40;

	@Test(timeout = 10000)
	public void testObjectsStartInProviderOrder() {
		// one thread and a priority queue, like the executor services of the CI builds
		PriorityBlockingQueue<Runnable> queue = new PriorityBlockingQueue<>();
		ThreadPoolExecutor mainTestExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, queue);
		ExecutorService subTestExecutor = Executors.newSingleThreadExecutor();
		try {
			List<String> names = IntStream.range(0, OBJECTS).mapToObj(i -> "object " + i).toList();
			List<String> started = Collections.synchronizedList(new ArrayList<>());
			TestSpecification<String> specification = new TestSpecification<>(
					new RecordingTest(started, queue), "object .*", new String[0], new String[0][]);
			TestObjectProvider provider = new TestObjectProvider() {
				@Override
				public <T> List<TestObjectContainer<T>> getTestObjects(Class<T> clazz, String name) {
					return names.stream().map(n -> new TestObjectContainer<>(n, clazz.cast(n))).toList();
				}
			};
			TestExecutor executor = new TestExecutor(List.of(provider), List.of(specification), (percent, message) -> {
			}, mainTestExecutor, subTestExecutor, 10);

			executor.run();

			assertEquals(names, started);
		}
		finally {
			mainTestExecutor.shutdownNow();
			subTestExecutor.shutdownNow();
		}
	}

	private static class RecordingTest extends AbstractTest<String> {

		private final List<String> started;
		private final PriorityBlockingQueue<Runnable> queue;

		private RecordingTest(List<String> started, PriorityBlockingQueue<Runnable> queue) {
			this.started = started;
			this.queue = queue;
		}

		@Override
		public Message execute(TestSpecification<String> specification, String testObject) throws InterruptedException {
			if (started.isEmpty()) {
				// the first test holds the only thread until all others wait in the queue
				while (queue.size() < OBJECTS - 1) Thread.sleep(1);
			}
			started.add(testObject);
			return Message.SUCCESS;
		}

		@Override
		public String getName() {
			return "recording-test";
		}

		@Override
		public Class<String> getTestObjectClass() {
			return String.class;
		}

		@Override
		public String getDescription() {
			return "Test fixture recording the start order of its test objects.";
		}
	}
}

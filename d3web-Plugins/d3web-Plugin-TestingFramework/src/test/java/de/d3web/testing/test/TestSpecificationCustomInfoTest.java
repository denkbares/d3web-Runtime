package de.d3web.testing.test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import de.d3web.testing.AbstractTest;
import de.d3web.testing.BuildResult;
import de.d3web.testing.Message;
import de.d3web.testing.TestExecutor;
import de.d3web.testing.TestObjectContainer;
import de.d3web.testing.TestObjectProvider;
import de.d3web.testing.TestSpecification;

import static org.junit.Assert.assertEquals;

/**
 * A test that stores custom infos in its specification during a build must still get its result.
 */
public class TestSpecificationCustomInfoTest {

	@Test(timeout = 5000)
	public void customInfosStoredDuringBuildKeepTheResult() {
		ExecutorService mainTestExecutor = Executors.newSingleThreadExecutor();
		ExecutorService subTestExecutor = Executors.newSingleThreadExecutor();
		try {
			TestSpecification<String> specification = new TestSpecification<>(
					new CustomInfoTest(), "test-object", new String[0], new String[0][]);
			TestObjectProvider provider = new TestObjectProvider() {
				@Override
				public <T> List<TestObjectContainer<T>> getTestObjects(Class<T> clazz, String name) {
					return List.of(new TestObjectContainer<>(name, clazz.cast(name)));
				}
			};
			TestExecutor executor = new TestExecutor(List.of(provider), List.of(specification), (percent, message) -> {
			}, mainTestExecutor, subTestExecutor, 10);

			executor.run();

			BuildResult build = executor.getBuildResult();
			assertEquals(1, build.getResults().size());
			assertEquals(Message.Type.SUCCESS, build.getOverallResult());
		}
		finally {
			mainTestExecutor.shutdownNow();
			subTestExecutor.shutdownNow();
		}
	}

	private static class CustomInfoTest extends AbstractTest<String> {

		@Override
		public Message execute(TestSpecification<String> specification, String testObject) {
			specification.setCustomInfo("state", new Object());
			return Message.SUCCESS;
		}

		@Override
		public String getName() {
			return "custom-info-test";
		}

		@Override
		public Class<String> getTestObjectClass() {
			return String.class;
		}

		@Override
		public String getDescription() {
			return "Test fixture storing a custom info.";
		}
	}
}

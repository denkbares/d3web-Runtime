/*
 * Copyright (C) 2023 denkbares GmbH, Germany
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

package de.d3web.costbenefit.inference.extender;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.denkbares.plugin.test.InitPluginManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.costbenefit.TestPath;
import de.d3web.costbenefit.inference.CostBenefitProperties;
import de.d3web.costbenefit.model.Path;

/**
 * Test the {@link de.d3web.costbenefit.inference.extender.PathSorter} algorithm
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 31.10.2023
 */
public class TestPathSorter {

	@BeforeClass
	public static void init() throws IOException {
		InitPluginManager.init();
	}

	@Test
	public void testVerySimplePaths() {
		testSorting(List.of(), List.of());
		TestContainer a = tc("A", 0);
		TestContainer b = tc("B", 0);
		TestContainer c = tc("C", 0);

		testSorting(List.of(a), List.of(a));
		testSorting(
				List.of(a, b, c),
				List.of(a, b, c));

		TestContainer b2 = tc("B2", -1);
		testSorting(
				List.of(a, b2, c),
				List.of(b2, a, c));
	}

	@Test
	public void testSimplePaths() {
		TestContainer a = tc("A", 0);
		TestContainer b = tc("B", -1);
		TestContainer c = tc("C", 0, b);
		TestContainer d = tc("D", 2, b);
		TestContainer e = tc("E", 1, b, a, c);
		testSorting(List.of(a, b, d), List.of(b, a, d));
		testSorting(List.of(a, b, c, d), List.of(b, a, c, d));
		testSorting(List.of(a, b, c, d, e), List.of(b, a, c, e, d));
	}

	@Test
	public void testStablePaths() {
		TestContainer a = tc("A", 0);
		TestContainer b = tc("B", 0);
		TestContainer c = tc("C", 0);
		TestContainer d = tc("D", 0, c);
		TestContainer e = tc("E", 0, d);
		TestContainer f = tc("F", 0);
		TestContainer g = tc("G", 0);
		TestContainer h = tc("H", 0);
		TestContainer i = tc("I", 0);
		TestContainer j = tc("J", 0, e, f, g);
		TestContainer k = tc("K", 0);
		TestContainer l = tc("L", 0);
		TestContainer m = tc("M", 0);
		TestContainer n = tc("N", 0);
		TestContainer o = tc("O", 0, a);
		TestContainer p = tc("P", 0);
		TestContainer q = tc("Q", 0);
		TestContainer r = tc("R", 0);
		TestContainer s = tc("S", 0, n, c, d);
		TestContainer t = tc("T", 0);
		TestContainer u = tc("U", 0);
		TestContainer v = tc("V", 0);
		TestContainer w = tc("W", 0);
		TestContainer x = tc("X", 0);
		TestContainer y = tc("Y", 0);
		TestContainer z = tc("Z", 0);

		// if we don't have any path order, don't change anything, don't be slow
		testSorting(
				List.of(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z),
				List.of(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z));
	}

	@Test
	public void testComplexPaths() {
		TestContainer a = tc("A", 0);
		TestContainer b = tc("B", 0);
		TestContainer c = tc("C", 0);
		TestContainer d = tc("D", 0, c);
		TestContainer e = tc("E", -1, d); // cannot move
		TestContainer f = tc("F", 0);
		TestContainer g = tc("G", 0);
		TestContainer h = tc("H", 0);
		TestContainer i = tc("I", 0);
		TestContainer j = tc("J", -1, e, f, g); // must not move before g -> ...g, j, h,...
		TestContainer k = tc("K", 0);
		TestContainer l = tc("L", 0);
		TestContainer m = tc("M", 0);
		TestContainer n = tc("N", 3); // must not move after s -> ...r, n, s,...
		TestContainer o = tc("O", 0, a);
		TestContainer p = tc("P", -1); // move to third
		TestContainer q = tc("Q", 0);
		TestContainer r = tc("R", 0);
		TestContainer s = tc("S", 0, n, c, d);
		TestContainer t = tc("T", 0);
		TestContainer u = tc("U", 0);
		TestContainer v = tc("V", 4); // move to end
		TestContainer w = tc("W", 3); // move before end
		TestContainer x = tc("X", -2); // move to second
		TestContainer y = tc("Y", -5); // move to start
		TestContainer z = tc("Z", 0);

		testSorting(
				List.of(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z),
				List.of(y, x, p, a, b, c, d, e, f, g, j, h, i, k, l, m, o, q, r, n, s, t, u, z, w, v));
	}

	private TestContainer tc(String name, double pathOrder) {
		return tc(name, pathOrder, new TestContainer[0]);
	}

	private TestContainer tc(String name, double pathOrder, TestContainer... dependsOn) {
		return new TestContainer(name, pathOrder, Arrays.asList(dependsOn));
	}

	private void testSorting(List<TestContainer> pathToSort, List<TestContainer> expectedPath) {
		new TestCase(pathToSort, expectedPath).run();
	}

	private static class TestCase {

		private final List<TestContainer> pathToSort;

		private final Map<String, TestContainer> containerMap = new HashMap<>();
		private final List<TestContainer> expectedPath;

		private final KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();

		private final Session session = SessionFactory.createSession(kb);

		public TestCase(List<TestContainer> pathToSort, List<TestContainer> expectedPath) {
			this.pathToSort = pathToSort;
			this.expectedPath = expectedPath;
			for (TestContainer testContainer : pathToSort) {
				containerMap.put(testContainer.name, testContainer);
				QContainer qContainer = new QContainer(kb, testContainer.name);
				qContainer.getInfoStore().addValue(CostBenefitProperties.PATH_ORDER, testContainer.pathOrder);
			}
		}

		private Path toPath(List<TestContainer> containers) {
			return new TestPath(containers.stream().map(c -> kb.getManager().searchQContainer(c.name)).toList());
		}

		private List<TestContainer> toContainerList(Path path) {
			return toContainerList(path.getPath());
		}

		@NotNull
		private List<TestContainer> toContainerList(List<QContainer> qContainers) {
			return qContainers.stream().map(this::toTestContainer).toList();
		}

		@NotNull
		private TestPathSorter.TestContainer toTestContainer(QContainer qContainer) {
			TestContainer testContainer = containerMap.get(qContainer.getName());
			if (testContainer == null) {
				throw new IllegalArgumentException("No test container found with name: " + qContainer.getName());
			}
			return testContainer;
		}

		public void run() {
			Map<String, Set<TestContainer>> toSort = pathToSort.stream()
					.collect(Collectors.groupingBy(TestContainer::name, Collectors.toSet()));
			Map<String, Set<TestContainer>> expected = expectedPath.stream()
					.collect(Collectors.groupingBy(TestContainer::name, Collectors.toSet()));
			Assert.assertEquals("Test definition faulty", expected, toSort);

			Assert.assertEquals("Path to sort and expected path must have the same length", pathToSort.size(), expectedPath.size());
			Path sortedPath = new SimplePathSorter().sortPath(session, toPath(pathToSort));
			List<TestContainer> containerList = toContainerList(sortedPath);
			Assert.assertEquals("Sorting did not yield the expected order", toString(expectedPath), toString(containerList));
			// just to make sure we don't lose anything in our toString method
			Assert.assertEquals("Sorting did not yield the expected order", expectedPath, containerList);
		}

		private String toString(List<TestContainer> expectedPath) {
			return expectedPath.stream().map(TestContainer::toString).collect(Collectors.joining("\n"));
		}

		private class SimplePathSorter extends PathSorter {
			@Override
			protected boolean isValidPath(Session copy, List<QContainer> path) {
				for (int i = 0; i < path.size(); i++) {
					QContainer qContainer = path.get(i);
					if (!toContainerList(path.subList(0, i)).containsAll(toTestContainer(qContainer).dependsOn)) {
						return false;
					}
				}
				return true;
			}
		}
	}

	private record TestContainer(String name, double pathOrder, List<TestContainer> dependsOn) {

		private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(" #.#;-#.#");

		@Override
		public String toString() {
			return "(" + name + " " + format(pathOrder) + " " + dependsOn.stream().map(d -> d.name).toList() + ")";
		}

		private String format(double d) {
			return DECIMAL_FORMAT.format(d);
		}
	}
}

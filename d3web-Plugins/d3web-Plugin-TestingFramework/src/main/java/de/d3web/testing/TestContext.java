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
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.testing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * The context a test is executed in. It gives a running test access to the test objects of its build, looked up the
 * same way the {@link TestExecutor} looks up the test objects of the tests, e.g. the knowledge bases a test case is
 * checked against. The executor passes the context to {@link Test#execute(TestSpecification, Object, TestContext)}.
 *
 * @author Albrecht Striffler (denkbares GmbH) + Claude for env-instance
 * @created 16.10.2026
 */
public final class TestContext {

	private final List<TestObjectProvider> providers;

	/**
	 * Creates a context that serves the test objects of the specified providers.
	 *
	 * @param providers the providers of the test objects of the build
	 */
	public TestContext(@NotNull Collection<TestObjectProvider> providers) {
		this.providers = List.copyOf(providers);
	}

	/**
	 * Creates a context that serves the test objects of the plugged and registered providers of {@link
	 * TestObjectProviderManager}. Only for tests that are executed without a {@link TestExecutor}: inside a build, the
	 * executor's context has to be used, since its providers may be bound to the build (e.g. to a wiki environment).
	 *
	 * @return a context of the plugged and registered providers
	 */
	@NotNull
	public static TestContext ofPluggedProviders() {
		return new TestContext(TestObjectProviderManager.getTestObjectProviders());
	}

	/**
	 * Returns the test objects of the specified class whose names match the specified name, collected from all
	 * providers of this context, see {@link TestObjectProvider#getTestObjects(Class, String)}.
	 *
	 * @param clazz the class of the test objects
	 * @param name  the name (or regex) of the test objects
	 * @return the matching test objects, never null
	 */
	@NotNull
	public <T> List<TestObjectContainer<T>> getTestObjects(@NotNull Class<T> clazz, @NotNull String name) {
		List<TestObjectContainer<T>> result = new ArrayList<>();
		for (TestObjectProvider provider : providers) {
			result.addAll(provider.getTestObjects(clazz, name));
		}
		return result;
	}
}

/*
 * Copyright (C) 2024 denkbares GmbH, Germany
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

/**
 * A test that makes use of an executor service to further parallelize test tasks...
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 24.06.2024
 */
public interface ParallelizedTest {

	void registerTestTaskConsumer(TestTaskHandler testTaskConsumer);

	interface TestTaskHandler {

		/**
		 * Add a test task, it will automatically be queued in the executor, but we do not wait for the task to be done
		 * in this method, use awaitSubtasks for this purpose.
		 */
		void accept(TestTask testTask);

		/**
		 * Wait til all previously registered sub-task are done
		 */
		void awaitSubtasks();
	}

	@FunctionalInterface
	interface TestTask {
		void run() throws InterruptedException;
	}
}

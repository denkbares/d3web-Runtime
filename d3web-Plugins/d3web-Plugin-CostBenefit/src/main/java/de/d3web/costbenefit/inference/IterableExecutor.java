/*
 * Copyright (C) 2020 denkbares GmbH, Germany
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
package de.d3web.costbenefit.inference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to decorate an executor service and allow to iterate over all futures results of the submitted callables.
 *
 * @author volker_belli
 * @created 09.09.2011
 */
public class IterableExecutor<T> implements Iterable<Future<T>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(IterableExecutor.class);

	// share one thread pool among the whole virtual machine
	private static final int threadCount = Runtime.getRuntime().availableProcessors() * 3 / 2;
	private static ExecutorService threadPool = null;

	/**
	 * A decorating {@link Callable}. If the delegated callable has finished its task, it adds the itself to our result
	 * list to notify the IterableExecutor that new results are available. Therefore it also gets the future set after
	 * it has been submitted to easily access the returned result after notification.
	 * <p>
	 * The user of this class has to make sure that the future has been initialized before using it (which usually
	 * happens asynchronously).
	 *
	 * @author volker_belli
	 * @created 09.09.2011
	 */
	private class Worker implements Callable<T> {

		private final Callable<T> callable;
		private Future<T> future;

		public Worker(Callable<T> callable) {
			this.callable = callable;
		}

		@Override
		public T call() throws Exception {
			try {
				return callable.call();
			}
			finally {
				addFinishedWorker(this);
			}
		}
	}

	private final ExecutorService service;
	private final List<Worker> finishedWorkers = Collections.synchronizedList(new ArrayList<Worker>());
	private int expectedResultCount = 0;

	public IterableExecutor(ExecutorService service) {
		this.service = service;
	}

	/**
	 * Creates a new IterableExecutor. All iterators share a common set of threats.
	 *
	 * @return the created IterableExecutor
	 * @created 14.09.2011
	 */
	public static <T> IterableExecutor<T> createExecutor() {
		// initialize thread pool if not exists
		if (threadPool == null) {
			threadPool = Executors.newFixedThreadPool(threadCount, IterableExecutor::newDemon);
			LOGGER.info("created multicore thread pool of size " + threadCount);
		}
		// and return new executor based on the thread pool
		return new IterableExecutor<>(threadPool);
	}

	/**
	 * Creates a new demon thread for the thread pool.
	 */
	private static Thread newDemon(Runnable runnable) {
		Thread thread = Executors.defaultThreadFactory().newThread(runnable);
		thread.setDaemon(true);
		return thread;
	}

	public static int getThreadCount() {
		return threadCount;
	}

	/**
	 * Submits a callable to this executor. Due to waiting for the results using the iterator, the return value of this
	 * method is ignored in most cases.
	 *
	 * @param callable the task to be executed
	 * @return the future for this task
	 * @created 09.09.2011
	 */
	public synchronized Future<T> submit(Callable<T> callable) {
		expectedResultCount++;
		Worker worker = new Worker(callable);
		Future<T> future = service.submit(worker);
		worker.future = future;
		return future;
	}

	/**
	 * Gets a Worker that has finished at a specific index. The indexes are ordered by the time the workers has finished
	 * their tasks. If a requested worker has not been finished yet (but we are expecting its result), this methods
	 * waits for that worker to be completed.
	 *
	 * @param index the index of the result to be requested
	 * @return the Worker finished at the index position
	 * @created 11.09.2011
	 */
	private synchronized Worker getFinishedWorker(int index) {
		// check if index is valid
		if (index < 0 || index >= expectedResultCount) {
			throw new ArrayIndexOutOfBoundsException();
		}
		// check if requested index is not available yet
		// --> wait for it
		while (index >= finishedWorkers.size()) {
			try {
				this.wait();
			}
			catch (InterruptedException e) {
				LOGGER.error("wait interrupted", e);
			}
		}
		return finishedWorkers.get(index);
	}

	/**
	 * Adds a finished worker to the list. Notifies ourself if an iterator is awaiting results.
	 *
	 * @param worker the Worker that has finished
	 * @created 11.09.2011
	 */
	private synchronized void addFinishedWorker(Worker worker) {
		// add worker to out result list
		finishedWorkers.add(worker);
		// and wake up if we are waiting for more workers to be finished
		this.notifyAll();
	}

	/**
	 * Returns an iterator over the results. The order of the results is, as the executor finishes the submitted tasks.
	 * It is guaranteed that every result is iterated. The iterator blocks if no result is available, but it is still
	 * waiting for additional results.
	 *
	 * @return an iterator for all results as they are completed
	 */
	@NotNull
	@Override
	public Iterator<Future<T>> iterator() {
		return new Iterator<Future<T>>() {

			int index = 0;

			@Override
			public boolean hasNext() {
				return index < expectedResultCount;
			}

			@Override
			public Future<T> next() {
				try {
					// check if we have to wait for more results
					Worker nextWorker = getFinishedWorker(index++);
					return nextWorker.future;
				}
				catch (ArrayIndexOutOfBoundsException e) {
					throw new NoSuchElementException();
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}

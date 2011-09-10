/*
 * Copyright (C) 2011 denkbares GmbH, Germany
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
package de.d3web.costbenefit.inference.astar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * 
 * @author volker_belli
 * @created 09.09.2011
 */
public class IterableExecutor<T> implements Iterable<Future<T>> {

	private static final Logger log = Logger.getLogger(IterableExecutor.class.getName());

	/**
	 * Adds the result to our list and notifies us
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
			T result = callable.call();
			addFinishedWorker(this);
			return result;
		}
	}

	private final ExecutorService service;
	private final List<Worker> finishedWorkers = Collections.synchronizedList(new ArrayList<Worker>());
	private int expectedResultCount = 0;

	public IterableExecutor(ExecutorService service) {
		this.service = service;
	}

	/**
	 * Submits a callable to this executor. Due to waiting for the results using
	 * the iterator, the return value of this method is ignored in most cases.
	 * 
	 * @created 09.09.2011
	 * @param callable the task to be executed
	 * @return the future for this task
	 */
	public synchronized Future<T> submit(Callable<T> callable) {
		expectedResultCount++;
		Worker worker = new Worker(callable);
		Future<T> future = service.submit(worker);
		worker.future = future;
		return future;
	}

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
				log.log(Level.SEVERE, "wait interrupted", e);
			}
		}
		return finishedWorkers.get(index);
	}

	private synchronized void addFinishedWorker(Worker worker) {
		// add worker to out result list
		finishedWorkers.add(worker);
		// and wake up if we are waiting for more workers to be finished
		this.notifyAll();
	}

	/**
	 * Returns an iterator over the results. The order of the results is, as the
	 * executor finishes the submitted tasks. It is guaranteed that every result
	 * is iterated. The iterator blocks if no result is available, but it is
	 * still waiting for additional results.
	 * 
	 * @return an iterator for all results
	 */
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
				// check if we have to wait for more results
				Worker nextWorker = getFinishedWorker(index);
				index++;
				return nextWorker.future;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}

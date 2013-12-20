/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.concurrent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * This executor collects a set of data objects and process them all in one
 * single execute method. After processing them, all futures will share the same
 * result object.
 * <p>
 * You may use this class if you have a set of incoming time-consuming
 * operations that can be processed more quickly if they will be combined. In
 * this case you should create an instance from an executor class derived from
 * this abstract class. The executor class should overwrites the
 * {@link #process(List)} method to process a list of data. Submit all incoming
 * operations to the executor and for at least one (or all) call process. For
 * each submitted data you will get a future to get the result later on.
 * <p>
 * Example:<br>
 * Imagine you get AJAX requests from the client for time-consuming server
 * operations. If the clients works to quickly, the requests get stuck on the
 * server. Use to following meta-code to process the requests individually, but
 * combine them if more than one is incoming before the previous one is
 * completed:
 * 
 * <pre>
 * // place this somewhere in you server 
 * private static final CombineExecutor<Data, Result> executor = new CombineExecutor<Data, Result>() {
 * 	public Result process(List<Data> data) {
 * 		// do your processing here
 * 	}
 * }
 * 
 * // and use this code to process the requests 
 * // use this instead of processing the object "data" 
 * Future<Result> future = executor.submit(data);
 * executor.process();
 * // note that "get()" must be called, even if you do not require the result!
 * Result result = future.get();
 * </pre>
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 19.12.2013
 */
public abstract class CombineExecutor<Data, Result> {

	private final List<CombineFuture<Data, Result>> pending = new LinkedList<CombineFuture<Data, Result>>();
	public volatile boolean awaitProcessing = false;

	public Future<Result> submit(Data data) {
		CombineFuture<Data, Result> future = new CombineFuture<Data, Result>(data);
		synchronized (pending) {
			pending.add(future);
		}
		return future;
	}

	/**
	 * Activates the processing of all pending data that have been submitted to
	 * this executor. There will only be one processing executed at one time. If
	 * the method is called multiple times simultaneously the particular calls
	 * are waiting to the current one to process. After that, one of the calls
	 * is used to process all data objects. The other calls will return
	 * immediately after processing.
	 * 
	 * @created 19.12.2013
	 * @see #submit(Object)
	 */
	public void process() {
		synchronized (pending) {
			// check if there is anything left to do
			if (pending.isEmpty()) return;
			// check if we already wait for some processing
			if (awaitProcessing) return;
			awaitProcessing = true;
		}

		// with this synchronized we wait for a running process
		// call to complete. During that time, some additional
		// data objects may be submitted and added to the pending
		// list. All calls to process will be returned immediately,
		// because we still have this thread waiting to process
		// these data objects
		synchronized (this) {
			// get the list of currently pending futures
			// and inform that
			List<CombineFuture<Data, Result>> items;
			synchronized (pending) {
				items = new ArrayList<CombineFuture<Data, Result>>(pending);
				pending.clear();
				// from here on, we require an other process thread
				// to process incoming new pending data objects
				awaitProcessing = false;
			}

			// prepare the list of their data objects to process
			List<Data> data = new ArrayList<Data>(items.size());
			for (CombineFuture<Data, Result> item : items) {
				if (item.isCancelled()) continue;
				data.add(item.getData());
			}
			try {
				// process the data objects and capture return value
				Result result = process(data);
				// and complete all futures with the common result
				for (CombineFuture<Data, Result> item : items) {
					if (item.isCancelled()) continue;
					item.completed(result);
				}
			}
			catch (Exception e) {
				// if there has been an error during processing,
				// complete the futures with some error
				for (CombineFuture<Data, Result> item : items) {
					if (item.isCancelled()) continue;
					item.failed(e);
				}
			}
		}
	}

	protected abstract Result process(List<Data> items) throws Exception;
}

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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Class that represents a future having a common process method to share with
 * the other futures of the same {@link CombineExecutor}. All futures of the
 * same CombineExecutor will be processed at one, sharing the same result object
 * instance.
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 19.12.2013
 */
class CombineFuture<Data, Result> implements Future<Result> {

	private final Data data;

	private volatile boolean completed;
	private volatile boolean cancelled;
	private volatile Result result;
	private volatile Exception exception;

	CombineFuture(Data data) {
		this.data = data;
	}

	Data getData() {
		return data;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public boolean isDone() {
		return this.completed;
	}

	private Result getResult() throws ExecutionException {
		if (this.exception != null) {
			throw new ExecutionException(this.exception);
		}
		return this.result;
	}

	@Override
	public synchronized Result get() throws InterruptedException, ExecutionException {
		while (!this.completed) {
			wait();
		}
		return getResult();
	}

	@Override
	public synchronized Result get(long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		long msecs = unit.toMillis(timeout);
		long startTime = (msecs <= 0) ? 0 : System.currentTimeMillis();
		long waitTime = msecs;
		if (this.completed) {
			return getResult();
		}
		else if (waitTime <= 0) {
			throw new TimeoutException();
		}
		else {
			for (;;) {
				wait(waitTime);
				if (this.completed) {
					return getResult();
				}
				else {
					waitTime = msecs - (System.currentTimeMillis() - startTime);
					if (waitTime <= 0) {
						throw new TimeoutException();
					}
				}
			}
		}
	}

	synchronized boolean completed(Result result) {
		if (this.completed) {
			return false;
		}
		this.completed = true;
		this.result = result;
		notifyAll();
		return true;
	}

	synchronized boolean failed(Exception exception) {
		if (this.completed) {
			return false;
		}
		this.completed = true;
		this.exception = exception;
		notifyAll();
		return true;
	}

	@Override
	public synchronized boolean cancel(boolean mayInterruptIfRunning) {
		if (this.completed) {
			return false;
		}
		this.completed = true;
		this.cancelled = true;
		notifyAll();
		return true;
	}
}

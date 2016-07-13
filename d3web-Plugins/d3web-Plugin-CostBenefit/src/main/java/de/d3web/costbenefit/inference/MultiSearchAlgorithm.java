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
package de.d3web.costbenefit.inference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import de.d3web.core.session.Session;
import de.d3web.costbenefit.inference.astar.IterableExecutor;
import de.d3web.costbenefit.model.SearchModel;
import com.denkbares.utils.Log;

/**
 * A new {@link SearchAlgorithm} that delegates the search to a sequence of
 * search algorithms and apply each search function on the same search model
 * until the optimal path has been found.
 * <p>
 * To optimize searching, each search function should ensure that is will stop
 * searching if no better path could be found.
 * 
 * @author volker_belli
 * @created 01.09.2011
 */
public class MultiSearchAlgorithm implements SearchAlgorithm {

	public enum Mode {
		continued, merged, parallel
	}

	private static class Worker implements Callable<SearchModel> {

		private final SearchAlgorithm algorithm;
		private final SearchModel model;
		private final Session session;

		public Worker(SearchAlgorithm algorithm, Session session, SearchModel model) {
			this.algorithm = algorithm;
			this.session = session;
			this.model = model;
		}

		@Override
		public SearchModel call() throws Exception {
			this.algorithm.search(session, model);
			return model;
		}
	}

	private final List<SearchAlgorithm> delegates = new LinkedList<>();
	private Mode mode = Mode.parallel;

	@Override
	public void search(Session session, SearchModel model) {
		switch (getMode()) {
		case continued:
			for (SearchAlgorithm searchAlgorithm : delegates) {
				// before searching, reset abort flag from the previous search
				model.setAbort(false);
				searchAlgorithm.search(session, model);
				// if one search has completed successfully, we are finished
				if (!model.isAborted()) break;
			}
			break;

		case parallel:
			// mode: independent, threaded calculation for each search
			IterableExecutor<SearchModel> executor = IterableExecutor.createExecutor();
			List<Worker> workers = new LinkedList<>();
			boolean succeeded = false;
			for (SearchAlgorithm searchAlgorithm : delegates) {
				SearchModel result = model.clone();
				Worker worker = new Worker(searchAlgorithm, session, result);
				executor.submit(worker);
				workers.add(worker);
			}
			for (Future<SearchModel> future : executor) {
				try {
					SearchModel result = future.get();
					if (!result.isAborted()) {
						// if any is succeeded we have the best result
						succeeded = true;
						// and can break all other searches
						for (Worker worker : workers) {
							worker.model.setAbort(true);
						}
					}
					model.merge(result);
				}
				catch (InterruptedException | ExecutionException e) {
					Log.severe("error in cost/benefit search thread", e);
				}
			}
			// if no search task has succeeded,
			// we also tell the merged model that all searches has been aborted
			if (!succeeded) model.setAbort(true);
			break;

		case merged:
			// mode: independent, non-threaded calculation for each search
			List<SearchModel> results = new ArrayList<>();
			for (SearchAlgorithm searchAlgorithm : delegates) {
				SearchModel result = model.clone();
				searchAlgorithm.search(session, result);
				results.add(result);
				// if we have found the best result (and not aborted before)
				// we stop and do not search with the following algorithms
				if (!result.isAborted()) break;
			}
			// merge all results (until the first non-aborted one)
			for (SearchModel result : results) {
				model.merge(result);
			}
			break;

		default:
			throw new IllegalStateException();
		}
	}

	public List<SearchAlgorithm> getSearchAlgorithms() {
		return Collections.unmodifiableList(delegates);
	}

	public void addSearchAlgorithms(SearchAlgorithm searchAlgorithm) {
		delegates.add(searchAlgorithm);
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Mode getMode() {
		return mode;
	}

}

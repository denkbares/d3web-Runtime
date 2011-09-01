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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.d3web.core.session.Session;
import de.d3web.costbenefit.model.SearchModel;

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
	};

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

	private final List<SearchAlgorithm> delegates = new LinkedList<SearchAlgorithm>();
	private Mode mode = Mode.merged;

	@Override
	public void search(Session session, SearchModel model) {
		switch (getMode()) {
		case continued:
			// mode: interact
			for (SearchAlgorithm searchAlgorithm : delegates) {
				searchAlgorithm.search(session, model);
			}
			break;

		case parallel:
			// mode: parallel (threaded)
			ExecutorService executor = Executors.newFixedThreadPool(delegates.size());
			List<Future<SearchModel>> futures = new ArrayList<Future<SearchModel>>();
			for (SearchAlgorithm searchAlgorithm : delegates) {
				SearchModel result = model.clone();
				Worker worker = new Worker(searchAlgorithm, session, result);
				Future<SearchModel> submit = executor.submit(worker);
				futures.add(submit);
			}
			for (Future<SearchModel> future : futures) {
				try {
					model.merge(future.get());
				}
				catch (InterruptedException e) {
					Logger.getLogger(getClass().getName()).log(Level.SEVERE,
							"error in cost/benefit search thread", e);
				}
				catch (ExecutionException e) {
					Logger.getLogger(getClass().getName()).log(Level.SEVERE,
							"error in cost/benefit search thread", e);
				}
			}
			executor.shutdown();
			break;

		case merged:
			// mode: parallel (non-threaded)
			List<SearchModel> results = new ArrayList<SearchModel>();
			for (SearchAlgorithm searchAlgorithm : delegates) {
				SearchModel result = model.clone();
				searchAlgorithm.search(session, result);
				results.add(result);
			}
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
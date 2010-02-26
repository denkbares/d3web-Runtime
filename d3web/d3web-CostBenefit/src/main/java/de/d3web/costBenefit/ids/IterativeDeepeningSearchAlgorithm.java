/*
 * Copyright (C) 2009 denkbares GmbH
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.costBenefit.ids;

import de.d3web.core.session.XPSCase;
import de.d3web.costBenefit.AbortStrategy;
import de.d3web.costBenefit.SearchAlgorithm;
import de.d3web.costBenefit.model.SearchModel;

/**
 * Encapsulates the call of a new IterativeDeepeningSearch
 * search. For each call a new instance of the IterativeDeepeningSearch is
 * created.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class IterativeDeepeningSearchAlgorithm implements SearchAlgorithm {

	private AbortStrategy abortStrategy;
	
	@Override
	public void search(XPSCase theCase, SearchModel model) {
		IterativeDeepeningSearch iterativeDeepeningSearch = new IterativeDeepeningSearch(model);
		if (abortStrategy!=null) iterativeDeepeningSearch.setAbortStrategy(abortStrategy);
		iterativeDeepeningSearch.search(theCase);
	}

	public AbortStrategy getAbortStrategy() {
		return abortStrategy;
	}

	public void setAbortStrategy(AbortStrategy abortStrategy) {
		this.abortStrategy = abortStrategy;
	}
	
	
}

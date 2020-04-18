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
package de.d3web.costbenefit.inference.extender;

import java.util.Arrays;
import java.util.List;

import com.denkbares.utils.Stopwatch;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionObjectSource;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.costbenefit.CostBenefitUtil;
import de.d3web.costbenefit.inference.SearchAlgorithm;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;
import de.d3web.costbenefit.model.Target;

/**
 * Extends the path of the sub SearchAlgorithm by adding specially comfortBenefit {@link QContainer} to the path, if it
 * doesn't destroy the path
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 07.11.2011
 */
public class PathExtender implements SearchAlgorithm, SessionObjectSource<PathExtender.PathExtenderInfo> {

	private final SearchAlgorithm delegate;
	private final List<PathModifier> modifiers = Arrays.asList(new PathSorter(), new ComfortAdder());

	public PathExtender(SearchAlgorithm algorithm) {
		delegate = algorithm;
	}

	public SearchAlgorithm getDelegateAlgorithm() {
		return delegate;
	}

	@Override
	public synchronized void search(Session session, SearchModel model) {
		// search using the delegate algorithm
		delegate.search(session, model);

		// rest the info object to reflect only the most recent search
		PathExtenderInfo info = session.getSessionObject(this);
		info.pathBeforeModification = null;
		info.pathAfterModification = null;

		// if no path is calculated, do nothing
		Target bestCostBenefitTarget = model.getBestCostBenefitTarget();
		if (bestCostBenefitTarget == null) return;
		info.pathBeforeModification = bestCostBenefitTarget.getMinPath();

		// otherwise apply modifications to the path / target
		Stopwatch stopwatch = new Stopwatch().start();
		Session copy = null;
		for (PathModifier modifier : modifiers) {
			if (modifier.canApply(model)) {
				// if at least one modifier will apply to the calculated path, create a session copy
				if (copy == null) {
					copy = CostBenefitUtil.createSearchCopy(session);
				}

				// and apply the modification to the path
				Path path = modifier.calculatePath(copy, model);
				bestCostBenefitTarget.setMinPath(path);
				info.pathAfterModification = path;
			}
		}
		CostBenefitUtil.log(stopwatch.getTime(), "Extending calculated path: " + stopwatch.getDisplay());
	}

	@Override
	public PathExtenderInfo createSessionObject(Session session) {
		return new PathExtenderInfo();
	}

	public static class PathExtenderInfo implements SessionObject {
		private Path pathBeforeModification = null;
		private Path pathAfterModification = null;

		/**
		 * Returns the path of the most recent path search, as it was before any modification has been applied.
		 */
		public Path getPathBeforeModification() {
			return pathBeforeModification;
		}

		/**
		 * Returns the path of the most recent path search, as it was after all modification has been applied.
		 */
		public Path getPathAfterModification() {
			return pathAfterModification;
		}
	}
}

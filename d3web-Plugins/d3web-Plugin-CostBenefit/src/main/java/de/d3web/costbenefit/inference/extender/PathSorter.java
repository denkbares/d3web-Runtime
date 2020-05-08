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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.Session;
import de.d3web.costbenefit.CostBenefitUtil;
import de.d3web.costbenefit.inference.CostBenefitProperties;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 24.02.2020
 */
class PathSorter implements PathModifier {
	@Override
	public boolean canApply(SearchModel model) {
		Path path = model.getBestCostBenefitPath();
		return path != null && path.getPath().stream().anyMatch(this::hasPathOrder);
	}

	@Override
	@NotNull
	public Path calculatePath(Session copiedSession, SearchModel model) {
		// only called when canApply succeeded
		Path path = model.getBestCostBenefitPath();
		assert path != null;
		List<QContainer> containers = path.getPath();

		// process the ordered items
		// first process the item with the highest (abs) priority value towards head/tail
		// then mark as processed, and move next ones, but not further than previously moved ones
		List<QContainer> order = containers.stream().filter(this::hasPathOrder)
				.sorted(Comparator.comparingDouble(qc -> -Math.abs(getPathOrder(qc)))).collect(Collectors.toList());
		int min = 0; // inclusively
		int max = containers.size() - 1; // inclusively
		for (QContainer container : order) {
			double priority = getPathOrder(container);
			int from = containers.indexOf(container);
			int to = from;

			// move towards head/tail, based on priority
			int delta = (priority < 0) ? -1 : 1;
			Path bestPath = null;
			while (min < to && to < max) {
				Path newPath = moveContainer(path, from, to + delta);
				if (!isValidPath(copiedSession, newPath)) break;
				// we found a new path
				bestPath = newPath;
				to += delta;
			}

			// if a new best path has been found, apply the path
			if (bestPath != null) {
				path = bestPath;
				containers = bestPath.getPath();
			}

			// and restrict the next items to be sorted not further than this item,
			// (even if if this item could not been moved, but is has a higher (abs) order value)
			if (priority < 0) {
				min = to + 1;
			}
			else {
				max = to - 1;
			}
		}

		return path;
	}

	private Path moveContainer(Path source, int from, int to) {
		List<QContainer> containers = new ArrayList<>(source.getPath());
		QContainer container = containers.remove(from);
		containers.add(to, container);
		return new ExtendedPath(containers, source);
	}

	private boolean isValidPath(Session copy, Path path) {
		copy = CostBenefitUtil.createDecoratedSession(copy);
		return CostBenefitUtil.checkPath(path.getPath(), copy, 0, true);
	}

	private boolean hasPathOrder(QContainer container) {
		return getPathOrder(container) != 0.0;
	}

	private double getPathOrder(QContainer container) {
		return container.getInfoStore().getValue(CostBenefitProperties.PATH_ORDER);
	}
}

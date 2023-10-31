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
		return sortPath(copiedSession, path);
	}

	@NotNull
	public Path sortPath(Session copiedSession, Path path) {
		List<QContainer> containers = path.getPath();

		List<QContainer> cleaned = new ArrayList<>(containers);

		// first, reorder the negativ path orders
		List<QContainer> negativeOrder = cleaned.stream()
				.filter(qc -> getPathOrder(qc) < 0)
				.sorted(Comparator.comparingDouble(this::getPathOrder))
				.collect(Collectors.toCollection(ArrayList::new));
		cleaned.removeAll(negativeOrder);
		List<QContainer> reorderedNegative = findValidReorderedPath(copiedSession, cleaned, negativeOrder, true);
		if (reorderedNegative == null) reorderedNegative = new ArrayList<>(path.getPath());
		List<QContainer> copyOfFirstStep = new ArrayList<>(reorderedNegative);

		// then, reorder the positiv path orders
		List<QContainer> positiveOrder = reorderedNegative.stream()
				.filter(qc -> getPathOrder(qc) > 0)
				.sorted(Comparator.comparingDouble(this::getPathOrder).reversed())
				.collect(Collectors.toCollection(ArrayList::new));
		reorderedNegative.removeAll(positiveOrder);
		List<QContainer> reorderedPositive = findValidReorderedPath(copiedSession, reorderedNegative, positiveOrder, false);
		if (reorderedPositive == null) { // reuse at least negative reordering
			reorderedPositive = copyOfFirstStep;
		}

		return new ExtendedPath(reorderedPositive, path);
	}

	private List<QContainer> findValidReorderedPath(Session copiedSession, List<QContainer> rest, List<QContainer> ordered, boolean negative) {
		if (rest.isEmpty()) {
			// try to permute equal path orders in this case?
			return isValidPath(copiedSession, ordered) ? ordered : null;
		}
		if (ordered.isEmpty()) {
			return isValidPath(copiedSession, rest) ? rest : null;
		}
		ArrayList<QContainer> orderedCopy = new ArrayList<>(ordered);

		QContainer nextInsert = orderedCopy.remove(0);
		double nextPathOrder = getPathOrder(nextInsert);
		if (negative) {
			// move the negativ path orders to the front
			int insert = getInsertIndex(rest, nextPathOrder);
			for (int i = insert; i < rest.size(); i++) {
				ArrayList<QContainer> restCopy = createCopyWithInsert(rest, i, nextInsert);
				List<QContainer> validReorderedPath = findValidReorderedPath(copiedSession, restCopy, orderedCopy, negative);
				if (validReorderedPath != null) return validReorderedPath;
			}
		}
		else {
			// the positives to the back...
			int insertStop = getInsertStopIndex(rest, nextPathOrder);
			for (int i = insertStop; i >= 0; i--) {
				ArrayList<QContainer> restCopy = createCopyWithInsert(rest, i, nextInsert);
				List<QContainer> validReorderedPath = findValidReorderedPath(copiedSession, restCopy, orderedCopy, negative);
				if (validReorderedPath != null) return validReorderedPath;
			}
		}
		return null;
	}

	@NotNull
	private static ArrayList<QContainer> createCopyWithInsert(List<QContainer> rest, int i, QContainer nextInsert) {
		ArrayList<QContainer> restCopy = new ArrayList<>(rest.size() + 1);
		restCopy.addAll(rest);
		restCopy.add(i, nextInsert);
		return restCopy;
	}

	private int getInsertIndex(List<QContainer> rest, double nextPathOrder) {
		int insert = rest.size() - 1;
		for (int i = rest.size() - 1; i >= 0; i--) {
			double pathOrder = getPathOrder(rest.get(i));
			if (nextPathOrder > pathOrder) break;
			insert = i;
		}
		return insert;
	}

	private int getInsertStopIndex(List<QContainer> rest, double nextPathOrder) {
		int insertStop = 0;
		for (int i = 0; i < rest.size(); i++) {
			double currentPathOrder = getPathOrder(rest.get(i));
			if (currentPathOrder > nextPathOrder) break;
			// the element at i is <= element to insert -> we can insert after the element at i
			insertStop = i + 1;
		}
		return insertStop; // we want to insert after the found element/index
	}

	protected boolean isValidPath(Session copy, List<QContainer> path) {
		copy = CostBenefitUtil.createDecoratedSession(copy);
		return CostBenefitUtil.checkPath(path, copy, 0, true);
	}

	private boolean hasPathOrder(QContainer container) {
		return getPathOrder(container) != 0.0;
	}

	private double getPathOrder(QContainer container) {
		return container.getInfoStore().getValue(CostBenefitProperties.PATH_ORDER);
	}
}

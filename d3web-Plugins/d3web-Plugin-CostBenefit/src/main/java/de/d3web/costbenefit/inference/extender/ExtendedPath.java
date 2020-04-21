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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.costbenefit.model.Path;

/**
 * Path implementation that represents a modified path based on a originally searched path.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 24.02.2020
 */
public final class ExtendedPath implements Path {

	private final List<QContainer> qContainers;
	private final double costs;
	private final double negativeCosts;
	private final Path originalPath;

	ExtendedPath(List<QContainer> qContainers, Path originalPath) {
		super();
		this.qContainers = qContainers;
		this.costs = originalPath.getCosts();
		this.negativeCosts = originalPath.getNegativeCosts();
		this.originalPath = originalPath;
	}

	@Override
	public List<QContainer> getPath() {
		return Collections.unmodifiableList(qContainers);
	}

	/**
	 * Returns the path this path is derived from. Note that the returned path may still be an {@link ExtendedPath}
	 * instance.
	 */
	public Path getOriginalPath() {
		return originalPath;
	}

	/**
	 * Returns the root path that has been extended, if the specified path is an extended path, otherwise return the
	 * specified path. If the specified path is extended multiple times, it returns the original path before any of
	 * these extensions.
	 */
	public static Path getRootPath(Path potentialExtendedPath) {
		while (potentialExtendedPath instanceof ExtendedPath) {
			potentialExtendedPath = ((ExtendedPath) potentialExtendedPath).getOriginalPath();
		}
		return potentialExtendedPath;
	}

	@Override
	public double getCosts() {
		return costs;
	}

	@Override
	public String toString() {
		return "Extended-Path: " + getPath() + " (costs: " + getCosts() + ")";
	}

	@Override
	public double getNegativeCosts() {
		return negativeCosts;
	}

	@Override
	public boolean contains(QContainer qContainer) {
		return qContainers.contains(qContainer);
	}

	@Override
	public boolean containsAll(Collection<QContainer> qContainers) {
		return this.qContainers.containsAll(qContainers);
	}

	@Override
	public boolean contains(Collection<QContainer> qContainers) {
		for (QContainer qcon : qContainers) {
			if (contains(qcon)) {
				return true;
			}
		}
		return false;
	}
}

/*
 * Copyright (C) 2023 denkbares GmbH, Germany
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

package de.d3web.costbenefit;

import java.util.Collection;
import java.util.List;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.costbenefit.model.Path;

/**
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 31.10.2023
 */
public record TestPath(List<QContainer> containers) implements Path {

	@Override
	public List<QContainer> getPath() {
		return containers;
	}

	@Override
	public double getCosts() {
		return 0;
	}

	@Override
	public double getNegativeCosts() {
		return 0;
	}

	@Override
	public boolean contains(QContainer qContainer) {
		return containers.contains(qContainer);
	}

	@Override
	public boolean containsAll(Collection<QContainer> qContainers) {
		return containers.containsAll(qContainers);
	}

	@Override
	public boolean contains(Collection<QContainer> qContainers) {
		for (QContainer qContainer : qContainers) {
			if (contains(qContainer)) {
				return true;
			}
		}
		return false;
	}
}

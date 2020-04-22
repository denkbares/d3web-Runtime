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
package de.d3web.costbenefit.inference;

import java.util.Collection;
import java.util.Iterator;

import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;

/**
 * This class discriminates if the solutions are part of multiple solution groups.
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 06.12.2013
 */
public class DefaultSolutionRater implements SolutionsRater {

	@Override
	public boolean check(Collection<Solution> undiscriminatedSolutions) {
		if (undiscriminatedSolutions == null || undiscriminatedSolutions.size() < 2) return false;

		// get the group of the first solution
		Iterator<Solution> iterator = undiscriminatedSolutions.iterator();
		Solution group = KnowledgeBaseUtils.getGroup(iterator.next());
		while (iterator.hasNext()) {
		// check if we already found a solution and now have a different solution,
		// then multiple groups are available, so discriminate
			if (group != KnowledgeBaseUtils.getGroup(iterator.next())) return true;
		}
		return false;
	}
}

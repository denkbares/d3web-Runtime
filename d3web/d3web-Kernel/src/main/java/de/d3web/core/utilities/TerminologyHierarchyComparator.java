/*
 * Copyright (C) 2014 denkbares GmbH, Germany
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

package de.d3web.core.utilities;

import java.util.Comparator;
import java.util.List;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.manage.KnowledgeBaseUtils;

/**
 * Compares TerminologyObjects according to their position inside the tree hierarchy according to DFS.
 * <p/>
 * Created by Albrecht Striffler (denkbares GmbH) on 11.08.14.
 */
public class TerminologyHierarchyComparator implements Comparator<TerminologyObject> {

	@Override
	public int compare(TerminologyObject o1, TerminologyObject o2) {
		return compareObjects(o1, o2);
	}

	public static int compareObjects(TerminologyObject o1, TerminologyObject o2) {
		if (o1 == o2) return 0;
		if (o1 == null) return -1;
		if (o2 == null) return 1;

		// optimization in case they have the same parent
		if (o1.getParents().length == 1 && o2.getParents().length == 1
				&& o1.getParents()[0] == o2.getParents()[0]) {
			return Integer.compare(indexOf(o1.getParents()[0].getChildren(), o1), indexOf(o2.getParents()[0].getChildren(), o2));
		}

		TerminologyObject commonAncestor = null;
		List<TerminologyObject> ancestors1 = KnowledgeBaseUtils.getAncestors(o1);
		List<TerminologyObject> ancestors2 = KnowledgeBaseUtils.getAncestors(o2);
		TerminologyObject compare1 = o1;
		TerminologyObject compare2 = o2;

		outer:
		for (TerminologyObject ancestor1 : ancestors1) {
			for (TerminologyObject ancestor2 : ancestors2) {
				if (ancestor1 == ancestor2) {
					commonAncestor = ancestor1;
					break outer;
				}
				compare2 = ancestor2;
			}
			compare1 = ancestor1;
		}
		if (commonAncestor == null) {
			// should only happen with different types of terminology objects, e.g. comparing solutions to questions
			return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
		}
		return Integer.compare(indexOf(commonAncestor.getChildren(), compare1), indexOf(commonAncestor.getChildren(), compare2));
	}

	private static int indexOf(TerminologyObject[] objectArray, TerminologyObject object) {
		for (int i = 0; i < objectArray.length; i++) {
			if (objectArray[i] == object) return i;
		}
		return -1;
	}

}

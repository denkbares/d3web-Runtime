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

import de.d3web.core.knowledge.TerminologyManager;
import de.d3web.core.knowledge.TerminologyObject;

/**
 * Compares TerminologyObjects according to their position inside the tree hierarchy according to DFS.
 * <p/>
 * Created by Albrecht Striffler (denkbares GmbH) on 11.08.14.
 */
public class TerminologyHierarchyComparator implements Comparator<TerminologyObject> {

	@Override
	public int compare(TerminologyObject o1, TerminologyObject o2) {
		TerminologyManager manager = o1.getKnowledgeBase().getManager();
		return manager.getTreeIndex(o1) - manager.getTreeIndex(o2);
	}

}

/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.core.io.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.utilities.NamedObjectComparator;

/**
 * A comparator for Rules. The rules are sorted after the terminal object used
 * in the condition, the backwards objects in the action and finally their
 * toString method.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 31.01.2011
 */
public final class RuleComparator implements Comparator<Rule> {

	@Override
	public int compare(Rule o1, Rule o2) {
		// get all idobjects of the conditions and try to sort the rules by
		// the ids of them
		Collection<? extends TerminologyObject> terminalObjects = o1.getCondition().getTerminalObjects();
		Collection<? extends TerminologyObject> terminalObjects2 = o2.getCondition().getTerminalObjects();
		int comparator = compareIDObjectLists(terminalObjects, terminalObjects2);
		if (comparator != 0) return comparator;
		// conditions contain the same idobjects, try to compare actions
		List<? extends TerminologyObject> backwardObjects = o1.getAction().getBackwardObjects();
		List<? extends TerminologyObject> backwardObjects2 = o2.getAction().getBackwardObjects();
		comparator = compareIDObjectLists(backwardObjects, backwardObjects2);
		if (comparator != 0) return comparator;
		// actions contain the same idodjects, compare by toString
		return o1.toString().compareTo(o2.toString());
	}

	public int compareIDObjectLists(Collection<? extends TerminologyObject> terminalObjects, Collection<? extends TerminologyObject> terminalObjects2) {
		List<TerminologyObject> allTerminalObjects = new LinkedList<TerminologyObject>();
		allTerminalObjects.addAll(terminalObjects);
		allTerminalObjects.addAll(terminalObjects2);
		Collections.sort(allTerminalObjects, new NamedObjectComparator());
		for (TerminologyObject o : allTerminalObjects) {
			if (!terminalObjects.contains(o)) {
				return -1;
			}
			if (!terminalObjects2.contains(o)) {
				return 1;
			}
		}
		return 0;
	}
}


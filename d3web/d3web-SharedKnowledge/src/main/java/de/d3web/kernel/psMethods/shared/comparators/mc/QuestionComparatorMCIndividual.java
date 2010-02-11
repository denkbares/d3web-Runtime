/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.d3web.kernel.psMethods.shared.comparators.mc;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.terminology.Answer;
import de.d3web.kernel.psMethods.shared.comparators.IndividualComparator;

/**
 * Creation date: (07.08.2001 02:42:47)
 * @author: Norman Brümmer
 */
public class QuestionComparatorMCIndividual extends QuestionComparatorMC implements IndividualComparator {

	private static final long serialVersionUID = 7222326846652642522L;

	public double compare(List<?> ans1, List<?> ans2) {
		List<Answer> proved = new LinkedList<Answer>();
		double compCount = 0;
		double succCount = 0;

		Iterator<?> iter = ans1.iterator();
		while (iter.hasNext()) {
			Answer ans = (Answer) iter.next();
			compCount++;
			if (ans2.contains(ans)) {
				succCount++;
			}
			proved.add(ans);
		}

		iter = ans2.iterator();
		while (iter.hasNext()) {
			Answer ans = (Answer) iter.next();
			if (!proved.contains(ans)) {
				compCount++;
			}
		}
		return succCount / compCount;
	}
}
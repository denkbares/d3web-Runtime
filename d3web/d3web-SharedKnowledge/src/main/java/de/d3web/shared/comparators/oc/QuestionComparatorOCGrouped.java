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

package de.d3web.shared.comparators.oc;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.shared.comparators.GroupedComparator;
import de.d3web.shared.comparators.PairRelation;
/**
 * Insert the type's description here.
 * Creation date: (03.08.2001 15:13:05)
 * @author: Norman Brümmer
 */
public abstract class QuestionComparatorOCGrouped extends QuestionComparatorOC implements GroupedComparator {

	protected List<PairRelation> pairRelations = null;

	/**
	 * Insert the method's description here.
	 * Creation date: (03.08.2001 15:42:38)
	 */
	public QuestionComparatorOCGrouped() {
		pairRelations = new LinkedList<PairRelation>();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (03.08.2001 15:23:14)
	 * @param ans1 de.d3web.kernel.domainModel.answers.AnswerChoice
	 */
	public void addPairRelation(Choice ans1, Choice ans2) {
		addPairRelation(ans1, ans2, 1.0);
	}
	
	@Override
	public void addPairRelation(PairRelation rel) {
		if (pairRelations.contains(rel)) {
			pairRelations.remove(rel);
		}
		pairRelations.add(rel);
		
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (03.08.2001 15:23:14)
	 * @param ans1 de.d3web.kernel.domainModel.answers.AnswerChoice
	 */
	public void addPairRelation(Choice ans1, Choice ans2, double value) {
		PairRelation rel = new PairRelation(ans1, ans2, value);
		addPairRelation(rel);
	}

	public double getPairRelationValue(Choice ans1, Choice ans2) {
		PairRelation rel = new PairRelation(ans1, ans2, 0.0);
		boolean found = pairRelations.contains(rel);
		if (found) {
			int index = pairRelations.indexOf(rel);
			PairRelation foundrel = (PairRelation) pairRelations.get(index);
			return foundrel.getValue();
		} else
			return 0.0;
	}

	public List<PairRelation> getPairRelations() {
		return pairRelations;
	}
}
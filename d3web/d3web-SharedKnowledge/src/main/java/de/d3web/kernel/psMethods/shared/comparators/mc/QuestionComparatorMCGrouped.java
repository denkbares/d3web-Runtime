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

import java.util.LinkedList;
import java.util.List;

import de.d3web.core.session.values.AnswerChoice;
import de.d3web.kernel.psMethods.shared.comparators.GroupedComparator;
import de.d3web.kernel.psMethods.shared.comparators.PairRelation;

/**
 * Creation date: (07.08.2001 09:55:57)
 * @author: Norman Brümmer
 */
public abstract class QuestionComparatorMCGrouped extends QuestionComparatorMC implements
        GroupedComparator {

   private static final long serialVersionUID = -4337435566756738090L;
	protected List<PairRelation> pairRelations = null;

    /**
     * Creation date: (03.08.2001 15:23:14)
     * 
     * @param ans1
     *            de.d3web.kernel.domainModel.answers.AnswerChoice
     */
    public void addPairRelation(AnswerChoice ans1, AnswerChoice ans2,
            double value) {
        PairRelation rel = new PairRelation(ans1, ans2, value);
        if (pairRelations.contains(rel)) {
            pairRelations.remove(rel);
        }

        pairRelations.add(rel);

    }
    
    public void addPairRelation(PairRelation pairRelation) {
    	pairRelations.add(pairRelation);
    }

    public double getPairRelationValue(AnswerChoice ans1, AnswerChoice ans2) {
        PairRelation rel = new PairRelation(ans1, ans2, 0.0);
        boolean found = pairRelations.contains(rel);
        if (found) {
            int index = pairRelations.indexOf(rel);
            PairRelation foundrel = (PairRelation) pairRelations.get(index);
            return foundrel.getValue();
        } else
            return 0.0;
    }

    /**
     * Creation date: (07.08.2001 10:56:41)
     */
    public QuestionComparatorMCGrouped() {
        pairRelations = new LinkedList<PairRelation>();
    }

    public void addPairRelation(AnswerChoice ans1, AnswerChoice ans2) {
        addPairRelation(ans1, ans2, 1.0);
    }

    /**
     * @return Returns the pairRelations.
     */
    public List<PairRelation> getPairRelations() {
        return pairRelations;
    }
}
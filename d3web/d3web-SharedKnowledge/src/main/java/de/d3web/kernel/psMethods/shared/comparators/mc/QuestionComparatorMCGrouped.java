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

import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.psMethods.shared.comparators.GroupedComparator;
import de.d3web.kernel.psMethods.shared.comparators.PairRelation;

/**
 * Creation date: (07.08.2001 09:55:57)
 * @author: Norman Brümmer
 */
public abstract class QuestionComparatorMCGrouped extends QuestionComparatorMC implements
        GroupedComparator {

    protected List pairRelations = null;

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
        pairRelations = new LinkedList();
    }

    public void addPairRelation(AnswerChoice ans1, AnswerChoice ans2) {
        addPairRelation(ans1, ans2, 1.0);
    }

    public double compare(List ans1, List ans2) {
        if (isSameAnswerListContent(ans1, ans2)) { return 1; }

        double value = 0;
        double countComp = 0;

        Iterator iter = pairRelations.iterator();
        while (iter.hasNext()) {
            PairRelation rel = (PairRelation) iter.next();
            if ((ans1.contains(rel.getAnswer1()) && ans2.contains(rel
                    .getAnswer1()))
                    || (ans1.contains(rel.getAnswer2()) && ans2.contains(rel
                            .getAnswer2()))) {
                value += 1.0;
                countComp++;
            } else if ((ans1.contains(rel.getAnswer1()) && ans2.contains(rel
                    .getAnswer2()))
                    || (ans1.contains(rel.getAnswer2()) && ans2.contains(rel
                            .getAnswer1()))) {
                value += rel.getValue();
                countComp++;
            }
        }
        if (countComp == 0) {
            return 0;
        } else {
            return value / countComp;
        }
    }

    /**
     * Creation date: (09.08.2001 18:07:04)
     * 
     * @return java.lang.String
     */
    public abstract java.lang.String getXMLString();
        
    /**
     * Creation date: (20.08.2001 15:18:06)
     */
    private boolean isSameAnswerListContent(List l1, List l2) {
        Iterator iter1 = l1.iterator();
        while (iter1.hasNext()) {
            if (!l2.contains(iter1.next())) { return false; }
        }

        Iterator iter2 = l2.iterator();
        while (iter2.hasNext()) {
            if (!l1.contains(iter2.next())) { return false; }
        }

        return true;
    }

    /**
     * @return Returns the pairRelations.
     */
    public List getPairRelations() {
        return pairRelations;
    }
}
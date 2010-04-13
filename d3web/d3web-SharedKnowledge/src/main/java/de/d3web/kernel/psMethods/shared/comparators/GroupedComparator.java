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

/*
 * Created on 28.07.2003
 */
package de.d3web.kernel.psMethods.shared.comparators;

import java.util.List;

import de.d3web.core.session.values.Choice;

/**
 * Marks a question-comparator as grouped.
 * 
 * @author Tobias Vogele
 */
public interface GroupedComparator {

	void addPairRelation(Choice ans1, Choice ans2, double value);
	
	void addPairRelation(PairRelation pairRelation);
	
	void addPairRelation(Choice ans1, Choice ans2);

	double getPairRelationValue(Choice ans1, Choice ans2);

	List<PairRelation> getPairRelations();
	
}

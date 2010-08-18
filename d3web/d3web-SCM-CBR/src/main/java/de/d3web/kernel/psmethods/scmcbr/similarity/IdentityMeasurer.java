/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.kernel.psmethods.scmcbr.similarity;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;

public class IdentityMeasurer implements ISimilarityMeasurer {

	private final Value expectedValue;
	private final Question question;

	public IdentityMeasurer(Question question, Value expectedValue) {
		this.expectedValue = expectedValue;
		this.question = question;
	}

	@Override
	public double computeSimilarity(Session session) {
		Value value = session.getBlackboard().getValue(question);
		if (value.equals(expectedValue)) // TODO Fix
		return 1;
		else return 0;
	}

}

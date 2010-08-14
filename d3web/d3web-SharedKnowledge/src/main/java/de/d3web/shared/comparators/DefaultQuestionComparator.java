/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.shared.comparators;

import de.d3web.core.session.Value;

/**
 * This comparator simply checks if the answers are equal. Then the similarity
 * is 1.0. If not, it is 0.0.<br>
 * <B> This Comparator will only be used if none is defined for a Question! The
 * SharedKnowledgeLoader still decides which comparator is the default
 * knowledge! </B>
 * 
 * @author bates
 */
public class DefaultQuestionComparator extends QuestionComparator {

	private static DefaultQuestionComparator instance = null;

	private DefaultQuestionComparator() {
		super();
	}

	public static DefaultQuestionComparator getInstance() {
		if (instance == null) {
			instance = new DefaultQuestionComparator();
		}
		return instance;
	}

	@Override
	public double compare(Value value1, Value value2) {
		return value1.compareTo(value2);
	}
}

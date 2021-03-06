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

package de.d3web.core.knowledge.terminology;

/**
 * Represents an Answer that is the "yes" answer for YesNo-questions Creation
 * date: (28.09.00 17:52:09)
 * 
 * @author Joachim Baumeister
 */
public class AnswerYes extends Choice {

	/**
	 * creates a new AnswerYes.
	 */
	public AnswerYes(String id) {
		super(id);
	}

	/**
	 * @return true
	 */
	@Override
	public boolean isAnswerYes() {
		return true;
	}
}

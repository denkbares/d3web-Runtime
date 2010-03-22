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

package de.d3web.core.session.values;

/**
 * Represents an Answer that is the "no" answer for YesNo-questions
 * Creation date: (28.09.00 17:52:43)
 * @author Joachim Baumeister
 */
public class AnswerNo extends AnswerChoice {

	private static final long serialVersionUID = 4580215611560294804L;
	
	public AnswerNo(String id) {
		super(id);
	}
	/**
	 * @return true
	 */
	public boolean isAnswerNo() {
		return true;
	}
	
	

	// 20030923 marty: delegate to AnswerChoice.equals(...)
	//	/**
	//	 * compares for equal reference first, then for equal class instance.
	//	 * <BR>
	//	 * 2002-05-29 joba: added for better comparisons
	//	 */
	//	public boolean equals(Object other) {
	//		if (this == other) {
	//			return true;
	//		} else if (other instanceof AnswerNo) {
	//			return true;
	//		} else {
	//			return false;
	//		}
	//	}
}

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

import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;

/**
 * special answer: "unknown"
 * Creation date: (13.09.2000 13:51:37)
 * @author norman
 */
public class AnswerUnknown extends Answer {
	
	public final static String UNKNOWN_ID = "MaU";
	
	public final static String UNKNOWN_VALUE = "-?-";

	public AnswerUnknown(String id) {
	    super(id);
	}

	public AnswerUnknown() {
		super(UNKNOWN_ID);
	}

	/**
	 * Creation date: (15.09.2000 11:07:48)
	 * @return AnswerUnknown.UNKNOWN_VALUE
	 */
	@Override
	public Object getValue(Session theCase) {

		String result = null;
		if (getQuestion() != null) {
			try {
				result =
					(String) getQuestion().getProperties().getProperty(
						Property.UNKNOWN_VERBALISATION);
			} catch (RuntimeException e) {
			}
		}

		if (result != null) {
			return result;
		}

		if (theCase != null) {
			try {
				result =
					(String) theCase
						.getKnowledgeBase()
						.getProperties()
						.getProperty(
						Property.UNKNOWN_VERBALISATION);
			} catch (RuntimeException e) {
			}
		}
		if (result != null) {
			return result;
		}

		return AnswerUnknown.UNKNOWN_VALUE;
	}

	/**
	 * Creation date: (13.09.2000 13:54:21)
	 * @return true
	 */
	@Override
	public boolean isUnknown() {
		return true;
	}

	@Override
	public String getId() {
		return UNKNOWN_ID;
	}

	/**
	 * Creation date: (15.03.2001 17:00:52)
	 * @return true, iff o is instanceof AnswerUnknown
	 */
	public static boolean isUnknownAnswer(Object o) {
		return o instanceof AnswerUnknown;
	}

	@Override
	public String toString() {
		return UNKNOWN_VALUE;
	}
	
	/**
	 * @return true iff otherAnswer is of same type and not null
	 */
	@Override
	public boolean equals(Object otherAnswer) {
		return otherAnswer instanceof AnswerUnknown;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public int compareTo(Answer other) {
		if (other instanceof AnswerUnknown) {
			return 0;
		}
		// unknown comes at the and
		return 1;
	}
}
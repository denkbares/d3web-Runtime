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
import de.d3web.core.session.XPSCase;
/**
 * Answer class for textual questions
 * Creation date: (13.09.2000 13:50:23)
 * @author norman
 */
public class AnswerText extends Answer {
	
	private static final long serialVersionUID = -3763281596806332194L;
	private String text;

	public AnswerText() {
		super(null);
	}
	
	/**
	 * getId method comment.
	 */
	@Override
	public String getId() {
		return getQuestion().getId() + "aText"; // äußerst fragwürdig!!! 
	}

	public String getName() {
		return text;
	}

	/**
	 * Creation date: (15.09.2000 11:06:43)
	 * @return answer text (instanceof String)
	 */
	public Object getValue(XPSCase theCase) {
		return getName();
	}

	public void setText(String newText) {
		text = newText;
	}

	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof AnswerText) {
			AnswerText other = (AnswerText) o;
			return getId().equals(other.getId()) && getName().equals(other.getName());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getId().hashCode() + getName().hashCode();
	}

	@Override
	public int compareTo(Answer other) {
		if (other instanceof AnswerText) {
			String s1 = ((AnswerText) this).getName();
			String s2 = ((AnswerText) other).getName();
			return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
		}
		if (other instanceof AnswerUnknown) {
			// unknown comes at the and
			return -1;
		}
		throw new IllegalArgumentException(
				"Cannot compare answers of type AnswerDate and " + other.getClass());
	}
}
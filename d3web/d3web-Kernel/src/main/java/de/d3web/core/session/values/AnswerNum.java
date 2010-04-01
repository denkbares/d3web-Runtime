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
 * answer class for numeric questions
 * Creation date: (13.09.2000 13:28:30)
 * @author norman
 */
public class AnswerNum extends Answer {
	
	private EvaluatableAnswerNumValue value = null;

	/**
	 * This wrapper class is neccessary for the evaluation of
	 * AnswerNum values which are EvaluatableAnswerNumValues 
	 */
	private class DoubleWrapper implements EvaluatableAnswerNumValue {
		/**
		* real AnswerNum value
		*/
		private Double value = null;

		/** creates a new DoubleWrapper object*/
		private DoubleWrapper(Double value) {
			this.value = value;
		}

		/** @return evaluated wrapper-object of type Double*/
		public Double eval(XPSCase theCase) {
			return value;
		}

		/** String representation (delegation to Double)*/
		public String toString() {
			return value.toString();
		}

		public boolean equals(Object other) {
			if (this == other) {
				return true;
			} else if ((other == null) || (getClass() != other.getClass())) {
				return false;
			} else {
				DoubleWrapper otherDW = (DoubleWrapper) other;
				if ((this.value != null) && (otherDW.value != null))
					return value.equals(otherDW.value);
				else
					return false;
			}
		}

		public int hashCode() {
			if (this.value != null)
				return this.value.hashCode();
			else
				return super.hashCode();
		}

	}

	public AnswerNum() {
	    super(null);
	}
	
	/**
	 * getId method comment.
	 */
	@Override
	public String getId() {
		return getQuestion().getId() + "aNum";
	}

	/**
	 * Creation date: (13.09.2000 13:39:57)
	 * @return numeric value of the answer (is instanceof Double or FormulaElement or null!)
	 */
	public Object getValue(XPSCase theCase) {
		if (value == null)
			return null;
		else
			return value.eval(theCase);
	}

	public void setValue(EvaluatableAnswerNumValue value) {
		this.value = value;
	}

	public void setValue(Double value) {
		this.value = new DoubleWrapper(value);
	}

	public String toString() {
		if (value != null) {
			return value.toString();
		} else {
			return "";
		}
	}

	/**
	 * first checks the reverence, then type-equality and at last the answer value.
	 */
	public boolean equals(Object other) {
		if (this == other)
			return true;
		else if ((other == null) || (getClass() != other.getClass())) {
			return false;
		} else {
			AnswerNum otherAN = (AnswerNum) other;
			return (value == null && otherAN.value == null)
				|| value.equals(otherAN.value);
		}
	}

	/**
	 * @return the hashcode e.g. for usage in Hashtable
	 */
	public int hashCode() {
		return getId().hashCode() + (value == null ? 42 : value.hashCode());
	}

	@Override
	public int compareTo(Answer other) {
		if (other instanceof AnswerNum) {
			Number d1 = (Number) this.getValue(null);
			Number d2 = (Number) other.getValue(null);
			return (int) Math.signum(d1.doubleValue() - d2.doubleValue());
		}
		if (other instanceof AnswerUnknown) {
			// unknown comes at the and
			return -1;
		}
		throw new IllegalArgumentException(
				"Cannot compare answers of type AnswerDate and " + other.getClass());
	}
}
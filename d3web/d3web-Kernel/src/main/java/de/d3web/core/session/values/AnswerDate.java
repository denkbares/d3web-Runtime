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
 * Created on 09.10.2003
 */
package de.d3web.core.session.values;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.core.session.XPSCase;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.QuestionChoice;
import de.d3web.core.terminology.QuestionDate;

/**
 * Answer-Class for DateQuestions
 * @author Tobias Vogele
 * @see QuestionDate
 */
public class AnswerDate extends Answer {

	/**
	 * This wrapper class is necessary for the evaluation of
	 * AnswerDate values which are EvaluatableAnswerDateValues 
	 */
	private class DateWrapper implements EvaluatableAnswerDateValue {
		/**
		* real AnswerDate value
		*/
		private Date value = null;

		/** creates a new DateWrapper object*/
		private DateWrapper(Date value) {
			this.value = value;
		}

		/** @return evaluated wrapper-object of type Date*/
		public Date eval(XPSCase theCase) {
			return value;
		}

		/** String representation (delegation to Date)*/
		public String toString() {
			return value.toString();
		}

		public boolean equals(Object other) {
			if (this == other) {
				return true;
			} else if ((other == null) || (getClass() != other.getClass())) {
				return false;
			} else {
				DateWrapper otherDW = (DateWrapper) other;
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
		
		public Date getDate() {
			return value;
		}

	}	
	
	/**
	 * The Format, in which the dates are saved and loaded.
	 * The Format is for example 2003-10-20-13-51-23 
	 */
	public static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	private EvaluatableAnswerDateValue value;

	public AnswerDate() {
	    super();
	}
	
	public AnswerDate(String id) {
	    super(id);
	}

	public String getId() {
		return getQuestion().getId() + "aDate";
	}

	public Object getValue(XPSCase theCase) {
		return value;
	}

	public void setValue(Date newDate) {
		value = new DateWrapper(newDate);
	}

	/**
	 * Output for debugging.
	 */
	public String toString() {
		// Tobi: Diese Methode wird nicht zum speichern benutzt, kann also ruhig geändert werden.
		return (value == null  || value.eval(null) == null) ? "" : format.format(value.eval(null));
	}

	/**
	 * Returns the date as String in the format in which it can be parsed at setDate(String) 
	 */
	public String getDateString() {
		return value == null ? "" : format.format(value.eval(null));		
	}

	public boolean equals(Object other) {
		if (! (other instanceof AnswerDate)) {
			return false;
		}
		AnswerDate othswer = (AnswerDate)other;
		if (value == null) {
			return othswer.value == null;
		}else {
			return value.equals(othswer.value);
		}
	}

	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}

	public void setValue(String newDate) {
		try {
			setValue(format.parse(newDate));
		} catch (ParseException e) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(), "setDate", e);
		}		
	}

	public void setValue(EvaluatableAnswerDateValue value) {
		this.value = value;
	}
	
	@Override
	public int compareTo(Answer other) {
		if (other instanceof AnswerDate) {
			EvaluatableAnswerDateValue e1 = (EvaluatableAnswerDateValue) this.getValue(null);
			EvaluatableAnswerDateValue e2 = (EvaluatableAnswerDateValue) other.getValue(null);
			long i1 = e1.eval(null).getTime();
			long i2 = e2.eval(null).getTime();
			return (int) (i1 - i2);
		}
		if (other instanceof AnswerUnknown) {
			// unknown comes at the and
			return -1;
		}
		throw new IllegalArgumentException(
				"Cannot compare answers of type AnswerDate and " + other.getClass());
	}
}

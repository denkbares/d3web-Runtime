/*
 * Copyright (C) 2011 denkbares GmbH
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

package de.d3web.core.session.protocol;

import java.text.DateFormat;
import java.util.Date;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.utils.EqualsUtils;
import de.d3web.utils.HashCodeUtils;

/**
 * Implementation of {@link ProtocolEntry} to store facts that are provided by a
 * single problem solving methods or as the merged fact from the whole
 * blackboard.
 * <p>
 * This object does not store references to the knowledge representation objects
 * belonging to the knowledge base instance, due to being able to be loaded or
 * used even without a concrete knowledge base. Therefore the fact object is
 * referenced by its name, that should be unique in the knowledge base (unique
 * name assumption). The problem or strategic solver is referenced by the class
 * name of the {@link PSMethod}. The value is stored as a raw/native java value
 * as follows:
 * <ul>
 * <li> {@link InterviewObject}: {@link Indication#State} representing the
 * indication level of that interview object, if this facts entry is an
 * interview fact and not a value fact. Otherwise the value is one of the
 * following ones.
 * <li> {@link Unknown}: if the value is unknown, {@link Unknown#getInstance()}
 * is returned
 * <li> {@link Unknown}: if the value is undefined,
 * {@link UndefinedValue#getInstance()} is returned
 * <li> {@link QuestionYN}: {@link String} representing the text (or
 * "#&lt;id&gt;" if the text is null) of the choice
 * <li> {@link QuestionOC}: {@link String} representing the text (or
 * "#&lt;id&gt;" if the text is null) of the choice
 * <li> {@link QuestionMC}: {@link String} representing the text (or
 * "#&lt;id&gt;" if the text is null) of the choice; if more than one choice
 * selected, a {@link String}[] is returned
 * <li> {@link QuestionNum}: {@link Number} representing the value of the
 * question
 * <li> {@link QuestionText}: {@link String} representing the entered text or the
 * unknown text of the question
 * <li> {@link QuestionDate}: {@link Date} representing the entered date of the
 * question
 * <li> {@link Solution}: {@link Rating#State} representing a value of the
 * solution
 * </ul>
 * 
 * See {@link ProtocolConversion} for some helper methods to deal with these
 * entries in combination to a defined knowledge base.
 * 
 * @author volker_belli
 * @created 19.10.2010
 */
public class FactProtocolEntry implements ProtocolEntry {

	private final Date date;
	private final String terminologyObjectName;
	private final String solvingMethodClassName;
	private final Value value;

	/**
	 * Creates a new protocol entry for a specified fact to the specified date.
	 * 
	 * @param date the date of the entry
	 * @param fact the fact to take the entry's information from
	 * @throws NullPointerException if any of the specified arguments are null
	 */
	public FactProtocolEntry(Date date, Fact fact) {
		this(date,
				fact.getTerminologyObject().getName(),
				fact.getPSMethod().getClass().getName(),
				fact.getValue());
	}

	/**
	 * Creates a new protocol entry for a specified fact to the specified time.
	 * The timeMillis is the number of milliseconds since the standard base time
	 * known as "the epoch", namely January 1, 1970, 00:00:00 GMT
	 * 
	 * @param timeMillis the date of the entry
	 * @param fact the fact to take the entry's information from
	 * @throws NullPointerException if the specified fact is null
	 */
	public FactProtocolEntry(long timeMillis, Fact fact) {
		this(new Date(timeMillis), fact);
	}

	/**
	 * Creates a new protocol entry with the specified values. The raw value
	 * must be as described in {@link FactProtocolEntry}.
	 * 
	 * @param date the date of the entry
	 * @param terminologyObjectName the name of the valued object
	 * @param solvingMethodClassName the problem solver class name (fully
	 *        qualified)
	 * @param rawValue the value of the object
	 * @throws NullPointerException if any of the specified arguments are null
	 */
	public FactProtocolEntry(Date date, String terminologyObjectName, String solvingMethodClassName, Value value) {
		this.date = date;
		this.terminologyObjectName = terminologyObjectName;
		this.solvingMethodClassName = solvingMethodClassName;
		this.value = value;
		if (date == null) {
			throw new NullPointerException("specified date of protocol entry is null");
		}
		if (terminologyObjectName == null) {
			throw new NullPointerException("specified object name of protocol entry is null");
		}
		if (solvingMethodClassName == null) {
			throw new NullPointerException("specified solver of protocol entry is null");
		}
		if (value == null) {
			throw new NullPointerException("specified value of protocol entry is null");
		}
	}

	@Override
	public Date getDate() {
		return date;
	}

	/**
	 * Returns the name of the terminology object.
	 * 
	 * @created 19.10.2010
	 * @return the terminology object name
	 */
	public String getTerminologyObjectName() {
		return terminologyObjectName;
	}

	/**
	 * Returns the fully qualified class name of the PSMethod having derived
	 * this fact. If the class name is null, it is a fact merged from multiple
	 * problem solvers.
	 * 
	 * @created 19.10.2010
	 * @return the PSMethod class name
	 */
	public String getSolvingMethodClassName() {
		return solvingMethodClassName;
	}

	/**
	 * Returns the raw value of this fact. Please note that the returned value
	 * is <strong>not</strong> a {@link Value}. Instead see
	 * {@link FactProtocolEntry} documentation for more details.
	 * 
	 * @created 19.10.2010
	 * @return the raw/primitive value
	 */
	public Value getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		int result = HashCodeUtils.SEED;
		result = HashCodeUtils.hash(result, getDate());
		result = HashCodeUtils.hash(result, getTerminologyObjectName());
		result = HashCodeUtils.hash(result, getSolvingMethodClassName());
		result = HashCodeUtils.hash(result, getValue());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FactProtocolEntry other = (FactProtocolEntry) obj;
		return EqualsUtils.equals(this.getDate(), other.getDate())
				&& EqualsUtils.equals(this.getTerminologyObjectName(),
						other.getTerminologyObjectName())
				&& EqualsUtils.equals(this.getSolvingMethodClassName(),
						other.getSolvingMethodClassName())
				&& EqualsUtils.equals(this.getValue(), other.getValue());
	}

	@Override
	public String toString() {
		return "[" +
				DateFormat.getInstance().format(getDate()) +
				"] " +
				this.getSolvingMethodClassName() +
				": " +
				this.getTerminologyObjectName() +
				" = " +
				this.getValue();
	}

}

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

import de.d3web.core.knowledge.terminology.info.Properties;
import de.d3web.core.knowledge.terminology.info.PropertiesContainer;
import de.d3web.core.manage.AnswerFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.core.session.values.Choice;

/**
 * This class represents the abstract notion of an answer, that can be assigned
 * to a question. For the different types of {@link Question} there also exist
 * corresponding subclasses of {@link Answer}. For instance, we a
 * {@link QuestionChoice} holds a list of {@link Choice} instances.
 * 
 * @author Christian Betz, joba, norman
 * @see Question TODO: remove IDOPbject from Answer. They do not should have any
 *      ids.
 */
public abstract class Answer implements IDObject, PropertiesContainer, Comparable<Answer> {

	private String id;

	@Override
	public String getId() {
		return id;
	}

	/**
	 * The {@link Question} instance this {@link Answer} belongs to
	 */
	private Question question;

	/**
	 * Especially {@link Choice} instances can hold additional properties, such
	 * as explanation text etc.
	 */
	private Properties properties = new Properties();

	/**
	 * Creates an {@link Answer} only with ID information. Not for public use.
	 * Please use {@link AnswerFactory}.
	 */
	public Answer(String id) {
		this.id = id;
	}

	/**
	 * Returns the {@link Properties} map for this {@link Answer}. For example,
	 * the explanation text for an answer can be stored as a property.
	 * 
	 * @return the {@link Properties} map of this {@link Answer}
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Set the new map of {@link Properties} for this {@link Answer} instance.
	 * For example, the explanation text for an answer can be stored as a
	 * property.
	 * 
	 * @param properties the new properties of this object
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * Empty method; please do not use.
	 * 
	 * @return null, because this is not needed here.
	 */
	// joba: I removed this method, since it appears to be not used in any way.
	// Please report any problems to me.
	// public SessionObject createCaseObject() {
	// return null;
	// }

	/**
	 * Returns the {@link Question} instance corresponding to this
	 * {@link Answer}.
	 * 
	 * @return the question related with this answer TODO: remove link to
	 *         question from the answer.
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * Sets the {@link Question} object, that is corresponding to this
	 * {@link Answer}.
	 * 
	 * @param question the corresponding {@link Question} TODO: remove link to
	 *        question from the answer.
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}

	/**
	 * Do not use: Please use: {@link AnswerUnknown}.UNKNOWN_ID
	 * 
	 * @return the ID for unknown answer
	 * @deprecated use static String in AnswerUnknown
	 */
	@Deprecated
	public String getUnknownTag() {
		return AnswerUnknown.UNKNOWN_ID;
	}

	/**
	 * Returns the current value of this Answer in a specific {@link Session}.
	 * This method is especially useful for numeric answers, since, e.g.,
	 * {@link Choice} objects have static values.
	 * 
	 * @return text or numeric value of this {@link Answer} object TODO: remove
	 *         Session from signature. This results to removing all dynamic
	 *         evaluateable answer values (e.g. Formulas) from the answer.
	 *         Evaluate them before creating the Answer
	 */
	public abstract Object getValue(Session theCase);

	/**
	 * Helper method, to test, if this {@link Answer} is an
	 * {@link AnswerUnknown} instance. In most cases, this answer is <b>not</b>
	 * 'unknown'. For {@link AnswerUnknown} this method is overridden and
	 * returns true.
	 * 
	 * @return false for almost all {@link Answer} instances, except
	 *         {@link AnswerUnknown}
	 * @see AnswerUnknown#isUnknown()
	 */
	public boolean isUnknown() {
		return false;
	}

	/**
	 * Returns a simple/static String representation of this {@link Answer}.
	 * 
	 * @return a String representation of this answer object
	 */
	@Override
	public String toString() {
		return "Answer";
	}

	/**
	 * Returns the verbalization of this {@link Answer} in the context of an
	 * {@link Session}. This is especially useful for Answers, dynamically
	 * changing, such as {@link AnswerNum}.
	 * 
	 * @param theCase the context {@link Session}
	 * @return value of this {@link Answer} in the context of an {@link Session}
	 */
	public String verbalizeValue(Session theCase) {
		return getValue(theCase).toString();
	}

	/**
	 * Returns the hashCode of this Answer.
	 */
	@Override
	public abstract int hashCode();

	public String getName() {
		return getValue(null).toString();
	}

	// [TODO] where is the equals implementation?

	/**
	 * Checks, if other object is an IDObject and if it contains the same ID.
	 * 
	 * @return true, if equal
	 * @param other Object to compare for equality
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		else if ((other == null) || (getClass() != other.getClass())) {
			return false;
		}
		else {
			IDObject otherIDO = (IDObject) other;
			if ((getId() != null) && (otherIDO.getId() != null)) {
				return getId().equals(otherIDO.getId());
			}
			else {
				return super.equals(other);
			}
		}
	}
}
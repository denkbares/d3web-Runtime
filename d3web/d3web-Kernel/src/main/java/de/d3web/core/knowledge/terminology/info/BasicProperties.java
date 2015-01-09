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
package de.d3web.core.knowledge.terminology.info;

import java.util.Date;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityNum;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;

/**
 * A collection of basic Properties for d3web
 *
 * @author Joachim Baumeister, hoernlein, Markus Friedrich (denkbares GmbH)
 * @created 07.10.2010
 */
public class BasicProperties {

	/**
	 * used for: QASet doc: specifies what amount of costs is needed to 'perform' the qaset
	 *
	 * @return Double
	 */
	public static final Property<Double> COST = Property.getProperty("cost", Double.class);

	/**
	 * Returns the costs defined for a question or questionnaire. If no explicit costs are set for
	 * the specific item, a default cost value of 1.0 will be assumed. The method does not
	 * accumulate the costs for the children of the specified qaset, only the directly set costs for
	 * the object are returned.
	 *
	 * @param qaset the question of questionnaire to get the costs for
	 * @return the costs of the specified qaset
	 */
	public static double getCost(QASet qaset) {
		Double cost = qaset.getInfoStore().getValue(COST);
		return (cost == null) ? 1.0 : cost;
	}

	/**
	 * Used for: Questions doc: Marks a Question as abstraction question (derived) or not.
	 * Boolean.TRUE means, it is a abstraction question, all other values means, it is not.
	 *
	 * @return Boolean
	 */
	public static final Property<Boolean> ABSTRACTION_QUESTION = Property.getProperty(
			"abstractionQuestion", Boolean.class);

	/**
	 * used for Diagnosis Saves the apriori probability of a diagnosis
	 *
	 * @return Float
	 */
	public static final Property<Float> APRIORI = Property.getProperty(
			"apriori", Float.class);

	/**
	 * used for: QuestionNum doc: valid range of numerical answers of QuestionNum
	 *
	 * @return NumericalInterval
	 */
	public static final Property<NumericalInterval> QUESTION_NUM_RANGE =
			Property.getProperty(
					"range", NumericalInterval.class);

	/**
	 * used for question doc: the ids or names of the answers(seperated by ";"), which is set in
	 * PSMethodInit
	 *
	 * @return String
	 */
	public static final Property<String> INIT = Property.getProperty("init", String.class);

	/**
	 * TODO: Remove when UnknownChoice is implemented
	 * <p/>
	 * used for: Question, Knowledgebase doc: should UNKNOWN be invisible in questions, If it is set
	 * to the kb it represents the default.
	 *
	 * @return Boolean
	 */
	public static final Property<Boolean> UNKNOWN_VISIBLE = Property.getProperty(
			"unknownVisible", Boolean.class);

	/**
	 * Element Name: Date Label: Date Definition: A date of an event in the lifecycle of the
	 * resource. Comment: Typically, Date will be associated with the creation or availability of
	 * the resource. Recommended best practice for encoding the date value is defined in a profile
	 * of ISO 8601 [W3CDTF] and includes (among others) dates of the form YYYY-MM-DD.
	 */
	public static final Property<String> VERSION =
			Property.getProperty("version", String.class);

	/**
	 * Element Name: Creator Label: Creator Definition: An entity primarily responsible for making
	 * the content of the resource. Comment: Examples of Creator include a person, an organization,
	 * or a service. Typically, the name of a Creator should be used to indicate the entity.
	 */
	public static final Property<String> AUTHOR = Property.getProperty("author", String.class);

	/**
	 * A property for a {@link KnowledgeBase} instance to store the desired affiliation of the
	 * author and/or this knowledge base.
	 */
	public static final Property<String> AFFILIATION = Property.getProperty("affiliation",
			String.class);

	/**
	 * A property for a {@link KnowledgeBase} instance to store the desired status (e.g. PREVIEW,
	 * FINAL...) of this knowledge base.
	 */
	public static final Property<String> STATUS = Property.getProperty("status", String.class);

	/**
	 * A property for a {@link KnowledgeBase} instance to store the desired filename of this
	 * knowledge base, when it is downloaded as a d3web file from the wiki.
	 */
	public static final Property<String> FILENAME = Property.getProperty("filename", String.class);

	/**
	 * Creation date of a knowledge base, will be created on saving the knowledge base.
	 */
	public static final Property<Date> CREATED = Property.getProperty("created", Date.class);

	/**
	 * @see DefaultAbnormality
	 */
	public static final Property<DefaultAbnormality> DEFAULT_ABNORMALITIY = Property.getProperty(
			"abnormality",
			DefaultAbnormality.class);

	/**
	 * @see AbnormalityNum
	 */
	public static final Property<AbnormalityNum> ABNORMALITIY_NUM = Property.getProperty(
			"abnormalityNum", AbnormalityNum.class);

	/**
	 * Allows to specify the desired display type of date questions.
	 *
	 * @return DateDisplay
	 */
	public static final Property<DateDisplay> DATE_DISPLAY = Property.getProperty("dateDisplay", DateDisplay.class);

	/**
	 * Return the desired display type for the specified date question.
	 * The date display type is defined by the property "dateDisplay" for the date question.
	 * If there is no such property, the "dateDisplay" of the questions
	 * knowledge base object will be used as the default value. If there is no
	 * such knowledge base specific default value, the type DATE is used.
	 *
	 * @param question the question to get the date format for
	 * @return the questions date format
	 * @created 20.08.2012
	 */
	public static DateDisplay getDateDisplay(QuestionDate question) {
		DateDisplay prompt = question.getInfoStore().getValue(DATE_DISPLAY);
		if (prompt == null) {
			prompt = question.getKnowledgeBase().getInfoStore().getValue(DATE_DISPLAY);
		}
		if (prompt == null) {
			prompt = DateDisplay.date;
		}
		return prompt;
	}

	public static boolean isAbstract(Question question) {
		Boolean value = question.getInfoStore().getValue(ABSTRACTION_QUESTION);
		return (value != null) && value;
	}

	/**
	 * Returns if the "unknown" alternative of a specific question should be offered to the user or
	 * not. This is defined by the property "unknownVisible" for the question. If there is no such
	 * property, the "unknownVisbible" property of the question's knowledge base object will be used
	 * as the default value. If there is no such knowledge base specific default value, false is
	 * returned.
	 *
	 * @param question the question to get the unknown visibility for
	 * @return if the question should have "unknown" as a possible answer
	 * @created 20.08.2012
	 */
	public static boolean isUnknownVisible(Question question) {
		Boolean visible = question.getInfoStore().getValue(UNKNOWN_VISIBLE);
		if (visible == null) {
			visible = question.getKnowledgeBase().getInfoStore().getValue(UNKNOWN_VISIBLE);
		}
		return (visible != null) && visible;
	}
}

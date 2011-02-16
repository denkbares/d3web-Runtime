/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software for non commercial use
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package de.d3web.core.knowledge.terminology.info;

import java.util.Date;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
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
	 * used for: QASet doc: specifies what amount of costs is needed to
	 * 'perform' the qaset
	 * 
	 * @return Double
	 */
	public static final Property<Double> COST = Property.getProperty("cost", Double.class);

	/**
	 * Used for: Questions doc: Marks a Question as abstraction question
	 * (derived) or not. Boolean.TRUE means, it is a abstraction question, all
	 * other values means, it is not.
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
	 * used for: QuestionNum doc: valid range of numerical answers of
	 * QuestionNum
	 * 
	 * @return NumericalInterval
	 */
	public static final Property<NumericalInterval> QUESTION_NUM_RANGE =
			Property.getProperty(
					"range", NumericalInterval.class);

	/**
	 * used for question doc: the ids or names of the answers(seperated by ";"),
	 * which is set in PSMethodInit
	 * 
	 * @return String
	 */
	public static final Property<String> INIT = Property.getProperty("init", String.class);

	/**
	 * TODO: Remove when UnknownChoice is implemented
	 * 
	 * used for: Question doc: should UNKNOWN be invisible in questions
	 * 
	 * @return Boolean
	 */
	public static final Property<Boolean> UNKNOWN_VISIBLE = Property.getProperty(
			"unknownVisible", Boolean.class);

	/**
	 * Element Name: Date Label: Date Definition: A date of an event in the
	 * lifecycle of the resource. Comment: Typically, Date will be associated
	 * with the creation or availability of the resource. Recommended best
	 * practice for encoding the date value is defined in a profile of ISO 8601
	 * [W3CDTF] and includes (among others) dates of the form YYYY-MM-DD.
	 */
	public static final Property<String> VERSION =
			Property.getProperty("version", String.class);

	/**
	 * Element Name: Creator Label: Creator Definition: An entity primarily
	 * responsible for making the content of the resource. Comment: Examples of
	 * Creator include a person, an organization, or a service. Typically, the
	 * name of a Creator should be used to indicate the entity.
	 */
	public static final Property<String> AUTHOR = Property.getProperty("author", String.class);

	/**
	 * A property for a {@link KnowledgeBase} instance to store the desired
	 * filename of this knowledge base, when it is downloaded as a d3web file
	 * from the wiki.
	 */
	public static final Property<String> FILENAME = Property.getProperty("filename", String.class);

	/**
	 * Creation date of a knowledge base, will be created on saving the
	 * knowledge base.
	 */
	public static final Property<Date> CREATED = Property.getProperty("created", Date.class);

	/**
	 * A property for a {@link KnowledgeBase} instance that enables setting
	 * UNKNOWN answer option visible by default for all questions
	 */
	public static final Property<Boolean> UNKNOWNBYDEFAULT =
			Property.getProperty("unknownByDefault", Boolean.class);

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

	public static boolean isAbstract(Question question) {
		Boolean value = question.getInfoStore().getValue(ABSTRACTION_QUESTION);
		return value != null && value.booleanValue();
	}

}

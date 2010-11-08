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

import de.d3web.core.knowledge.terminology.IDObject;

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
	 * used for: Question the unit of numerical questions
	 * 
	 * @return String
	 */
	public static final Property<String> UNIT = Property.getProperty("unit", String.class);

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
	 * used for: {@link IDObject} An explanation of the {@link IDObject}
	 * 
	 * @return String
	 */
	public static final Property<String> EXPLANATION = Property.getProperty(
			"explanation", String.class);

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
	 * used for question doc: the ids of the answers(seperated by ";"), which is
	 * set in PSMethodInit
	 * 
	 * @return String
	 */
	public static final Property<String> INIT = Property.getProperty("INIT", String.class);

	/**
	 * used for question doc: the ids of the answers(seperated by ";"), which is
	 * preselected in dialogs
	 * 
	 * @return String
	 */
	public static final Property<String> DEFAULT = Property.getProperty(
			"DEFAULT", String.class);

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
	 * TODO: Remove when UnknownChoice is implemented
	 * 
	 * used for: Question doc: return of getValue of Unknown
	 * 
	 * @return String
	 */
	public static final Property<String> UNKNOWN_VERBALISATION = Property.getProperty(
			"unknown_verbalisation", String.class);

	/**
	 * TODO: Remove and make each MMINFO to a Property
	 * 
	 * used for: QASet, Answer, Diagnosis doc: contains former MMInfo
	 * 
	 * @return MMInfoStorage
	 */
	public static final Property<MMInfoStorage> MMINFO = Property.getProperty(
			"mminfo", MMInfoStorage.class);
}

/*
 * Copyright (C) 2010 denkbares GmbH
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

import de.d3web.core.knowledge.terminology.IDObject;

/**
 * A collection of basic Properties for d3web
 * 
 * @author Joachim Baumeister, hoernlein, Markus Friedrich (denkbares GmbH)
 * @created 07.10.2010
 */
public class BasicProperties {

	/**
	 * used for: Storing infos about a QuestionImage and their AnswerRegions
	 * doc: Used to store a List of Image and Answer Regions
	 * 
	 * @return java.util.list with QuestionImage and AnswerRegions
	 */
	public static final Property IMAGE_QUESTION_INFO = Property.getProperty("image_question_info");

	/**
	 * used for: QASet doc: specifies what amount of costs is needed to
	 * 'perform' the qaset
	 * 
	 * @return double
	 */
	public static final Property COST = Property.getProperty("cost");

	/**
	 * used for: Question the unit of numerical questions
	 * 
	 * @return String
	 */
	public static final Property UNIT = Property.getProperty("unit");

	/**
	 * Used for: Questions doc: Marks a Question as abstraction question
	 * (derived) or not. Boolean.TRUE means, it is a abstraction question, all
	 * other values means, it is not.
	 * 
	 * @return boolean
	 */
	public static final Property ABSTRACTION_QUESTION = Property.getProperty(
			"abstractionQuestion");

	/**
	 * used for: {@link IDObject} An explanation of the {@link IDObject}
	 * 
	 * @return String
	 */
	public static final Property EXPLANATION = Property.getProperty("explanation");

	/**
	 * used for Diagnosis Saves the apriori probability of a diagnosis
	 * 
	 * @return float
	 */
	public static final Property APRIORI = Property.getProperty("apriori");

	/**
	 * used for: QuestionNum doc: valid range of numerical answers of
	 * QuestionNum
	 * 
	 * @return NumericalInterval
	 */
	public static final Property QUESTION_NUM_RANGE = Property.getProperty("range");

	/**
	 * used for question doc: the ids of the answers(seperated by ";"), which is
	 * set in PSMethodInit
	 * 
	 * @return String
	 */
	public static final Property INIT = Property.getProperty("INIT");

	/**
	 * used for question doc: the ids of the answers(seperated by ";"), which is
	 * preselected in dialogs
	 * 
	 * @return String
	 */
	public static final Property DEFAULT = Property.getProperty("DEFAULT");

	/**
	 * TODO: Remove when UnknownChoice is implemented
	 * 
	 * used for: Question doc: should UNKNOWN be invisible in questions
	 * 
	 * @return Boolean
	 */
	public static final Property UNKNOWN_VISIBLE = Property.getProperty("unknownVisible");

	/**
	 * TODO: Remove when UnknownChoice is implemented
	 * 
	 * used for: Question doc: return of getValue of Unknown
	 * 
	 * @return String
	 */
	public static final Property UNKNOWN_VERBALISATION = Property.getProperty("unknown_verbalisation");

	/**
	 * TODO: Remove and make each MMINFO to a Property
	 * 
	 * used for: QASet, Answer, Diagnosis doc: contains former MMInfo
	 * 
	 * @return MMInfoStorage
	 */
	public static final Property MMINFO = Property.getProperty("mminfo");
}

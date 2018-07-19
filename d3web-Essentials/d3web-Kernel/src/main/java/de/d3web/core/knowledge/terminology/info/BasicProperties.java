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

import org.jetbrains.annotations.NotNull;

import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.abnormality.Abnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityNum;
import de.d3web.core.knowledge.terminology.info.abnormality.AbnormalityUtils;
import de.d3web.core.knowledge.terminology.info.abnormality.DefaultAbnormality;
import de.d3web.core.knowledge.terminology.info.abnormality.DynamicAbnormality;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;

/**
 * A collection of basic Properties for d3web
 *
 * @author Joachim Baumeister, hoernlein, Markus Friedrich (denkbares GmbH)
 * @created 07.10.2010
 */
public class BasicProperties {

	/**
	 * used for QASet. Specifies what amount of costs is needed to 'perform' the qaset
	 */
	public static final Property<Double> COST = Property.getProperty("cost", Double.class);

	/**
	 * Returns the costs defined for a question or questionnaire. If no explicit costs are set for the specific item, a
	 * default cost value of 1.0 will be assumed. The method does not accumulate the costs for the children of the
	 * specified qaset, only the directly set costs for the object are returned.
	 *
	 * @param qaset the question of questionnaire to get the costs for
	 * @return the costs of the specified qaset
	 */
	public static double getCost(QASet qaset) {
		Double cost = qaset.getInfoStore().getValue(COST);
		return (cost == null) ? 1.0 : cost;
	}

	/**
	 * Used for Questions. Marks a Question as abstraction question (derived) or not. Boolean.TRUE means, it is a
	 * abstraction question, all other values means, it is not.
	 */
	public static final Property<Boolean> ABSTRACTION_QUESTION = Property.getProperty(
			"abstractionQuestion", Boolean.class);

	/**
	 * Used for Questions. Marks a Question as abstraction question (derived) or not. Boolean.TRUE means, it is a
	 * abstraction question, all other values means, it is not.
	 */
	public static final Property<Visible> VISIBLE = Property.getProperty(
			"visible", Visible.class);

	/**
	 * Used for Diagnosis. Saves the apriori probability of a diagnosis, relative to the default probability. A value of
	 * 2.0 means that the solution will occur twice as often as others, and a value of 0.5 means that the solution has
	 * the half probability. The default value if 1.0, so getting this property always returns a number.
	 */
	public static final Property<Float> APRIORI = Property.getProperty(
			"apriori", Float.class);

	/**
	 * used for: QuestionNum doc: valid range of numerical answers of QuestionNum
	 */
	public static final Property<NumericalInterval> QUESTION_NUM_RANGE =
			Property.getProperty(
					"range", NumericalInterval.class);

	/**
	 * used for question doc: the ids or names of the answers(separated by ";"), which is set in PSMethodInit
	 */
	public static final Property<String> INIT = Property.getProperty("init", String.class);

	/**
	 * used for: Question, Knowledgebase doc: should UNKNOWN be invisible in questions, If it is set to the kb it
	 * represents the default.
	 */
	public static final Property<Boolean> UNKNOWN_VISIBLE = Property.getProperty(
			"unknownVisible", Boolean.class);

	/**
	 * Element Name: Date Label: Date Definition: A date of an event in the lifecycle of the resource. Comment:
	 * Typically, Date will be associated with the creation or availability of the resource. Recommended best practice
	 * for encoding the date value is defined in a profile of ISO 8601 [W3CDTF] and includes (among others) dates of the
	 * form YYYY-MM-DD.
	 */
	public static final Property<String> VERSION =
			Property.getProperty("version", String.class);

	/**
	 * Element Name: Creator Label: Creator Definition: An entity primarily responsible for making the content of the
	 * resource. Comment: Examples of Creator include a person, an organization, or a service. Typically, the name of a
	 * Creator should be used to indicate the entity.
	 */
	public static final Property<String> AUTHOR = Property.getProperty("author", String.class);

	/**
	 * A property for a {@link KnowledgeBase} instance to store the desired affiliation of the author and/or this
	 * knowledge base.
	 */
	public static final Property<String> AFFILIATION = Property.getProperty("affiliation",
			String.class);

	/**
	 * A property for a {@link KnowledgeBase} instance to store the desired status (e.g. PREVIEW, FINAL...) of this
	 * knowledge base.
	 */
	public static final Property<String> STATUS = Property.getProperty("status", String.class);

	/**
	 * A property for a {@link KnowledgeBase} instance to store the desired filename of this knowledge base, when it is
	 * downloaded as a d3web file from the wiki.
	 */
	public static final Property<String> FILENAME = Property.getProperty("filename", String.class);

	/**
	 * Creation date of a knowledge base, will be created on saving the knowledge base.
	 */
	public static final Property<Date> CREATED = Property.getProperty("created", Date.class);

	/**
	 * @see DefaultAbnormality
	 */
	public static final Property<DefaultAbnormality> DEFAULT_ABNORMALITY = Property.getProperty(
			"abnormality",
			DefaultAbnormality.class);

	/**
	 * @see DynamicAbnormality
	 */
	public static final Property<DynamicAbnormality> DYNAMIC_ABNORMALITY = Property.getProperty(
			"abnormalityDynamic",
			DynamicAbnormality.class);

	/**
	 * @deprecated replaced by {@link #DEFAULT_ABNORMALITY}
	 */
	@Deprecated
	public static final Property<DefaultAbnormality> DEFAULT_ABNORMALITIY = DEFAULT_ABNORMALITY;

	/**
	 * @see AbnormalityNum
	 */
	public static final Property<AbnormalityNum> ABNORMALITY_NUM = Property.getProperty(
			"abnormalityNum", AbnormalityNum.class);

	/**
	 * @deprecated replaced by {@link #ABNORMALITY_NUM}
	 */
	@Deprecated
	public static final Property<AbnormalityNum> ABNORMALITIY_NUM = ABNORMALITY_NUM;

	/**
	 * Allows to specify the desired display type of a questions.
	 */
	public static final Property<QuestionDisplay> QUESTION_DISPLAY = Property.getProperty("questionDisplay", QuestionDisplay.class);

	/**
	 * Allows to specify the desired display type of date questions.
	 */
	public static final Property<DateDisplay> DATE_DISPLAY = Property.getProperty("dateDisplay", DateDisplay.class);

	/**
	 * Allows to specify the desired display type of num questions.
	 */
	public static final Property<NumDisplay> NUM_DISPLAY = Property.getProperty("numDisplay", NumDisplay.class);

	/**
	 * Allows to specify the desired display type of the choices of a question.
	 */
	public static final Property<ChoiceDisplay> CHOICE_DISPLAY = Property.getProperty("choiceDisplay", ChoiceDisplay.class);

	/**
	 * Used for Diagnosis. Specified how the solution should be displayed to the user
	 */
	public static final Property<SolutionDisplay> SOLUTION_DISPLAY = Property.getProperty(
			"solutionDisplay", SolutionDisplay.class);

	/**
	 * Allows to specify the desired number of digits of numeric questions. "0" means only integer numbers, negative
	 * value means a maximum of the specified digits, but less are allowed.
	 *
	 * @see #getDigits(QuestionNum)
	 */
	public static final Property<Integer> DIGITS = Property.getProperty("digits", Integer.class);

	/**
	 * Return the desired display type for the specified date question. The date display type is defined by the property
	 * "dateDisplay" for the date question. If there is no such property, the "dateDisplay" of the question's knowledge
	 * base object will be used as the default value. If there is no such knowledge base specific default value, the
	 * type {@link DateDisplay#date} is used.
	 *
	 * @param question the question to get the date format for
	 * @return the questions date format
	 * @created 20.08.2012
	 */
	public static DateDisplay getDateDisplay(QuestionDate question) {
		DateDisplay display = question.getInfoStore().getValue(DATE_DISPLAY);
		if (display == null) {
			display = question.getKnowledgeBase().getInfoStore().getValue(DATE_DISPLAY);
		}
		return (display == null) ? DateDisplay.date : display;
	}

	/**
	 * Return the desired display type for the specified numeric question. The numeric display type is defined by the
	 * property "numDisplay" for the numeric question. If there is no such property, the "numDisplay" of the question's
	 * knowledge base object will be used as the default value. If there is no such knowledge base specific default
	 * value, the type {@link NumDisplay#normal} is used.
	 *
	 * @param question the question to get the num display for
	 * @return the questions display type
	 * @created 20.08.2012
	 */
	public static NumDisplay getNumDisplay(QuestionNum question) {
		NumDisplay display = question.getInfoStore().getValue(NUM_DISPLAY);
		if (display == null) {
			display = question.getKnowledgeBase().getInfoStore().getValue(NUM_DISPLAY);
		}
		return (display == null) ? NumDisplay.normal : display;
	}

	/**
	 * Return the desired display type for the choices of the specified choice question. The choice display type is
	 * defined by the property "choiceDisplay" for the choice question. If there is no such property, the "choiceDisplay"
	 * of the question's knowledge base object will be used as the default value. If there is no such knowledge base specific
	 * default value, the type {@link ChoiceDisplay#normal} is used.
	 *
	 * @param question the question to get the choice display for
	 * @return the questions display type
	 * @created 28.01.2018
	 */
	@NotNull
	public static ChoiceDisplay getChoiceDisplay(QuestionChoice question) {
		ChoiceDisplay display = question.getInfoStore().getValue(CHOICE_DISPLAY);
		if (display == null) {
			display = question.getKnowledgeBase().getInfoStore().getValue(CHOICE_DISPLAY);
		}
		return (display == null) ? ChoiceDisplay.normal : display;
	}

	/**
	 * Return the desired display type for the specified question. The question display type is defined by the
	 * property "questionDisplay" for the question. If there is no such property, the "questionDisplay" of the
	 * question's knowledge base object will be used as the default value. If there is no such knowledge base specific
	 * default value, the type {@link QuestionDisplay#unspecified} is used.
	 *
	 * @param question the question to get the question display for
	 * @return the questions display type
	 * @created 28.01.2018
	 */
	@NotNull
	public static QuestionDisplay getQuestionDisplay(Question question) {
		QuestionDisplay display = question.getInfoStore().getValue(QUESTION_DISPLAY);
		if (display == null) {
			display = question.getKnowledgeBase().getInfoStore().getValue(QUESTION_DISPLAY);
		}
		return (display == null) ? QuestionDisplay.unspecified : display;
	}

	/**
	 * Return the desired display type for the specified solution. The solution display type is defined by the property
	 * "solutionDisplay" for the solution. If there is no such property, the "solutionDisplay" of the question's
	 * knowledge base object will be used as the default value. If there is no such knowledge base specific default
	 * value, the type {@link SolutionDisplay#normal} is used.
	 *
	 * @param solution the solution to get the display type for
	 * @return the solution display type
	 * @created 11.10.2015
	 */
	@NotNull
	public static SolutionDisplay getSolutionDisplay(Solution solution) {
		SolutionDisplay display = solution.getInfoStore().getValue(SOLUTION_DISPLAY);
		if (display == null) {
			display = solution.getKnowledgeBase().getInfoStore().getValue(SOLUTION_DISPLAY);
		}
		return (display == null) ? SolutionDisplay.normal : display;
	}

	/**
	 * Return the desired fractional digits for the specified numeric question. Zero indicated that the value should be
	 * displayed without fractional digits. A positive number indicates a fixed number of digits to be used, even if
	 * they are not required to correctly display the value, e.g. "17.50" instead of "17.5" for 2 digits. A negative
	 * number indicates that a dynamic number of digits should be used, but not more that the specified (absolute)
	 * digits parameter tells, e.g. für digits = -2: 17.126 &rarr; "17.13", but 17.1 &rarr; "17.1" (instead of "17.10"
	 * for digits = +2).
	 * <p>
	 * The fractional digits are defined by the property "digits" for the numeric question. If there is no such
	 * property, the "digits" of the question's knowledge base object will be used as the default value. If there is no
	 * such knowledge base specific default value, {@link Integer#MIN_VALUE}. is used, to indicate that the digits
	 * should be used as required.
	 *
	 * @param question the question to get the date format for
	 * @return the questions date format
	 * @created 20.08.2012
	 */
	public static int getDigits(QuestionNum question) {
		Integer digits = question.getInfoStore().getValue(DIGITS);
		if (digits == null) {
			digits = question.getKnowledgeBase().getInfoStore().getValue(DIGITS);
		}
		return (digits == null) ? Integer.MIN_VALUE : digits;
	}

	/**
	 * Returns true if the question is an abstraction question and therefore should not been answered manually by the
	 * user.
	 *
	 * @param question the question to be checked
	 * @return true if the question is an abstraction question
	 */
	public static boolean isAbstract(Question question) {
		Boolean value = question.getInfoStore().getValue(ABSTRACTION_QUESTION);
		return (value != null) && value;
	}

	/**
	 * Returns true if the question or qContainer should be always visible, if the enclosing qContainer is visible,
	 * regardless of it relevance (indication state).
	 *
	 * @param object the question to be checked
	 * @return true if the question should always be visible
	 */
	public static boolean isAlwaysVisible(InterviewObject object) {
		Visible value = object.getInfoStore().getValue(VISIBLE);
		return value == Visible.always;
	}

	/**
	 * Returns true if the question or qContainer should be never visible, regardless of its relevance (indication
	 * state) or of the enclosing qContainer is relevance.
	 * <p>
	 * Note: For a qContainer to be "never visible" the whole subtree never becomes visible, except a sub-qcontainer
	 * becomes relevant itself. Then this sub-qContainer is treated independently.
	 *
	 * @param question the question to be checked
	 * @return true if the question should never be visible
	 */
	public static boolean isNeverVisible(InterviewObject question) {
		Visible value = question.getInfoStore().getValue(VISIBLE);
		return value == Visible.never;
	}

	/**
	 * Returns if the "unknown" alternative of a specific question should be offered to the user or not. This is defined
	 * by the property "unknownVisible" for the question. If there is no such property, the "unknownVisible" property of
	 * the question's knowledge base object will be used as the default value. If there is no such knowledge base
	 * specific default value, false is returned.
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

	private static final Abnormality EMPTY_ABNORMALITY = new Abnormality() {
		@Override
		public double getValue(Value answerValue) {
			return AbnormalityUtils.getDefault();
		}

		@Override
		public boolean isSet(Value answerValue) {
			return false;
		}
	};

	/**
	 * Returns an abnormality object for the specified question. The method works for any question, regardless of the
	 * type of abnormality defined for the question. The method never returns null. If no abnormality is specified, an
	 * empty abnormality is returned.
	 *
	 * @param question the question to get the abnormality for
	 * @return the abnormality object
	 */
	@NotNull
	public static Abnormality getAbnormality(Question question) {
		// first test for default abnormality
		InfoStore infoStore = question.getInfoStore();
		Abnormality abnormality = infoStore.getValue(DEFAULT_ABNORMALITY);
		if (abnormality != null) return abnormality;

		// if not, test for numeric abnormality
		abnormality = infoStore.getValue(ABNORMALITY_NUM);
		if (abnormality != null) return abnormality;

		// if not, test for dynamic abnormality
		abnormality = infoStore.getValue(DYNAMIC_ABNORMALITY);
		if (abnormality != null) return abnormality;

		// if not return empty abnormality
		return EMPTY_ABNORMALITY;
	}

	/**
	 * Returns an abnormality object for the specified question. The method works for any question, regardless of the
	 * type of abnormality defined for the question. The method never returns null. If no abnormality is specified, an
	 * empty abnormality is returned. If the abnormality is a dynamic abnormality it will automatically be evaluated
	 * according to the session, and the static abnormality is returned.
	 *
	 * @param question the question to get the abnormality for
	 * @param session  the session to evaluate dynamic abnormalities against
	 * @return the (static) abnormality object
	 * @see de.d3web.core.knowledge.terminology.info.abnormality.DynamicAbnormality
	 */
	@NotNull
	public static Abnormality getAbnormality(Question question, Session session) {
		Abnormality abnormality = getAbnormality(question);
		if (abnormality instanceof DynamicAbnormality) {
			abnormality = ((DynamicAbnormality) abnormality).eval(session);
		}
		return abnormality;
	}
}

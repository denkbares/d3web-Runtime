/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.core.knowledge.terminology.info;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * only Property objects are keys for Properties
 * 
 * @author Joachim Baumeister / hoernlein
 * @see de.d3web.core.knowledge.terminology.info.Properties
 */
public class Property {

	/**
	 * used for: {KnowledgeBase, QASet, QContainer, QuestionChoice, Answer, ...}
	 * doc: {some documentation what this property is for} handled by: {project
	 * where peristence for this property is done}
	 * 
	 * @return {class of stored object, if Collection: class of stored objects}
	 */
	public static final Property MY_NEW_PROPERTY = new Property(
			"my_new_property");

	/**
	 * used for: Storing infos about a QuestionImage and their AnswerRegions
	 * doc: Used to store a List of Image and Answer Regions handled by:
	 * d3web-Persistence-Basic
	 * 
	 * @return java.util.list with QuestionImage and AnswerRegions
	 */
	public static final Property IMAGE_QUESTION_INFO = new Property(
			"image_question_info");

	/**
	 * used for: QASet, Answer, Diagnosis doc: contains former MMInfo handled
	 * by: d3web-Persistence-MMInfo
	 * 
	 * @return MMInfoStorage
	 */
	public static final Property MMINFO = new Property("mminfo");

	/**
	 * used for: QASet doc: specifies what amount of costs is needed to
	 * 'perform' the qaset handled by: d3web-Persistence
	 * 
	 * @return double
	 */
	public static final Property COST = new Property("cost");

	/**
	 * used for: QASet doc: specifies what amount of risk is needed to 'perform'
	 * Diagnosis: only applicable to therapies: the negative effects for the
	 * patient scale = 0: no n.e., 1: slight n.e., 2: medium n.e., 3: high n.e.,
	 * 4: very high n.e. (only to be applied if patient dies otherwise) the
	 * qaset handled by: d3web-Persistence
	 * 
	 * @return double
	 */
	public static final Property RISK = new Property("risk");

	/**
	 * used for: QASet doc: specifies what amount of time is needed to 'perform'
	 * the qaset handled by: d3web-Persistence
	 * 
	 * @return double
	 */
	public static final Property TIME = new Property("timeexpenditure");

	/**
	 * used for: NamedObject doc: specifies if a NamedObject is time dependent
	 * handled by: d3web-Persistence, KnowME-App
	 * 
	 * @return Boolean
	 */
	public static final Property TIME_VALUED = new Property("timevalued");

	/**
	 * used for: Question the unit of numerical questions handled by:
	 * d3web-Persistence
	 * 
	 * @return String
	 */
	public static final Property UNIT = new Property("unit");

	/**
	 * used for: Diagnosis doc: For handling "context" diagnosis: diagnosis to
	 * be suppressed in special use cases: The user will never know these
	 * diagnoses exist. Dialog: They will not be shown in any solution Training:
	 * Neither selection nor feedback will show the diagnoses. handled_by:
	 * d3web-Persistence [TODO]:aha:this should go to train/dialog persistence
	 * 
	 * @return boolean
	 */
	public static final Property HIDE_IN_DIALOG = new Property("hide_in_dialog");

	/**
	 * used for: Diagnosis doc: only this property discriminates 'pure'
	 * diagnoses from therapies we should alter this point of view ... handled
	 * by: d3web-Persistence
	 * 
	 * @return boolean
	 */
	public static final Property IS_THERAPY = new Property("is_therapy");

	/**
	 * Used for: Questions doc: Marks a Question as abstraction question
	 * (derived) or not. Boolean.TRUE means, it is a abstraction question, all
	 * other values means, it is not. handled by: d3web-Persistence
	 * 
	 * @return boolean
	 */
	public static final Property ABSTRACTION_QUESTION = new Property(
			"abstractionQuestion");

	/**
	 * used for: Answer doc: [TODO]:doc for EXPLANATION handled by:
	 * [TODO]:handled by for EXPLANATION
	 * 
	 * @return String
	 */
	public static final Property EXPLANATION = new Property("explanation");

	/**
	 * used for Diagnosis Saves the apriori probability of a diagnosis
	 * 
	 * @return float
	 */
	public static final Property APRIORI = new Property("apriori");

	/**
	 * used for: QuestionNum doc: valid range of numerical answers of
	 * QuestionNum handled by: d3web-Persistence
	 * 
	 * @return NumericalInterval
	 */
	public static final Property QUESTION_NUM_RANGE = new Property("range");

	/**
	 * used for: Question doc: should UNKNOWN be invisible in questions handled
	 * by: d3web-Persistence
	 * 
	 * @return Boolean
	 */
	public static final Property UNKNOWN_VISIBLE = new Property(
			"unknownVisible");

	/**
	 * used for: Question doc: return of getValue of AnswerUnknown handled by:
	 * d3web-Persistence
	 * 
	 * @return String
	 */
	public static final Property UNKNOWN_VERBALISATION = new Property(
			"unknown_verbalisation");

	/*
	 * ##########################################################################
	 * ##
	 */

	/**
	 * used for: KnowledgeBase doc: Config file for d3web.Train and d3web.dialog
	 * configuration handled by: d3web-Config-Persistence
	 * 
	 * @see d3web-Config project
	 * @return Config
	 */
	public static final Property CONFIG = new Property("config");

	/*
	 * ##########################################################################
	 * ##
	 */

	/**
	 * used for Question doc: if the question is an inactive follow-question and
	 * this property is set (true), it will be displayed inactivated on the
	 * screen (independent of the appropriate Config). handled by: d3web-dialog
	 * 
	 * @return Boolean
	 */
	public static final Property DIALOG_MQDIALOGS_SHOW_FOLLOWQ_ALWAYS = new Property(
			"dialog.mqdialogs.show_followq_always");

	/**
	 * used for question doc: the ids of the answers(seperated by ";"), which is
	 * set in PSMethodInit
	 * 
	 * @return String
	 */
	public static final Property INIT = new Property("INIT");

	/**
	 * used for question doc: the ids of the answers(seperated by ";"), which is
	 * preselected in dialogs
	 * 
	 * @return String
	 */
	public static final Property DEFAULT = new Property("DEFAULT");

	/*
	 * ##########################################################################
	 * ##
	 */

	/**
	 * used for: CaseObject doc: this is the metadata blob handled by:
	 * d3web-CaseRepository
	 * 
	 * @return de.d3web.caserepository.MetaData
	 */
	public static final Property CASE_METADATA = new Property("case_metadata");

	/**
	 * used for: CaseObject doc: this is the DCMarkup of the knowledgebase
	 * suited for the caserepository handled by: d3web-CaseRepository
	 * 
	 * @return DCMarkup
	 */
	public static final Property CASE_KNOWLEDGEBASE_DESCRIPTOR = new Property(
			"case_knowledgebase_descriptor");

	/**
	 * used for: CaseObject doc: this is a comment for a case handled by:
	 * d3web-CaseRepository
	 * 
	 * @deprecated use DCElement.DESCRIPTION instead
	 * @return PropertiesUtilities$CDataString
	 */
	@Deprecated
	public static final Property CASE_COMMENT = new Property("case_comment");

	/**
	 * used for: CaseObject doc: this specifies the system which created the
	 * case handled by: d3web-CaseRepository
	 * 
	 * @return String
	 */
	public static final Property CASE_SOURCE_SYSTEM = new Property(
			"case_source_system");

	/**
	 * used for: CaseObject <br>
	 * doc: critiquing reports from NLP (natural language processing) which
	 * compares the user's diagnoses with the system's using text analysis.<br>
	 * handled by: d3web-Dialog-MedicalKbPlugin
	 * 
	 * @return LinkedList of PropertiesUtilities$CDataString
	 */
	public static final Property CASE_CRITIQUE_TEXT = new Property(
			"case_critique_text");

	/*
	 * ##########################################################################
	 * ##
	 */

	/**
	 * used for: heuristic problem solver. If one diagnosis is established, then
	 * finish case Only the best diagnosis is returned as solution
	 */
	public static final Property SINGLE_FAULT_ASSUMPTION = new Property(
			"single_fault_assumption");

	/**
	 * used for: heuristic problem solver. In contrast to SFA, the case is not
	 * quited after establishing the first diagnosis, but only the best (highest
	 * score) diagnosis is returned.
	 */
	public static final Property BEST_SOLUTION_ONLY = new Property(
			"best_solution_only_strategy");

	/**
	 * used for: dialog should abort case, if the single fault assuption
	 * strategy is used
	 * 
	 * @return Boolean
	 */
	public static final Property HDT_ABORT_CASE_SFA = new Property(
			"abort_case_sfa");

	/*
	 * ##########################################################################
	 * ##
	 */

	/**
	 * used for distributed reasoning and dialog control handled by special
	 * projects -> KnowWE
	 * 
	 * @return Boolean
	 */
	public static final Property EXTERNAL = new Property("external");

	/**
	 * used for distributed reasoning and dialog control handled by special
	 * projects -> KnowWE
	 * 
	 * @return String
	 */
	public static final Property FOREIGN_NAMESPACE = new Property("foreign_namespace");

	/**
	 * used for distributed reasoning and dialog control handled by special
	 * projects -> KnowWE
	 * 
	 * @return Boolean
	 */
	public static final Property PRIVATE = new Property("private");

	/**
	 * used for terminological interface -> intergration (no) and alignment
	 * (yes) handled by special projects -> KnowWE
	 * 
	 * @return Boolean
	 */
	public static final Property FOREIGN = new Property("foreign");

	/**
	 * used for knowledge base constants handled by denkbar/cc formula
	 * expressions
	 * 
	 * @return cc.knowme.expression.eval.ConstantsCollection
	 */
	public static final Property CONSTANTS = new Property("constants");

	/**
	 * used for time database purge hints handled by denkbar/cc
	 * DefaultPurgeHandler
	 * 
	 * @return java.lang.String
	 */
	public static final Property PURGE_HINT = new Property("timeDB.purge_hint");

	/**
	 * A KMW specific object, will be removed in increment 2 (second half of
	 * 2010)
	 * 
	 * @return KMWMasterData
	 */
	public static final Property KMW_MASTER_DATA = new Property("KMWMasterData");

	/**
	 * The mal-answering propability of a question
	 * 
	 * @return Double
	 */
	public static final Property malAnsweringPropability = new Property("malAnsweringPropability");

	/*
	 * ##########################################################################
	 * ##
	 */

	private static Collection<Property> allPropertys;

	static {
		allPropertys = new LinkedList<Property>();
		for (Field f : Property.class.getFields()) {
			if (isOKField(f)) try {
				allPropertys.add((Property) f.get(null));
			}
			catch (IllegalArgumentException e) {
				Logger.getLogger(Property.class.getName()).throwing(
						Property.class.getName(), "static {...}", e);
			}
			catch (IllegalAccessException e) {
				Logger.getLogger(Property.class.getName()).throwing(
						Property.class.getName(), "static {...}", e);
			}
		}
	}

	private static boolean isOKField(Field f) {
		if (!f.getType().equals(Property.class)) return false;
		if (!f.getName().matches("[0-9A-Z_]+")) return false;
		if (!Modifier.isFinal(f.getModifiers())) return false;
		if (!Modifier.isPublic(f.getModifiers())) return false;
		if (!Modifier.isStatic(f.getModifiers())) return false;
		return true;
	}

	/**
	 * Properties that are persistent in a knowledge base
	 */
	private static Collection<Property> basicPropertys;

	static {
		basicPropertys = new LinkedList<Property>();
		basicPropertys.add(Property.APRIORI);
		basicPropertys.add(Property.COST);
		basicPropertys.add(Property.RISK);
		basicPropertys.add(Property.TIME);
		basicPropertys.add(Property.UNIT);
		basicPropertys.add(Property.HIDE_IN_DIALOG);
		basicPropertys.add(Property.IS_THERAPY);
		basicPropertys.add(Property.EXPLANATION);
		basicPropertys.add(Property.UNKNOWN_VISIBLE);
		basicPropertys.add(Property.UNKNOWN_VERBALISATION);
		basicPropertys.add(Property.QUESTION_NUM_RANGE);
		basicPropertys.add(Property.ABSTRACTION_QUESTION);
		basicPropertys.add(Property.SINGLE_FAULT_ASSUMPTION);
		basicPropertys.add(Property.BEST_SOLUTION_ONLY);
		basicPropertys.add(Property.EXTERNAL);
		basicPropertys.add(Property.FOREIGN_NAMESPACE);
		basicPropertys.add(Property.PRIVATE);
		basicPropertys.add(Property.FOREIGN);
		basicPropertys.add(Property.INIT);
		basicPropertys.add(Property.DEFAULT);
		basicPropertys.add(Property.PURGE_HINT);
		basicPropertys.add(Property.KMW_MASTER_DATA);
		basicPropertys.add(Property.malAnsweringPropability);
	}

	private String name;

	public Property(String name) {
		this.name = name;
	}

	/**
	 * this method should only be used by persistence
	 * 
	 * @return String internal name of this property
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * this method should only be used by persistence
	 * 
	 * @param name
	 * @return PropertyDescriptor with name.equals(name)
	 */
	public static Property getProperty(String name) {
		for (Property pd : allPropertys) {
			if (pd.getName().equals(name)) {
				return pd;
			}
		}
		Logger.getLogger(Property.class.getName()).warning(
				"no property found for '" + name + "'");
		return null;
	}

	/**
	 * Properties that are persistent in a knowledge base
	 */
	public static Collection<Property> getBasicPropertys() {
		return Collections.synchronizedCollection(basicPropertys);
	}

	public static Collection<Property> getAllPropertys() {
		return allPropertys;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Property)) return false;
		return ((Property) obj).getName().equals(this.getName());
	}

	/**
	 * This method is called immediately after an object of this class is
	 * deserialized. To avoid that several instances of a unique object are
	 * created, this method returns the current unique instance that is equal to
	 * the object that was deserialized.
	 * 
	 * @author georg
	 */
	private Object readResolve() {
		return getProperty(getName());
	}

}
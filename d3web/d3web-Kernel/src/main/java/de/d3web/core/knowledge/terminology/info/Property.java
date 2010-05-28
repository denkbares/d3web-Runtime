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
	 * used for: KnowledgeBase doc: contains the selectd quality checks and its
	 * settings handled by: d3web-Quasimodu
	 * 
	 * @return java.util.List with selected QualityChecks
	 */
	public static final Property QUASIMODU_SELECTED_CHECKS = new Property(
			"quasimodu");

	/**
	 * used for: QASet, Answer, Diagnosis doc: contains former MMInfo handled
	 * by: d3web-Persistence-MMInfo
	 * 
	 * @return MMInfoStorage
	 */
	public static final Property MMINFO = new Property("mminfo");

	/**
	 * used for: KnowledgeBase doc: contains the tasks handled by: KnowME-Tasks
	 * 
	 * @return java.util.List with Task-Objects
	 */
	public static final Property TASKS = new Property("tasks");

	/**
	 * used for: KnowledgeBase doc: contains the POQS for the Tests handled by:
	 * KnowME-POQSTest
	 * 
	 * @return java.util.List with POQS-Objects
	 */
	public static final Property POQS_TEST = new Property("poqsTest");

	/**
	 * used for: KnowledgeBase doc: contains the POQS for the Trainer handled
	 * by: KnowME-POQSTrainer
	 * 
	 * @return java.util.List with POQS-Objects
	 */
	public static final Property POQS_TRAINER = new Property("poqsTrainer");

	/**
	 * used for: KnowledgeBase doc: contains the UnitCases handled by:
	 * KnowME-Plugin-UnitCaseEditor
	 * 
	 * @return java.util.List with CaseObjects
	 */
	public static final Property UNITCASES = new Property("unitCases");

	/**
	 * used for: KnowledgeBase doc: contains some comments on the KnowledgeBase
	 * handled by: d3web-Persistence
	 * 
	 * @return String
	 */
	public static final Property COMMENT = new Property("comment");

	/**
	 * used for: KnowledgeBase doc: contains basepath for supportknowledge
	 * 
	 * @return String
	 */
	public static final Property SUPPORT_KNOWLEDGE_BASEDIRECTORY = new Property(
			"support_knowledge_basedirectory");

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
	 * used for: Question the unit of numerical questions, in opposite to UNIT
	 * this is some string preceeding the question value example: dilutions are
	 * notated as 1: x and not as x % to be able to treat these questions as
	 * numerical and not as mc there is the need to have some prefix handled by:
	 * d3web-Persistence
	 * 
	 * @return String
	 */
	public static final Property PREFIX_UNIT = new Property("prefixunit");

	/**
	 * used for: Question the unit of numerical questions handled by:
	 * d3web-Persistence
	 * 
	 * @return String
	 */
	public static final Property UNIT = new Property("unit");

	/**
	 * used for: Question The unit of numerical questions according to
	 * international standard units
	 * 
	 * @link http://physics.nist.gov/cuu/Units/ handled by:
	 *       d3web-Train-Persistence [TODO]:aha:this should go to base
	 *       persistence
	 * @return de.d3web.Train.persistence.SIUnit
	 */
	public static final Property SI_UNIT = new Property("siunit");

	/**
	 * [TODO]:georg: ICD10 has to return a List (StringList)!! used for:
	 * Diagnosis specifies the icd10 code associated with this diagnosis
	 * 
	 * @link http://www.who.int/whosis/icd10/ handled by:
	 * @return String
	 */
	public static final Property ICD10 = new Property("icd10");

	/**
	 * used for: Question, Diagnosis doc: This property stores relevancy classes
	 * ("Relevanzklassen") for questions (also for diagnoses now). partition
	 * classes are is stored/retrieved as Integer objects (1..10). handled_by:
	 * d3web-SharedKnowledge
	 * 
	 * @return List of Integer
	 */
	public static final Property PARTITION_CLASS = new Property(
			"partition_class");

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

	public static final Property HIDE_IN_TRAINING = new Property(
			"hide_in_training");

	/**
	 * used for: Diagnosis, QASet doc: If true, the diagnosis/qaSet won't be
	 * shown anywhere in dialog. For Diagnosis: In contrast to "HIDE_IN_DIALOG",
	 * which is used for "context"-diagnoses, this property describes, if a
	 * diagnosis shall be hidden in ALL diagnosis-hierarchies. If true, the
	 * diagnosis will neither be shown in any solution nor shown in any
	 * diag-hierarchy (e.g. hierarchy to select additional diags). handled by:
	 * d3web-Interface-SC-CWD
	 * 
	 * @return Boolean
	 */
	public static final Property HIDE_IN_ALL_HIERARCHIES = new Property(
			"hide_in_all_hierarchies");

	/**
	 * used for: Diagnosis doc: only this property discriminates 'pure'
	 * diagnoses from therapies we should alter this point of view ... handled
	 * by: d3web-Persistence
	 * 
	 * @return boolean
	 */
	public static final Property IS_THERAPY = new Property("is_therapy");

	/**
	 * used for: diagnoses<br>
	 * doc: filter for dialog diagnosis view<br>
	 * handled by: d3web-Persistence<br>
	 * 
	 * @return boolean
	 */
	public static final Property IS_RISK_DIAGNOSIS = new Property("is_risk_diagnosis");

	/**
	 * used for: Diagnosis doc: for follow-up-sessions other diagnoses and
	 * therapies may be important as opposed to 'normal' sessions * in 'normal'
	 * sessions all diagnosis are shown where IS_FUS_SOLUTION does not exist or
	 * is false * in fus all diagnoses are shown (but not with P000 as root but
	 * some other diagnosis) handled by: d3web-Persistence [TODO]:aha:this
	 * should go to base persistence
	 * 
	 * @return boolean
	 */
	public static final Property IS_FUS_SOLUTION = new Property(
			"is_fus_solution");

	/**
	 * used for: Diagnosis doc: d3web.Train authors may store compact general
	 * explanations for diagnoses or therapies here - this is not explanation
	 * handled by: d3web-Persistence [TODO]:aha:this should go to base
	 * persistence
	 * 
	 * @return CDataString
	 */
	public static final Property SOLUTION_FEEDBACK_EXPLANATION = new Property(
			"solution_feedback_explanation");

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
	 * used for: KnowledgeBase doc: this is the baseurl to load additional
	 * knowledge from handled by: d3web-Persistence
	 * 
	 * @return URL
	 */
	public static final Property BASE_URL = new Property("base_url");

	/**
	 * used for: Answer doc: [TODO]:doc for EXPLANATION handled by:
	 * [TODO]:handled by for EXPLANATION
	 * 
	 * @return String
	 */
	public static final Property EXPLANATION = new Property("explanation");

	/**
	 * used for: PSMethodDiagnosisDecisionGraph doc: [TODO]:doc for
	 * DIAGNOSIS_TYPE possible values: "OR", "AND", "XOR", "NORMAL" handled by:
	 * [TODO]:handled by for DIAGNOSIS_TYPE
	 * 
	 * @return String
	 */
	public static final Property DIAGNOSIS_TYPE = new Property("diagnosisType");

	/**
	 * used for Diagnosis Saves the apriori probability of a diagnosis
	 * 
	 * @return float
	 */
	public static final Property APRIORI = new Property("apriori");

	/**
	 * used for: PSMethodHeuristic - decision tree doc: [TODO]:doc for
	 * PROBLEM_TYPE handled by: [TODO]:handled by for PROBLEM_TYPE
	 * 
	 * @return String
	 */
	public static final Property PROBLEM_TYPE = new Property("problemType");

	/**
	 * used for: QuestionDate doc: type of input; possible values: "date": only
	 * date (without time) is required and possible "time": only time is
	 * required and possible "date_time": date and time is required handled by:
	 * d3web-Persistence
	 * 
	 * @see QuestionDate.TYPE_{DATE, TIME, DATE_TIME}
	 * @return String
	 */
	public static final Property QUESTION_DATE_TYPE = new Property(
			"questionDateType");

	/**
	 * used for: QuestionNum doc: valid range of numerical answers of
	 * QuestionNum handled by: d3web-Persistence
	 * 
	 * @return NumericalInterval
	 */
	public static final Property QUESTION_NUM_RANGE = new Property("range");

	/**
	 * used for: QuestionNum doc: specifies (esp. for QuestionNum) if an
	 * int-number is required as answer handled by: d3web-dialog
	 * 
	 * @return Boolean
	 */
	public static final Property INT_NUMBER_REQUIRED = new Property(
			"intNumberRequired");

	/**
	 * [TODO]:georg: handling of "significantDigits" in dialog used for:
	 * QuestionNum doc: specifies (esp. for QuestionNum) the number of
	 * significant digits of the (double-)answer handled by: d3web-dialog
	 * 
	 * @return Integer
	 */
	public static final Property SIGNIFICANT_DIGITS = new Property(
			"significantDigits");

	/**
	 * used for: QuestionNum doc: number of rows of the textfield in dialog
	 * handled by: d3web-Persistence
	 * 
	 * @return [TODO]:comment for QUESTION_TEXT_ROWCOUNT
	 */
	public static final Property QUESTION_TEXT_ROWCOUNT = new Property(
			"rowCount");

	/**
	 * used for: Question doc: should text of a question be suppressed handled *
	 * by: d3web-Dialog
	 * 
	 * @return Boolean
	 */
	public static final Property SUPPRESS_QUESTION_TEXT = new Property(
			"suppressQuestionText");

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

	/**
	 * used for: Question doc: verbalisation of AnswerChoiceYes handled by:
	 * d3web-Persistence, KnowME
	 * 
	 * @return String
	 */
	public static final Property YES_VERBALISATION = new Property(
			"yes_verbalisation");

	/**
	 * used for: Question doc: verbalisation of AnswerChoiceNo handled by:
	 * d3web-Persistence, KnowME
	 * 
	 * @return String
	 */
	public static final Property NO_VERBALISATION = new Property(
			"no_verbalisation");

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
	 * used for QuestionMC <br>
	 * doc: if this property is set, an "all"-alternative will be shown for
	 * MC-questions in the dialog. If the user activates this alternative, the
	 * MC-alternatives of the question will be set all together.<br>
	 * handled by: d3web-dialog
	 * 
	 * @return Boolean
	 */
	public static final Property DIALOG_SHOW_SELECT_ALL_ALTERNATIVE = new Property(
			"dialog.mcq.show_select_all_alternative");

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
	 * used for: QASet doc: mode, in which the answers of questions shall be
	 * presented on the screen handled by: d3web-dialog
	 * 
	 * @return String ("below", "beside", "popup")
	 */
	public static final Property DIALOG_MQDIALOG_RETRIEVALTYPE = new Property(
			"dialog.retrievaltype");

	/**
	 * used for: QContainer doc: column-width (in characters) of each column in
	 * which the answers are presented on the screen handled by: d3web-dialog
	 * 
	 * @return Integer
	 */
	public static final Property DIALOG_MQDIALOG_COLWIDTH = new Property(
			"dialog.colwidth");

	/**
	 * used for: QContainer doc: average length (in characters) of the
	 * question-headers (q.getText() or "prompt") handled by: d3web-dialog
	 * 
	 * @return Integer
	 */
	public static final Property DIALOG_MQDIALOG_HEADERWIDTH = new Property(
			"dialog.headerwidth");

	/**
	 * used for: QContainer doc: vertical space between two questions in pixel,
	 * as they are presented on the screen handled by: d3web-dialog
	 * 
	 * @return Integer
	 */
	public static final Property DIALOG_MQDIALOG_QUESTIONSPACE = new Property(
			"dialog.questionspace");

	/**
	 * used for QuestionOC doc: mode, in which the answers of a QuestionOC shall
	 * be presented on the screen handled by: d3web-dialog
	 * 
	 * @return String ("radio_buttons", "listbox")
	 */
	public static final Property DIALOG_BOXED_QOC_RETRIEVALTYPE = new Property(
			"dialog.qoc.retrievaltype");

	/**
	 * used for Question doc: if this property is set, a line-break will be
	 * inserted ahead of the question. handled by: d3web-dialog
	 * 
	 * @return Boolean
	 */
	public static final Property DIALOG_BOXED_FORCE_LINEBREAK_AHEAD_QUESTION = new Property(
			"dialog.boxed.force_linebreak_ahead");

	/**
	 * used for Question doc: if this property is set, a line-break will be
	 * inserted after the question. handled by: d3web-dialog
	 * 
	 * @return Boolean
	 */
	public static final Property DIALOG_BOXED_FORCE_LINEBREAK_AFTER_QUESTION = new Property(
			"dialog.boxed.force_linebreak_after");

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

	/**
	 * used for: QContainer doc: the column-count within MQBoxedDialog (number
	 * of questions side by side in a row) will not be greater than this limit
	 * handled by: d3web-dialog
	 * 
	 * @return Integer
	 */
	public static final Property DIALOG_BOXED_MAXCOLWIDTH = new Property(
			"dialog.boxed.maxcolwidth");

	/**
	 * used for: QContainer doc: the minimal count of chars that have to fit in
	 * a column handled by: d3web-dialog
	 * 
	 * @return Integer
	 */
	public static final Property DIALOG_BOXED_MINCHARCOUNT_PER_COL = new Property(
			"dialog.boxed.mincharcount_per_col");

	/**
	 * used for: QContainer doc: the maximal count of answer-alternatives per
	 * columns within a question-cell handled by: d3web-dialog
	 * 
	 * @return Integer
	 */
	public static final Property DIALOG_BOXED_MAXANSWERCOUNT_PER_QCOL = new Property(
			"dialog.boxed.maxanswercount_per_qcol");

	/**
	 * used for: KnowledgeBase doc: Hashtable that maps names (String) of
	 * reports to de.d3web.dialog.persistence.ReportTemplateStorage Objects
	 * (used to create reports for a case) handled by: d3web-dialog
	 * 
	 * @return Hashtable
	 */
	public static final Property DIALOG_REPORTS = new Property("dialog.reports");

	/**
	 * used for: KnowledgeBase doc: Generator-Object, which may automatically
	 * generate the casename of an Session handled by: d3web-dialog
	 * 
	 * @return de.d3web.templateedit.core.Generator
	 */
	public static final Property DIALOG_CASENAME_GEN = new Property(
			"dialog.casename_gen");

	/**
	 * used for: QuestionChoice <br>
	 * doc: true for questions that can be modified (answers changed) through a
	 * dialog interface <br>
	 * handled by: d3web-Dialog-OnlineModificationPlugin <br>
	 * 
	 * @return Boolean
	 */
	public static final Property DIALOG_MODIFIABLE_QUESTION = new Property(
			"dialog.modifiable_question");

	/**
	 * used for: Session doc: name of the patient (there is no question for it
	 * in the kb) handled by: d3web-dentist
	 * 
	 * @return String
	 */
	public static final Property DENTIST_PATIENT_NAME = new Property(
			"dentist.patient_name");

	/**
	 * used for: Session doc: birth of the patient (there is no question for it
	 * in the kb) handled by: d3web-dentist
	 * 
	 * @return String
	 */
	public static final Property DENTIST_PATIENT_BIRTH = new Property(
			"dentist.patient_birth");

	/**
	 * used for: Answer <br>
	 * doc: the value "true" states that the answer is visible (esp.
	 * AnswerChoice) <br>
	 * handled by: d3web-Dialog-OnlineModificationPlugin <br>
	 * 
	 * @return Boolean
	 */
	public static final Property ANSWER_VISIBILITY = new Property(
			"dialog.onlinemodificationplugin.ans_visibility");

	/*
	 * ##########################################################################
	 * ##
	 */

	/**
	 * used for: Session<br>
	 * doc: Map maps Questions to user-defined weights<br>
	 * handled by: *d3web-CaseRepository*
	 * 
	 * @return Map<Question, Double>
	 */
	public static final Property CASE_USER_DEFINED_WEIGHTS = new Property(
			"case_user_defined_weights");

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
	 * used for: enable a subproblemsolver for heuristic decisiontrees
	 * 
	 * @return Boolean
	 */
	public static final Property HEURISTIC_DECISION_TREE = new Property(
			"heuristic_decision_tree");

	/**
	 * used for: heuristic problem solver. If one diagnosis is established, then
	 * suggest all children
	 * 
	 * @return Boolean
	 */
	public static final Property ESTABLISH_REFINE_STRATEGY = new Property(
			"establish_refine_strategy");

	/**
	 * used for: heuristic problem solver. If one diagnosis is excluded, then
	 * exclude all children
	 * 
	 * @return Boolean
	 */
	public static final Property EXCLUDE_DISCARD_STRATEGY = new Property(
			"exclude_discard_strategy");

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

	/**
	 * used for: scm problem-solver: if activated, then the scm sets its score
	 * to 0 by default, when the heuristic ps has excluded the solution
	 */
	public static final Property RULEBASED_EXCLUSION = new Property("rulebased_exclusion");

	/**
	 * used for: scm problem-solver: wether to use simple SC-ProblemSolver
	 */
	public static final Property SC_PROBLEMSOLVER_SIMPLE = new Property(
			"simple_sc_problem_solver");

	/**
	 * used for: d3web-distributed: storing namespace id of used terminology
	 */
	public static final Property TERMINOLOGY_USED = new Property(
			"terminology");

	/*
	 * ##########################################################################
	 * ##
	 */

	/**
	 * used for: Session doc: hashtable for NuWiBs-properties handled by:
	 * NuWiBs-Dialog
	 * 
	 * @return Hashtable
	 */
	public static final Property NUWIBS_PROPERTIES = new Property(
			"nuwibs_properties");

	/*
	 * ##########################################################################
	 * ##
	 */

	/**
	 * used for: knowledgebase doc: optional (!) short descriptions for
	 * NamedObjects handled by: VIKAMINE
	 * 
	 * @return String
	 */

	public static final Property SHORT_DESCRIPTION = new Property(
			"short_description");

	/**
	 * used for: Dialog. Boolean flag handled by: KnowME
	 * 
	 * @return Boolean
	 */

	public static final Property PICTURE_QUESTION = new Property(
			"picture_question");

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
	 * used for constraint between mc answers handled especially by dialog(2)
	 * 
	 * @return LinkedList<LinkedList<String>>
	 */
	public static final Property MC_CONSTRAINTS = new Property("mc_constraints");

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
	 * The actual version
	 * 
	 * @return String
	 */
	public static final Property VERSION = new Property("Version");

	/**
	 * Simply Property to store dates as Strings
	 * 
	 * @return String
	 */
	public static final Property DATE = new Property("Date");

	/**
	 * Simple Property to store as kb description as String
	 * 
	 * @return String
	 */
	public static final Property DESCRIPTION = new Property("Description");

	/**
	 * The available languages of the kb
	 * 
	 * @return String
	 */
	public static final Property LANGUAGES = new Property("Languages");

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
		basicPropertys.add(Property.COMMENT);
		basicPropertys.add(Property.SHORT_DESCRIPTION);
		basicPropertys.add(Property.COST);
		basicPropertys.add(Property.RISK);
		basicPropertys.add(Property.TIME);
		basicPropertys.add(Property.UNIT);
		basicPropertys.add(Property.PREFIX_UNIT);
		basicPropertys.add(Property.ICD10);
		basicPropertys.add(Property.PARTITION_CLASS);
		basicPropertys.add(Property.HIDE_IN_DIALOG);
		basicPropertys.add(Property.HIDE_IN_TRAINING);
		basicPropertys.add(Property.IS_THERAPY);
		basicPropertys.add(Property.BASE_URL);
		basicPropertys.add(Property.EXPLANATION);
		basicPropertys.add(Property.SOLUTION_FEEDBACK_EXPLANATION);
		basicPropertys.add(Property.IS_FUS_SOLUTION);
		basicPropertys.add(Property.UNKNOWN_VISIBLE);
		basicPropertys.add(Property.UNKNOWN_VERBALISATION);
		basicPropertys.add(Property.YES_VERBALISATION);
		basicPropertys.add(Property.NO_VERBALISATION);
		basicPropertys.add(Property.QUESTION_NUM_RANGE);
		basicPropertys.add(Property.QUESTION_DATE_TYPE);
		basicPropertys.add(Property.QUESTION_TEXT_ROWCOUNT);
		basicPropertys.add(Property.ABSTRACTION_QUESTION);
		basicPropertys.add(Property.SUPPORT_KNOWLEDGE_BASEDIRECTORY);
		basicPropertys.add(Property.HEURISTIC_DECISION_TREE);
		basicPropertys.add(Property.ESTABLISH_REFINE_STRATEGY);
		basicPropertys.add(Property.EXCLUDE_DISCARD_STRATEGY);
		basicPropertys.add(Property.SINGLE_FAULT_ASSUMPTION);
		basicPropertys.add(Property.BEST_SOLUTION_ONLY);
		basicPropertys.add(Property.DIALOG_SHOW_SELECT_ALL_ALTERNATIVE);
		basicPropertys.add(Property.PICTURE_QUESTION);
		basicPropertys.add(Property.SUPPRESS_QUESTION_TEXT);
		basicPropertys.add(Property.EXTERNAL);
		basicPropertys.add(Property.FOREIGN_NAMESPACE);
		basicPropertys.add(Property.PRIVATE);
		basicPropertys.add(Property.FOREIGN);
		basicPropertys.add(Property.RULEBASED_EXCLUSION);
		basicPropertys.add(Property.MC_CONSTRAINTS);
		basicPropertys.add(Property.SC_PROBLEMSOLVER_SIMPLE);
		basicPropertys.add(Property.TERMINOLOGY_USED);
		basicPropertys.add(Property.INIT);
		basicPropertys.add(Property.DEFAULT);
		basicPropertys.add(Property.PURGE_HINT);
		basicPropertys.add(Property.VERSION);
		basicPropertys.add(Property.DATE);
		basicPropertys.add(Property.DESCRIPTION);
		basicPropertys.add(Property.LANGUAGES);
		basicPropertys.add(Property.KMW_MASTER_DATA);
		basicPropertys.add(Property.malAnsweringPropability);
	}

	private String name;

	private Property() { /* hide empty constructor */
	}

	private Property(String name) {
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
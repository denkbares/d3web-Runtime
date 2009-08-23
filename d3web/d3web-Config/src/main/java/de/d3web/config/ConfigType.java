/*
 * Created on 01.12.2003
 */
package de.d3web.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * ConfigType (in )
 * de.d3web.config
 * d3web-Config
 * @author hoernlein
 * @date 01.12.2003
 */
public class ConfigType {
	
	public final static ConfigType TRAIN = new ConfigType("d3web.Train");
	public final static ConfigType DIALOG = new ConfigType("d3web.Dialog");
	
	public static class ConfigGroup {
		private String name;
		private List<String> keys = new LinkedList<String>();
		
        private ConfigGroup() { /* forbid empty constructor */ }
		public ConfigGroup(String name) { this.name = name; }
		
        public String getName() { return this.name; }
		public List<String> getKeys() {
			return Collections.unmodifiableList(this.keys);
		}
		public void addKey(String key) { this.keys.add(key); }
		public void addKeys(List<String> keys) { this.keys.addAll(keys); }
	}
	
	static {
		
		ConfigGroup gr_system = new ConfigGroup("System");
		gr_system.addKeys(Arrays.asList(new String[]{
			"D3WEBTRAIN_HOMEPAGE",
			"IWEB_TRACE",
			"USE_EXPERIMENTAL_EXTERNAL_PROXY",
			"ALLOWED_LOGS",
			"SESSION_TIMEOUT",
			"FINISH_TIMEOUT",
			"BASE_DIR",
			"SYSTEMS_DIR",
			"GEN_SYSTEMS_DIR",
			"MULTIMEDIA_DIR",
			"THUMBS_DIR",
			"UPLOAD_DIR",
			"DOS_GEN_SYSTEMS_DIR",
			"USE_GEN_SYSTEMS_DIR",
			"IMAGES_DIR",
			"DEVELOPER_MODE",
			"SEARCH_PANE_HEIGHT",
			"SHOW_CONFIGBUTTON",
			"FINALFEEDBACK_FILEOUTPUT",
			"FINALFEEDBACK_DIR",
			"DOMAIN_PRESELECT",
			"HIDE_EMPTY_DOMAINS",
            "DOMAIN_TITLE_NAME",
			"WANTED_MAIN_WINDOW_WIDTH",
			"WANTED_MAIN_WINDOW_HEIGHT",
			"PREFERRED_MAIN_WINDOW_WIDTH",
			"PREFERRED_MAIN_WINDOW_HEIGHT",
			"WANTED_SCREEN_WIDTH",
			"WANTED_SCREEN_HEIGHT",
            "TEST_FOR_MEDIAPLAYER",
			"EXTRA_WINDOW_OPTS",
			"EXTERNAL_EVENT_SOURCE",
			"PERSISTENT_SESSIONS",
			"PERSISTENT_SESSIONS_ALLOW_USERLOAD",
			"USER_MAY_LEAVE_CASE",
			"TIME_SLICE_FOR_CHECK",
			"DIRECTORY_BASED_CONFIGURATIONS_DELETE_ONLY",
            "STRANGE_GETINORDER_OVERRIDE",
		}));
		
		ConfigGroup gr_layout = new ConfigGroup("Layout");
		gr_layout.addKeys(Arrays.asList(new String[]{
            "COLOR_BORDER",
            "WIDTH_BORDER",
            "COLOR_BORDER_SCROLLER",
            "COLOR_BORDER_MAP",
            "COLOR_BORDER_SCROLLER_MAP",
            "COLOR_CONTENT",
            "COLOR_CONTENT_SCROLLER",
            "COLOR_CONTENT_MAP",
            "COLOR_CONTENT_SCROLLER_MAP",
            "COLOR_INPAGEBOX",
            "COLOR_INPAGEBOX_SCROLLER",
            "COLOR_INPAGEBOX_MAP",
            "COLOR_INPAGEBOX_SCROLLER_MAP",
            "COLOR_ACTIVE_TOPIC",
            "COLOR_WARNING",
            "COLOR_OK",
            "COLOR_GREY_LIGHT",
            "COLOR_FRAMES",
            "WIDTH_FRAMES",
            "COLOR_HEADLINES",
            "COLOR_GREEN_DARK",
            "COLOR_GREEN_LIGHT",
            "COLOR_GREY_DARK"
		}));
		
		ConfigGroup gr_context = new ConfigGroup("Context");
		gr_system.addKeys(Arrays.asList(new String[]{
	        "CONTEXT_NAME",
	        "CONTEXT_IMAGE",
	        "CONTEXT_IMAGE_WIDTH",
	        "CONTEXT_MAIL",
	        "CONTEXT_URL",
	        "INTRO_TEXT",
	        "ALLOW_BACK_FROM_INTRODUCTION",
            "ALLOW_PROBLEM_LEAVE",
            "ALLOW_STRG_Y_JUMPOUT",
	        "LOGIN_AUTO",
	        "LOGOUT_ALLOWED",
	        "AFTER_LOGIN",
	        "AFTER_ENDPROBLEM",
            "ALLOW_CASE_REPLAY",
            "USE_EXTENDED_RESULT"
		}));
		
		ConfigGroup gr_avatar = new ConfigGroup("Avatar");
		gr_avatar.addKeys(Arrays.asList(new String[]{
			"USE_INSTRUCTOR",
			"INSTRUCTOR_USEAGE",
			"AVATAR_CONTENT_WIDTH",
			"AVATAR_CONTENT_HEIGHT",
			"HELP_WIDTH",
			"HELP_HEIGHT",
			"INTRO_HELP_IS_URL",
			"INTRO_HELP_CONTENT",
			"INTRO_TOP_TO_BOTTOM_RATIO",
			"INTRO_SHOW_COURSE_LINK",
			"INTRO_COURSE_LINK",
			"GIVE_AWAY_SOLUTIONS_ON_FEEDBACK",
            "ALLOW_EXTENDED_FEEDBACK",
            "HINTS_ON_FEEDBACK",
			"SHOW_HINTS_FOR_DIAGNOSES",
            "SHOW_HINTS_FOR_THERAPIES",
			"SHOW_EXTENDED_HINTS_FOR_DIAGNOSES",
			"SHOW_EXTENDED_HINTS_FOR_THERAPIES",
			"HINTS_FOR_DIAGNOSES_VERTICAL_MODE",
			"HINTS_FOR_THERAPIES_VERTICAL_MODE",
            "ACTIVATE_GUIUTTERINGS",
            "ALLOW_GUIUTTERING_RATINGS",
            "GUIUTTERING_RATINGS",
            "GUIUTTERING_BLOCKS",
            "GUIUTTERING_BLOCK_RESET",
            "SHOW_INTRO_HELP",
            "SHOW_SESSION_HELP",
            "PB_HOLD_FEEDBACK_UNTIL_ALL_ANSWERED",
            "PB_AUTOFEEDBACK",
            
            "UTTERING_FOR_POQS",
            "UTTERING_FOR_FILTERED_TESTS",
            "UTTERING_FOR_DONE_NEQUESTIONS",
            "UTTERING_FOR_DONE_SIMPLEQUESTIONS",
            "UTTERINGBLOCK_FOR_MISSING_NEQUESTIONS",
            "UTTERINGBLOCK_FOR_MISSING_SIMPLEQUESTIONS",
            "UTTERINGBLOCK_FOR_MISSING_DIAGNOSES",
            "UTTERINGBLOCK_FOR_MISSING_THERAPIES",
            "UTTERING_FOR_NEW_PB_MODE"
		}));

		ConfigGroup gr_basics = new ConfigGroup("Basics");
		gr_basics.addKeys(Arrays.asList(new String[] {
			"SHOW_CLOCK",
            "PAGEBASED_MODE",
            "PB_NEQUESTIONS_ONLY",
            "PB_AUTOSYNCHRONIZE",
            "PB_OBSCURE_COMING_TASKS",
            "PB_SUPRESS_FIRST_EXTRA_WINDOW_TASK_IN_NEW_ELEMENT",
			"NO_START_COMMENT",
			"NO_END_COMMENT",
			"SOLUTIONS_THRESHOLD",
			"ADD_SOLUTIONS_AS_ESTABLISHED",
			"ADD_INTERIM_SOLUTIONS_AS_ESTABLISHED",
			"SHOW_DIAGNOSES",
			"SHOW_THERAPIES",
			"SHOW_ONLY_ESTABLISHED_THERAPIES",
			"BLOCK_THERAPIES_ON_GEFUEHRT_UNTIL_END",
            "SHOW_INTERIMSOLUTIONS",
			"SESSION_TYPE_FREI",
			"SESSION_TYPE_GEFUEHRT",
            "SESSION_TYPE_SEMI",
			"ADDITIONAL_TEXT_DIR",
			"HIDE_DIALOGNEXT_IF_ALL_TESTS_ARE_STARTTEST",
			"SHOW_DIAGJUSTIFICATIONS",
			"SIMPLE_DIAGJUSTIFICATIONS",
			"SHOW_SIMPLEQUESTIONS",
            "ALLOW_USER_TO_CHANGE_DIAGNOSES_AFTER_SECTION",
			"SHOW_ENDQUESTIONS",
			"CHOOSE_RANDOM_FOLLOWUP_SESSION",
			"ATTEND_POQS",
            "BLOCK_END_UNTIL_ALL_ENDQUESTIONS_ARE_ANSWERED",
            "DIAGNOSES_LONG_MENU_MODE",
            "THERAPIES_LONG_MENU_MODE",
            "EXAMINATIONS_LONG_MENU_MODE",
            "FUP_DIAGNOSES_LONG_MENU_MODE",
            "FUP_THERAPIES_LONG_MENU_MODE"
		}));

		ConfigGroup gr_search = new ConfigGroup("Search");
		gr_search.addKeys(Arrays.asList(new String[]{
			"ENABLE_SYNONYM_SEARCH_ON_EXAMINATIONS",
			"ENABLE_SYNONYM_SEARCH_ON_DIAGNOSES",
			"ENABLE_SYNONYM_SEARCH_ON_THERAPIES",
			"SHOW_GOOGLE_ON_EXAMINATIONS",
			"SHOW_GOOGLE_ON_DIAGNOSES",
			"SHOW_GOOGLE_ON_THERAPIES",
			"SHOW_SEARCH_GOOGLE",
			"SHOW_SEARCH_HEALTHFINDER",
			"SHOW_SEARCH_MEDIWARP",
			"ADDITIONAL_LIBRARY"
		}));
		
		ConfigGroup gr_trees = new ConfigGroup("Trees");
		gr_trees.addKeys(Arrays.asList(new String[]{
			"LINK_ON_ALL_TESTS",
			"CHECKBOXES_FOR_TESTS",
			"SHOW_MMINFO_IZONE",
			"SHOW_MMINFO_LINK",
			"SHOW_MMINFO_INFO",
			"SHOW_MMINFO_THERAPY",
			"PREOPENED_THERAPIES",
			"PREOPENED_DIAGNOSES",
			"PREOPENED_EXAMINATIONS",
			"SCROLL_ON_OPENTREE",
			"SCROLL_ON_FLIPNODE",
			"NOTICE_SELECTED_EXAMINATIONS_AT_FLIPNODE",
			"SCROLL_EXAMINATIONS",
			"SCROLL_DIAGNOSES",
			"SCROLL_DIAGNOSES_STATES",
			"SCROLL_THERAPIES",
			"SCROLL_THERAPIES_STATES",
			"SHOW_INFOBUTTONS_IN_PRINTTREE",
			"LINKS_IN_PRINTTREE"
		}));

		ConfigGroup gr_results = new ConfigGroup("Results");
		gr_results.addKeys(Arrays.asList(new String[]{
			"NO_ANSWERS",
			"NO_ANSWERS_PL",
			"NULL_ANSWER",
			"NOT_YET_ANSWERED",
			"NON_PATHOLOGIC_VALUE_TEXT",
			"NO_RESULTS",
			"USE_PROMPTS_FOR_QUESTIONS",
			"USE_PROMPTS_FOR_CONTAINERS",
			"SHOW_CONVERTED_UNITS",
			"RESULTS_SHOW_PROMPT",
			"SHOW_FILTERS",
			"DEFAULT_FILTER_NAMES",
			"FILTER_NO_CHILDREN_TEXT",
			"SCROLL_RESULTS",
			"ALL_EXAMINES_FIRST_SUBHIERARCHY_LEVEL",
            "INTRO_TAB_TEXT",
            "INTRO_TAB_WIDTH",
            "RESULTS_PANE_TITLE",
			"SLIDER_WIDTH",
			"SLIDER_NUMBER",
            "SLIDER_NUMBER_DYNAMIC",
            "SLIDER_ONE_VISIBLE",
            "SLIDER_SHOW_NAVIGATION",
            "SLIDER_ENUMERATE",
            "SLIDER_ALIGNMENT",
			"ASSUME_MEANINGFUL_ABNORMALITIES",
			"FUS_TEXT"
		}));
		
		ConfigGroup gr_costs = new ConfigGroup("Costs");
		gr_costs.addKeys(Arrays.asList(new String[]{
			"SHOW_COSTS",
			"COST_ALLOWED",
			"TIMEEXPENDITURE_ALLOWED",
			"RISK_ALLOWED",
			"PICS_4_COSTBARS",
			"ICONS_4_COSTS",
			"BUTTONS_4_COSTS",
			"COST_PANE_SPACE",
			"COST_PANE_SPACER_WIDTH",
			"COST_PANE_BARS_WIDTH"
		}));

		ConfigGroup gr_findings = new ConfigGroup("Findings");
		gr_findings.addKeys(Arrays.asList(new String[]{
			"SHOW_STARTITEM_WITH_STARTINFO",
			"STRETCH_INTRO_PICTURE",
			"TEXT_THUMB_URL",
			"MMITEMS_SHOWTYPE_FOR_GEFUEHRT",
			"MMITEMS_SHOWTYPE_FOR_FREI",
			"SHOW_STARTITEM_WITH_ITEMS",
			"TREAT_FEATURES_AS_OBSERVED",
			"MM_SHOW_ITEMS_OF_PAST_BLOCKS",
			"MM_FINDINGS",
			"MM_FINDINGS_AUTOCHOOSE_QUESTION",
			"MM_FINDINGS_AUTOCHOOSE_ITEM",
			"MM_FINDINGS_AUTOJUMP",
			"MM_FINDINGS_GIVE_ITEMS_HINT",
			"MM_FINDINGS_AUTOFEEDBACK",
			"MM_FINDINGS_SHOW_QUESTIONPROMPT",
			"QUESTION_SHOW_PROMPT",
			"ACTION_ON_THUMBS_IS_EXTRA",
			"SHOW_ARROWS_BELOW_THUMBS_ANYWAY",
			"THUMBS_MAY_HAVE_ANY_SIZE",
			"FINDINGS_WINDOW_WEIGHT_FOR_BOLD",
			"SHOW_CLOSEBUTTON_IN_FINDINGS_WINDOW",
			"RENDER_FINDINGSQUESTION_ALWAYS",
            "FINDINGS_SINGLEMODE"
		}));

		ConfigGroup gr_viewitems = new ConfigGroup("ViewItems");
		gr_viewitems.addKeys(Arrays.asList(new String[]{
	        "SHOW_VIEWITEMS",
            "VIEWITEMS_SINGLEMODE"
		}));
		
		ConfigGroup gr_simplequestions = new ConfigGroup("SimpleQuestions");
		gr_simplequestions.addKeys(Arrays.asList(new String[]{
	        "MM_SIMPLEQUESTIONS",
	        "MM_SIMPLEQUESTIONS_HIDE_UNTIL_ALL_MM_AVAILABLE",
	        "MM_SIMPLEQUESTIONS_AUTOCHOOSE_QUESTION",
	        "MM_SIMPLEQUESTIONS_AUTOJUMP",
	        "MM_SIMPLEQUESTIONS_GIVE_ITEMS_HINT",
	        "MM_SIMPLEQUESTIONS_AUTOCHOOSE_ITEM",
            "MM_SIMPLEQUESTIONS_AUTOFEEDBACK",
            "MM_SIMPLEQUESTIONS_SINGLEMODE",
            "SIMPLEQUESTIONS_NULL_TEXT",
            "SIMPLEQUESTIONS_FEEDBACK_RIGHT_WRONG",
            "SIMPLEQUESTIONS_FEEDBACK_ALL_ANSWERALTERNATIVES",
            "SIMPLEQUESTIONS_FEEDBACK_NO_DETAILS",
            "SIMPLEQUESTIONS_ONE_SHOT",
            "QUESTIONS_SHOW_NUMBER_OF_CORRECT",
            "QUESTIONS_OC_IF_ONLY_ONE_CORRECT",
            "NOWEIGHT_TEXT",
            "SHOW_EXPLANATION_OF_NOWEIGHT_QUESTIONS",
            "SORT_WEIGHTLESS_QUESTIONS_CRITIQUE_TO_BACK"
		}));
		
		ConfigGroup gr_findingsloc = new ConfigGroup("Findings-Localisation");
		gr_findingsloc.addKeys(Arrays.asList(new String[]{
	        "SHOW_LOCALISATION",
			"SHOW_ALL_FEATUREGROUPS",
			"SCOREMETHOD",
			"TOO_MUCH",
			"TOO_LESS"
		}));
		
		ConfigGroup gr_plugins = new ConfigGroup("Plugins");
        gr_plugins.addKeys(Arrays.asList(new String[]{
			"PLUGIN_CONFIG",
            "LINELENGTH_IN_LABORDERRENDERER",
            "LABTABLERENDERER_FILTERVALUELESS",
			"ANSWER_POSITION",
			"FONT_SIZE_IN_CONTENT",
			"FONT_FAMILY_IN_CONTENT",
			"FONT_SIZE_IN_CONTENT_HEADERS",
			"FONT_WEIGHT_IN_CONTENT_HEADERS",
			"ANSWERGRAPHICS_DATA",
            "QUESTIONS_ONELINER_LIST",
            "ANSWERS_RENDERER_NOBREAK",
            "NODE_CONTENT_SUBSUMES_CHILD_CONTENT",
            "RENDER_TQCONTAINERS_AS_CONTINUOUS_TEXT"
		}));
		
		ConfigGroup gr_questionary = new ConfigGroup("Questionary");
		gr_questionary.addKeys(Arrays.asList(new String[]{
			"QUESTIONARY_TYPE",
			"QUESTIONARY_FHH_NAME",
			"QUESTIONARY_FHH_URL",
			"QUESTIONARY_FHH_AUTOGENERATE_PER_CASE",
			"QUESTIONARY_FHH_AUTOGENERATE_PER_CASE_BASE",
			"QUESTIONARY_TRAIN_SYSTEMURL_AFTER_1_CASE",
			"QUESTIONARY_TRAIN_SYSTEMURL_AFTER_5_CASES",
			"QUESTIONARY_TRAIN_SYSTEMURL",
			"QUESTIONARY_TRAIN_CASEURL"
		}));
		
		ConfigGroup gr_adaptiveHelp = new ConfigGroup("AdaptiveHelp");
		gr_questionary.addKeys(Arrays.asList(new String[]{
	        "USE_ADAPTIVE_HELP",
			"ADAPTIVEHELP_RULESET_FILENAME",
			"ADAPTIVEHELP_OVERLAYMODEL_FILENAME",
			"ADAPTIVEHELP_ICC_FILENAME",
			"ADAPTIVEHELP_INTERVENTIONLIBRARY_FILENAME",
			"ADAPTIVEHELP_OVERLAYMODEL_SAVINGPERIOD"
		}));
		
		TRAIN.addGroup(gr_system);
		TRAIN.addGroup(gr_layout);
		TRAIN.addGroup(gr_context);
		TRAIN.addGroup(gr_basics);
		TRAIN.addGroup(gr_search);
		TRAIN.addGroup(gr_avatar);
		TRAIN.addGroup(gr_trees);
		TRAIN.addGroup(gr_results);
		TRAIN.addGroup(gr_costs);
		TRAIN.addGroup(gr_findings);
		TRAIN.addGroup(gr_viewitems);
		TRAIN.addGroup(gr_simplequestions);
		TRAIN.addGroup(gr_findingsloc);
		TRAIN.addGroup(gr_plugins);
		TRAIN.addGroup(gr_questionary);
		TRAIN.addGroup(gr_adaptiveHelp);
		
		
		ConfigGroup gr_dialog_system = new ConfigGroup("dialog.System");
		gr_dialog_system.addKeys(Arrays.asList(new String[]{
			"dialog_REPORT_DIR"
		}));
		
		ConfigGroup gr_dialog_view = new ConfigGroup("dialog.View");
		gr_dialog_view.addKeys(Arrays.asList(new String[]{
			"dialog_DIALOGMODE",
			"dialog_MQDIALOG_TYPE",
			"dialog_OQDIALOG_TYPE",
			"dialog_SHOW_CONTAINER_AS_HEADER",
			"dialog_AVOID_POPUP_WINDOWS",
			"dialog_QASET_TREE",
			"dialog_QASET_TREE_ROOT",
			"dialog_QCONTAINER_OVERVIEW_SLIDERS_ENABLED",
			"dialog_DIAGNOSIS_TREE",
			"dialog_DIAGNOSIS_TREE_ROOT",
			"dialog_HIERARCHYPAGE_ENABLE_SEARCH",
			"dialog_HIERARCHYPAGE_ENABLE_OLAP",			
			"dialog_SEARCH_MAX_SOLUTION_COUNT"
		}));
		
		ConfigGroup gr_dialog_resultpage = new ConfigGroup("dialog.Resultpage");
		gr_dialog_resultpage.addKeys(Arrays.asList(new String[]{
			"dialog_RESULTPAGE_SHOW_MESSAGE_ONLY",
			"dialog_RESULTPAGE_SHOW_SAVE_DETAILED_BUTTON",
			"dialog_RESULTPAGE_SHOW_SAVE_QUICK_BUTTON",
			"dialog_RESULTPAGE_SAVE_QUICK_BUTTON_DIAG_CHECKBOX",
			"dialog_RESULTPAGE_SHOW_NEW_CASE_BUTTON",
			"dialog_RESULTPAGE_QUESTIONS_OVERVIEW",
			"dialog_RESULTPAGE_SHOW_ESTABLISHED_RESULTS",
			"dialog_RESULTPAGE_SHOW_SUGGESTED_RESULTS",
			"dialog_RESULTPAGE_SHOW_RISK_DIAGNOSES",
			"dialog_RESULTPAGE_FILTER_UNSPECIFIC_DIAGNOSES",
			"dialog_RESULTPAGE_SHOW_DIAGSCORES"
		}));
		
		ConfigGroup gr_dialog_solutionpage = new ConfigGroup("dialog.Solutionpage");
		gr_dialog_solutionpage.addKeys(Arrays.asList(new String[]{
			"dialog_SOLUTIONPAGE_SHOW_ESTABLISHED_RESULTS",
			"dialog_SOLUTIONPAGE_SHOW_SUGGESTED_RESULTS",
			"dialog_SOLUTIONPAGE_SHOW_RISK_DIAGNOSES",
			"dialog_SOLUTIONPAGE_FILTER_UNSPECIFIC_DIAGNOSES",
			"dialog_SOLUTIONPAGE_ENABLE_COMPARECASE",
			"dialog_SOLUTIONPAGE_ENABLE_SETCOVERING"
		}));
		
		ConfigGroup gr_dialog_explain = new ConfigGroup("dialog.ExplainingComponent");
		gr_dialog_explain.addKeys(Arrays.asList(new String[]{
			"dialog_EXPLAIN_REASON",
			"dialog_EXPLAIN_CONCRETE_DERIVATION",
			"dialog_EXPLAIN_DERIVATION",
			"dialog_EXPLAIN_SHOW_IDS",
		}));
		
		ConfigGroup gr_dialog_correct_diags_page = new ConfigGroup("dialog.CorrectDiagsPage");
		gr_dialog_correct_diags_page.addKeys(Arrays.asList(new String[]{
			"dialog_CORRECT_DIAGS_PAGE_SHOW_SAVEBUTTON",
			"dialog_CORRECT_DIAGS_PAGE_SHOW_GENERATE_REPORT_BUTTON",
			"dialog_CORRECT_DIAGS_PAGE_SHOW_GENERATE_REPORT_CHECKBOX",
			"dialog_CORRECT_DIAGS_PAGE_DEFAULT_GENERATE_REPORT"
		}));
		
		
		ConfigGroup gr_dialog_oqdc = new ConfigGroup("dialog.OneQuestionDialog");
		gr_dialog_oqdc.addKeys(Arrays.asList(new String[]{
			"dialog_OQDC_SHOW_QUESTIONINFO"
		}));
		
		ConfigGroup gr_dialog_mqdc = new ConfigGroup("dialog.MultipleQuestionDialogs");
		gr_dialog_mqdc.addKeys(Arrays.asList(new String[]{
			"dialog_MQDIALOGS_GLOBAL_OK_BUTTON",
			"dialog_MQDIALOGS_GLOBAL_RESULT_BUTTON",
			"dialog_MQDIALOGS_GLOBAL_UNKNOWN_BUTTON",
			"dialog_MQDIALOGS_GLOBAL_INACTIVE_QUESTIONS_BUTTON",
			"dialog_MQDIALOGS_SHOW_INACTIVE_FOLLOWQUESTIONS",
			"dialog_MQDIALOGS_SHOW_INACTIVE_HIERARCHICAL_FOLLOWQUESTIONS_ONLY"
		}));

		ConfigGroup gr_dialog_mqdialog = new ConfigGroup("dialog.MultipleQuestionDialogs.MQDialog");
		gr_dialog_mqdialog.addKeys(Arrays.asList(new String[]{
			"dialog_MQDIALOG_STYLE",
			"dialog_MQDIALOG_BUTTON_FOR_EVERY_QUESTION",
			"dialog_MQDIALOG_STD_ANSWERS_SEPARATED",
			"dialog_MQDIALOG_STD_ANSWERS_SEPARATED_TEXTFIELD_EXCEPTION",
			"dialog_MQDIALOG_FONTSIZE",
			"dialog_MQDIALOG_Q_DEFAULT_RETRIEVAL_TYPE",
			"dialog_MQDIALOG_C_DEFAULT_HEADER_LENGTH_IN_CHARS",
			"dialog_MQDIALOG_C_DEFAULT_COLUMN_WIDTH_IN_CHARS",
			"dialog_MQDIALOG_C_DEFAULT_VERTICAL_QUESTION_SPACE"
		}));
		
		ConfigGroup gr_dialog_mqdialogboxed = new ConfigGroup("dialog.MultipleQuestionDialogs.MQDialogBoxed");
		gr_dialog_mqdialogboxed.addKeys(Arrays.asList(new String[]{
			"dialog_MQDIALOGBOXED_CONTROL_MODE",
			"dialog_MQDIALOGBOXED_FOLLOWQ_INDICATOR",
			"dialog_MQDIALOGBOXED_NUMBER_ANSWERS",
			"dialog_MQDIALOGBOXED_QUESTION_WORD_WRAP",
			"dialog_MQDIALOGBOXED_ANSWER_WORD_WRAP",
			"dialog_MQDIALOGBOXED_BUTTON_FOR_EVERY_QUESTION",
			"dialog_MQDIALOGBOXED_FONTSIZE",
			"dialog_MQDIALOGBOXED_CELLBORDER",
			"dialog_MQDIALOGBOXED_CELLPADDING",
			"dialog_MQDIALOGBOXED_DEFAULT_MINCHARCOUNT_PER_COL",
			"dialog_MQDIALOGBOXED_DEFAULT_MAXANSWERCOUNT_PER_QCOL",
			"dialog_MQDIALOGBOXED_DEFAULT_MAXCOLCOUNT",
			"dialog_MQDIALOGBOXED_DEFAULT_QOC_RETRIEVAL_TYPE",
			"dialog_MQDIALOGBOXED_QNUM_UNKNOWN_BELOW",
			"dialog_MQDIALOGBOXED_SHOW_USER_DEFINED_WEIGHTS"
		}));
		
		ConfigGroup gr_dialog_case_file_archiver = new ConfigGroup("dialog.CaseFileArchiver");
		gr_dialog_case_file_archiver.addKeys(Arrays.asList(new String[]{
			"dialog_CASE_FILE_ARCHIVER_ENABLED",
			"dialog_CASE_FILE_ARCHIVER_DATES"
		}));
		
		ConfigGroup gr_dialog_medical_plugin = new ConfigGroup("dialog.MedicalPlugin");
		gr_dialog_medical_plugin.addKeys(Arrays.asList(new String[]{
			"dialog_MEDICAL_PLUGIN_RATING_QUESTION_ID",
			"dialog_MEDICAL_PLUGIN_START_ANSWER_ID",
			"dialog_MEDICAL_PLUGIN_ANONYMIZER_VALUES",
			"dialog_MEDICAL_PLUGIN_ADMINISTRATION_ENABLED",
			"dialog_MEDICAL_PLUGIN_DIAGNOSES_CHECK_ENABLED"
		}));
		
		ConfigGroup gr_dialog_diagclarification_plugin = new ConfigGroup("dialog.DiagClarificationPlugin");
		gr_dialog_diagclarification_plugin.addKeys(Arrays.asList(new String[]{
			"dialog_DIAG_CLARIFICATION_PLUGIN_SHOW_BUTTON_FOR_DIAGS",
			"dialog_DIAG_CLARIFICATION_PLUGIN_SHOW_COMPLETE_QUESTIONS",
			"dialog_DIAG_CLARIFICATION_PLUGIN_DIAG_ID"
		}));
		
		ConfigGroup gr_dialog_setcovering_plugin = new ConfigGroup("dialog.SetCoveringPlugin");
		gr_dialog_setcovering_plugin.addKeys(Arrays.asList(new String[]{
			"dialog_SET_COVERING_PLUGIN_USE_D3_METHOD"
		}));
		
				
		DIALOG.addGroup(gr_dialog_system);
		DIALOG.addGroup(gr_dialog_view);
		DIALOG.addGroup(gr_dialog_resultpage);
		DIALOG.addGroup(gr_dialog_solutionpage);
		DIALOG.addGroup(gr_dialog_correct_diags_page);
		DIALOG.addGroup(gr_dialog_explain);
		DIALOG.addGroup(gr_dialog_oqdc);
		DIALOG.addGroup(gr_dialog_mqdc);
		DIALOG.addGroup(gr_dialog_mqdialog);
		DIALOG.addGroup(gr_dialog_mqdialogboxed);
		DIALOG.addGroup(gr_dialog_case_file_archiver);
		DIALOG.addGroup(gr_dialog_medical_plugin);
		DIALOG.addGroup(gr_dialog_diagclarification_plugin);
		DIALOG.addGroup(gr_dialog_setcovering_plugin);
		
	}
	
	private String name;
	private List<ConfigGroup> groups = new LinkedList<ConfigGroup>();
	
	private ConfigType() { /* hide empty constructor */ }
	public ConfigType(String name) { this.name = name; }

	public List<ConfigGroup> getGroups() { return Collections.unmodifiableList(this.groups); }
	public String getName() { return this.name; }
	
	public void addGroup(ConfigGroup group) { this.groups.add(group); }
	
}
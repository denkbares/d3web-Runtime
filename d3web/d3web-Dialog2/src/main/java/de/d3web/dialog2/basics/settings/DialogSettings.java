package de.d3web.dialog2.basics.settings;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.d3web.dialog2.util.DialogUtils;

public class DialogSettings {

    public static Logger logger = Logger.getLogger(DialogSettings.class);

    public static final String SETTINGS_FILENAME = "dialogsettings.xml";

    public static final String STYLESHEET_FILENAME = "dialog.css";
    public static final String STYLESHEET_PATH = "css/";

    public static final String QUESTIONDATESECTION_DATE = "date";
    public static final String QUESTIONDATESECTION_TIME = "time";
    public static final String QUESTIONDATESECTION_FULL = "full";

    public static final String DIALOGMODE_MQ = "MQ";
    public static final String DIALOGMODE_OQ = "OQ";

    public static final String PROCESSEDQTEXTMODE_TEXT = "text";
    public static final String PROCESSEDQTEXTMODE_PROMPT = "prompt";

    public static final boolean INCLUDING_INACTIVE_QUESTIONS = true;
    public static final boolean INACTIVE_HIERARCHICAL_CHILDREN_ONLY = false;

    private String styleSheetPath = STYLESHEET_PATH + STYLESHEET_FILENAME;

    private float leftPanelSizeActual = 20.0f;

    private float rightPanelSizeActual = 20.0f;

    private boolean showRightPanel = true;

    private boolean kbHasCSSFile = false;

    // Elements of Dialog
    private float leftPanelSize = 20.0f;
    private float rightPanelSize = 20.0f;
    private boolean leftPanelFixed = true;
    private boolean showProgressBar = true;

    private boolean allowRightPanel = true;

    private boolean showRightPanelToggleButtons = true;

    private long saveCaseThreadMaxIdleTime = 0;

    private int sessionMaxInactiveInterval = 7200; // after 2 hours the session
    // will expire
    private boolean showManagementButton = true;
    private boolean showDialogButton = true;
    private String timeZone = "Europe/Berlin";
    private boolean showCountryFlags = true;
    private boolean showPageHeader = true;
    private boolean showPageFooter = true;
    // Elements of QuestionPage
    private boolean autoMoveToResultpage = true;
    private boolean showQuestionPageAnswerButton = true;
    private boolean showQuestionPageResultButton = true;

    private boolean showQuestionPageUnknownButton = true;
    private int questionMinAnsWrap = 6;
    private String questionDateDefaultdateSection = QUESTIONDATESECTION_FULL;
    private String dialogMode = DIALOGMODE_MQ;
    private boolean mCConstraintsAutoGrayOut = true;
    private boolean showQASetTreeTab = true;
    private boolean showDiagnosesTreeTab = true;
    private boolean showAbstractQuestions = true;
    private boolean showAbstractQuestionsInResultPage = true;
    public boolean isShowAbstractQuestionsInResultPage() {
		return showAbstractQuestionsInResultPage;
	}

	public void setShowAbstractQuestionsInResultPage(boolean showAbstractQuestionsInResultPage) {
		this.showAbstractQuestionsInResultPage = showAbstractQuestionsInResultPage;
	}

	// Elements of Diagnoses
    private boolean showHeuristicDiagnoses = true;
    private boolean showHeuristicEstablishedDiagnoses = true;
    private boolean showHeuristicSuggestedDiagnoses = true;

    private boolean showHeuristicExcludedDiagnoses = true;
    // Elements of Explanation
    private boolean showDiagExplanation = true;
    private boolean showDiagReason = true;
    private boolean showDiagConcreteDerivation = true;

    private boolean showDiagDerivation = true;
    // Elements of MMInfo
    private boolean showMMInfo = true;
    private int maxCharLengthInMMInfoPopup = 50;
    // Elements of CompareCase
    private boolean showCompareCase = true;

    // Elements of SCM
    private boolean showSCM = true;
    private float scm_display_min_percentage = 0.15f;

    private int scm_digitcount = 2;

    // Elements of ProcessedQContainers
    private boolean showProcessedQContainers = true;
    private boolean processedShowAll = false;
    private boolean processedShowUnknown = false;

    private int processedMaxInput = 8;
    private String processedQTextMode = PROCESSEDQTEXTMODE_TEXT;
    private boolean processedShowQContainerNames = true;
    private boolean processedShowUnknownIcon = true;
    private boolean processedShowQContainerNamesIcon = true;
    // Elements of Frequentness
    private boolean showFrequentness = true;

    public DialogSettings() {
	// to set the config path
	DialogUtils.init();
	init(null);
    }

    public String getDialogMode() {
	return dialogMode;
    }

    public float getLeftPanelSize() {
	return leftPanelSize;
    }

    public float getLeftPanelSizeActual() {
	return leftPanelSizeActual;
    }

    public String getLeftWidth() {
	return "width: " + (int) leftPanelSizeActual + "%;";
    }

    public String getMarginLeftIfLeftPanelFixedCSS() {
	if (leftPanelFixed)
	    return "margin-left: " + (int) leftPanelSizeActual + "%;";
	return "";
    }

    public int getMaxCharLengthInMMInfoPopup() {
	return maxCharLengthInMMInfoPopup;
    }

    public String getMiddleWidth() {
	return "width: " + (100 - leftPanelSizeActual - rightPanelSizeActual)
		+ "%;";
    }

    public String getPositionFixedCSS() {
	if (leftPanelFixed)
	    return "leftPanelFixed";
	return "";
    }

    public int getProcessedMaxInput() {
	return processedMaxInput;
    }

    public String getProcessedQTextMode() {
	return processedQTextMode;
    }

    public String getQuestionDateDefaultdateSection() {
	return questionDateDefaultdateSection;
    }

    public int getQuestionMinAnsWrap() {
	return questionMinAnsWrap;
    }

    public float getRightPanelSize() {
	return rightPanelSize;
    }

    public float getRightPanelSizeActual() {
	return rightPanelSizeActual;
    }

    public String getRightWidth() {
	return "width: " + (int) rightPanelSizeActual + "%;";
    }

    public long getSaveCaseThreadMaxIdleTime() {
	return saveCaseThreadMaxIdleTime;
    }

    public int getScm_digitcount() {
	return scm_digitcount;
    }

    public float getScm_display_min_percentage() {
	return scm_display_min_percentage;
    }

    public int getSessionMaxInactiveInterval() {
	return sessionMaxInactiveInterval;
    }

    public String getStyleSheetPath() {
	return styleSheetPath;
    }

    public String getTimeZone() {
	return timeZone;
    }

    public void init(String kbid) {
	// load settings from file
	new DialogSettingsLoader(this).init(kbid);
	// set the (new) actual panel widths
	setLeftPanelSizeActual(getLeftPanelSize());
	setRightPanelSizeActual(getRightPanelSize());
	// update HTTP-session
	HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
		.getExternalContext().getSession(false);
	session.setMaxInactiveInterval(sessionMaxInactiveInterval);
	logger.info("Session expiration updated. It will expire in "
		+ session.getMaxInactiveInterval() + " seconds.");
    }

    public boolean isAllowRightPanel() {
	return allowRightPanel;
    }

    public boolean isAutoMoveToResultpage() {
	return autoMoveToResultpage;
    }

    public boolean isKbHasCSSFile() {
	return kbHasCSSFile;
    }

    public boolean isLeftPanelFixed() {
	return leftPanelFixed;
    }

    public boolean isMCConstraintsAutoGrayOut() {
	return mCConstraintsAutoGrayOut;
    }

    public boolean isProcessedShowAll() {
	return processedShowAll;
    }

    public boolean isProcessedShowQContainerNames() {
	return processedShowQContainerNames;
    }

    public boolean isProcessedShowQContainerNamesIcon() {
	return processedShowQContainerNamesIcon;
    }

    public boolean isProcessedShowUnknown() {
	return processedShowUnknown;
    }

    public boolean isProcessedShowUnknownIcon() {
	return processedShowUnknownIcon;
    }

    public boolean isShowAbstractQuestions() {
	return showAbstractQuestions;
    }

    public boolean isShowCompareCase() {
	return showCompareCase;
    }

    public boolean isShowCountryFlags() {
	return showCountryFlags;
    }

    public boolean isShowDiagConcreteDerivation() {
	return showDiagConcreteDerivation;
    }

    public boolean isShowDiagDerivation() {
	return showDiagDerivation;
    }

    public boolean isShowDiagExplanation() {
	return showDiagExplanation;
    }

    public boolean isShowDiagnosesTreeTab() {
	return showDiagnosesTreeTab;
    }

    public boolean isShowDiagReason() {
	return showDiagReason;
    }

    public boolean isShowDialogButton() {
	return showDialogButton;
    }

    public boolean isShowFrequentness() {
	return showFrequentness;
    }

    public boolean isShowHeuristicDiagnoses() {
	return showHeuristicDiagnoses;
    }

    public boolean isShowHeuristicEstablishedDiagnoses() {
	return showHeuristicEstablishedDiagnoses;
    }

    public boolean isShowHeuristicExcludedDiagnoses() {
	return showHeuristicExcludedDiagnoses;
    }

    public boolean isShowHeuristicSuggestedDiagnoses() {
	return showHeuristicSuggestedDiagnoses;
    }

    public boolean isShowManagementButton() {
	return showManagementButton;
    }

    public boolean isShowMMInfo() {
	return showMMInfo;
    }

    public boolean isShowPageFooter() {
	return showPageFooter;
    }

    public boolean isShowPageHeader() {
	return showPageHeader;
    }

    public boolean isShowProcessedQContainers() {
	return showProcessedQContainers;
    }

    public boolean isShowProgressBar() {
	return showProgressBar;
    }

    public boolean isShowQASetTreeTab() {
	return showQASetTreeTab;
    }

    public boolean isShowQuestionPageAnswerButton() {
	return showQuestionPageAnswerButton;
    }

    public boolean isShowQuestionPageResultButton() {
	return showQuestionPageResultButton;
    }

    public boolean isShowQuestionPageUnknownButton() {
	return showQuestionPageUnknownButton;
    }

    public boolean isShowRightPanel() {
	return showRightPanel;
    }

    public boolean isShowRightPanelToggleButtons() {
	return showRightPanelToggleButtons;
    }

    public boolean isShowSCM() {
	return showSCM;
    }

    public void setAllowRightPanel(boolean allowRightPanel) {
	this.allowRightPanel = allowRightPanel;
    }

    public void setAutoMoveToResultpage(boolean autoMoveToResultpage) {
	this.autoMoveToResultpage = autoMoveToResultpage;
    }

    public void setDialogMode(String dialogMode) {
	this.dialogMode = dialogMode;
    }

    public void setKbHasCSSFile(boolean kbHasCSSFile) {
	this.kbHasCSSFile = kbHasCSSFile;
    }

    public void setLeftPanelFixed(boolean leftPanelFixed) {
	this.leftPanelFixed = leftPanelFixed;
    }

    public void setLeftPanelSize(float leftPanelSize) {
	this.leftPanelSize = leftPanelSize;
    }

    public void setLeftPanelSizeActual(float leftPanelSizeActual) {
	this.leftPanelSizeActual = leftPanelSizeActual;
    }

    public void setMaxCharLengthInMMInfoPopup(int maxCharLengthInMMInfoPopup) {
	this.maxCharLengthInMMInfoPopup = maxCharLengthInMMInfoPopup;
    }

    public void setMCConstraintsAutoGrayOut(boolean constraintsAutoGrayOut) {
	mCConstraintsAutoGrayOut = constraintsAutoGrayOut;
    }

    public void setProcessedMaxInput(int processedMaxInput) {
	this.processedMaxInput = processedMaxInput;
    }

    public void setProcessedQTextMode(String processedQTextMode) {
	this.processedQTextMode = processedQTextMode;
    }

    public void setProcessedShowAll(boolean processedShowAll) {
	this.processedShowAll = processedShowAll;
    }

    public void setProcessedShowQContainerNames(
	    boolean processedShowQContainerNames) {
	this.processedShowQContainerNames = processedShowQContainerNames;
    }

    public void setProcessedShowQContainerNamesIcon(
	    boolean processedShowQContainerNamesIcon) {
	this.processedShowQContainerNamesIcon = processedShowQContainerNamesIcon;
    }

    public void setProcessedShowUnknown(boolean processedShowUnknown) {
	this.processedShowUnknown = processedShowUnknown;
    }

    public void setProcessedShowUnknownIcon(boolean processedShowUnknownIcon) {
	this.processedShowUnknownIcon = processedShowUnknownIcon;
    }

    public void setQuestionDateDefaultdateSection(
	    String questionDateDefaultdateSection) {
	this.questionDateDefaultdateSection = questionDateDefaultdateSection;
    }

    public void setQuestionMinAnsWrap(int questionMinAnsWrap) {
	this.questionMinAnsWrap = questionMinAnsWrap;
    }

    public void setRightPanelSize(float rightPanelSize) {
	this.rightPanelSize = rightPanelSize;
    }

    public void setRightPanelSizeActual(float newSize) {
	this.rightPanelSizeActual = newSize;
    }

    public void setSaveCaseThreadMaxIdleTime(long saveCaseThreadMaxIdleTime) {
	this.saveCaseThreadMaxIdleTime = saveCaseThreadMaxIdleTime;
    }

    public void setScm_digitcount(int scm_digitcount) {
	this.scm_digitcount = scm_digitcount;
    }

    public void setScm_display_min_percentage(float scm_display_min_percentage) {
	this.scm_display_min_percentage = scm_display_min_percentage;
    }

    public void setSessionMaxInactiveInterval(int sessionMaxInactiveInterval) {
	this.sessionMaxInactiveInterval = sessionMaxInactiveInterval;
    }

    public void setShowAbstractQuestions(boolean showAbstractQuestions) {
	this.showAbstractQuestions = showAbstractQuestions;
    }

    public void setShowCompareCase(boolean showCompareCase) {
	this.showCompareCase = showCompareCase;
    }

    public void setShowCountryFlags(boolean showCountryFlags) {
	this.showCountryFlags = showCountryFlags;
    }

    public void setShowDiagConcreteDerivation(boolean showDiagConcreteDerivation) {
	this.showDiagConcreteDerivation = showDiagConcreteDerivation;
    }

    public void setShowDiagDerivation(boolean showDiagDerivation) {
	this.showDiagDerivation = showDiagDerivation;
    }

    public void setShowDiagExplanation(boolean showDiagExplanation) {
	this.showDiagExplanation = showDiagExplanation;
    }

    public void setShowDiagnosesTreeTab(boolean showDiagnosesTreeTab) {
	this.showDiagnosesTreeTab = showDiagnosesTreeTab;
    }

    public void setShowDiagReason(boolean showDiagReason) {
	this.showDiagReason = showDiagReason;
    }

    public void setShowDialogButton(boolean showDialogButton) {
	this.showDialogButton = showDialogButton;
    }

    public void setShowFrequentness(boolean showFrequentness) {
	this.showFrequentness = showFrequentness;
    }

    public void setShowHeuristicDiagnoses(boolean showHeuristicDiagnoses) {
	this.showHeuristicDiagnoses = showHeuristicDiagnoses;
    }

    public void setShowHeuristicEstablishedDiagnoses(
	    boolean showHeuristicEstablishedDiagnoses) {
	this.showHeuristicEstablishedDiagnoses = showHeuristicEstablishedDiagnoses;
    }

    public void setShowHeuristicExcludedDiagnoses(
	    boolean showHeuristicExcludedDiagnoses) {
	this.showHeuristicExcludedDiagnoses = showHeuristicExcludedDiagnoses;
    }

    public void setShowHeuristicSuggestedDiagnoses(
	    boolean showHeuristicSuggestedDiagnoses) {
	this.showHeuristicSuggestedDiagnoses = showHeuristicSuggestedDiagnoses;
    }

    public void setShowManagementButton(boolean showManagementButton) {
	this.showManagementButton = showManagementButton;
    }

    public void setShowMMInfo(boolean showMMInfo) {
	this.showMMInfo = showMMInfo;
    }

    public void setShowPageFooter(boolean showPageFooter) {
	this.showPageFooter = showPageFooter;
    }

    public void setShowPageHeader(boolean showPageHeader) {
	this.showPageHeader = showPageHeader;
    }

    public void setShowProcessedQContainers(boolean showProcessedQContainers) {
	this.showProcessedQContainers = showProcessedQContainers;
    }

    public void setShowProgressBar(boolean showProgressBar) {
	this.showProgressBar = showProgressBar;
    }

    public void setShowQASetTreeTab(boolean showQASetTreeTab) {
	this.showQASetTreeTab = showQASetTreeTab;
    }

    public void setShowQuestionPageAnswerButton(
	    boolean showQuestionPageAnswerButton) {
	this.showQuestionPageAnswerButton = showQuestionPageAnswerButton;
    }

    public void setShowQuestionPageResultButton(
	    boolean showQuestionPageResultButton) {
	this.showQuestionPageResultButton = showQuestionPageResultButton;
    }

    public void setShowQuestionPageUnknownButton(
	    boolean showQuestionPageUnknownButton) {
	this.showQuestionPageUnknownButton = showQuestionPageUnknownButton;
    }

    public void setShowRightPanel(boolean show) {
	this.showRightPanel = show;
	if (show) {
	    rightPanelSizeActual = rightPanelSize;
	} else {
	    rightPanelSizeActual = 0f;
	}
    }

    public void setShowRightPanelToggleButtons(
	    boolean showRightPanelToggleButtons) {
	this.showRightPanelToggleButtons = showRightPanelToggleButtons;
    }

    public void setShowSCM(boolean showSCM) {
	this.showSCM = showSCM;
    }

    public void setStyleSheetPath(String styleSheetPath) {
	this.styleSheetPath = styleSheetPath;
    }

    public void setTimeZone(String timeZone) {
	this.timeZone = timeZone;
    }

    public void toggleRightFrame(ActionEvent event) {
	setShowRightPanel(!isShowRightPanel());
    }

}

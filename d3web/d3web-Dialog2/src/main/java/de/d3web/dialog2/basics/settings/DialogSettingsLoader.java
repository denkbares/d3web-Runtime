/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.dialog2.basics.settings;

import java.io.File;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import de.d3web.dialog2.util.DialogUtils;

public class DialogSettingsLoader {

    private DialogSettings settings;

    public static Logger logger = Logger.getLogger(DialogSettingsLoader.class);

    public DialogSettingsLoader(DialogSettings settings) {
	this.settings = settings;
    }

    private boolean getBooleanValue(Element setting, String string, boolean old) {
	if (setting == null) {
	    return old;
	}
	Element element = setting.getChild(string);
	if (element == null) {
	    return old;
	}
	return element.getAttributeValue("value") != null ? Boolean
		.parseBoolean(element.getAttributeValue("value")) : old;
    }

    private float getFloatValue(Element setting, String string, float old) {
	if (setting == null) {
	    return old;
	}
	Element element = setting.getChild(string);
	if (element == null) {
	    return old;
	}
	return element.getAttributeValue("value") != null ? Float
		.parseFloat(element.getAttributeValue("value")) : old;
    }

    private int getIntValue(Element setting, String string, int old) {
	if (setting == null) {
	    return old;
	}
	Element element = setting.getChild(string);
	if (element == null) {
	    return old;
	}
	return element.getAttributeValue("value") != null ? Integer
		.parseInt(element.getAttributeValue("value")) : old;
    }

    private long getLongValue(Element setting, String string, long old) {
	if (setting == null) {
	    return old;
	}
	Element element = setting.getChild(string);
	if (element == null) {
	    return old;
	}
	return element.getAttributeValue("value") != null ? Long
		.parseLong(element.getAttributeValue("value")) : old;
    }

    private String getStringValue(Element setting, String string, String old) {
	if (setting == null) {
	    return old;
	}
	Element element = setting.getChild(string);
	if (element == null) {
	    return old;
	}
	return element.getAttributeValue("value") != null ? element
		.getAttributeValue("value") : old;
    }

    public void init(String kbid) {
	SAXBuilder builder;
	Document doc;
	Element root = null;

	File settingsFile;
	if (kbid == null) {
	    String destPath = DialogUtils.getContextPath() + File.separator
		    + "WEB-INF" + File.separator + "classes" + File.separator
		    + "de" + File.separator + "d3web" + File.separator
		    + "dialog2";
	    settingsFile = new File(destPath, DialogSettings.SETTINGS_FILENAME);
	} else {
	    String destPath = ResourceRepository.getInstance()
		    .getBasicSettingValue(ResourceRepository.MULTIMEDIAPATH)
		    .replaceAll("\\$kbid\\$", kbid);
	    if (kbid.contains("..")) {
			destPath = destPath.replaceAll("\\.", "P");
		}
	    settingsFile = new File(DialogUtils.getRealPath(destPath),
		    DialogSettings.SETTINGS_FILENAME);
	}

	try {
	    builder = new SAXBuilder();
	    doc = builder.build(settingsFile);
	    root = doc.getRootElement();

	} catch (Exception e) {
	    logger
		    .error("Error while loading dialogsettings from settingsfile. Using default values...");
	    return;
	}

	readDialogSettings(root);
	readQuestionPageSettings(root);
	readDiagnosesSettings(root);
	readExplanationSettings(root);
	readMMInfoSettings(root);
	readCompareCaseSettings(root);
	readSCMSettings(root);
	readProcessedQContainersSettings(root);
	readFrequentnessSettings(root);
    }

    private void readCompareCaseSettings(Element root) {
	Element ccSettings = root.getChild("CompareCase");

	settings.setShowCompareCase(getBooleanValue(ccSettings,
		"showCompareCase", settings.isShowCompareCase()));
    }

    private void readDiagnosesSettings(Element root) {
	Element diagnosesSettings = root.getChild("Diagnoses");

	settings.setShowHeuristicDiagnoses(getBooleanValue(diagnosesSettings,
		"showHeuristicDiagnoses", settings.isShowHeuristicDiagnoses()));
	settings.setShowHeuristicEstablishedDiagnoses(getBooleanValue(
		diagnosesSettings, "showHeuristicEstablishedDiagnoses",
		settings.isShowHeuristicEstablishedDiagnoses()));
	settings.setShowHeuristicSuggestedDiagnoses(getBooleanValue(
		diagnosesSettings, "showHeuristicSuggestedDiagnoses", settings
			.isShowHeuristicSuggestedDiagnoses()));
	settings.setShowHeuristicExcludedDiagnoses(getBooleanValue(
		diagnosesSettings, "showHeuristicExcludedDiagnoses", settings
			.isShowHeuristicExcludedDiagnoses()));
    }

    private void readDialogSettings(Element root) {
	Element dialogSettings = root.getChild("Dialog");

	settings.setShowProgressBar(getBooleanValue(dialogSettings,
		"showProgressBar", settings.isShowProgressBar()));
	settings.setLeftPanelSize(getFloatValue(dialogSettings,
		"leftPanelSizePercent", settings.getLeftPanelSize()));
	settings.setLeftPanelFixed(getBooleanValue(dialogSettings,
		"leftPanelFixed", settings.isLeftPanelFixed()));
	settings.setRightPanelSize(getFloatValue(dialogSettings,
		"rightPanelSizePercent", settings.getRightPanelSize()));
	settings.setAllowRightPanel(getBooleanValue(dialogSettings,
		"allowRightPanel", settings.isAllowRightPanel()));
	settings.setShowRightPanelToggleButtons(getBooleanValue(dialogSettings,
		"showRightPanelToggleButtons", settings
			.isShowRightPanelToggleButtons()));
	settings.setSaveCaseThreadMaxIdleTime(getLongValue(dialogSettings,
		"saveCaseThreadMaxIdleTime", settings
			.getSaveCaseThreadMaxIdleTime()));
	settings.setSessionMaxInactiveInterval(getIntValue(dialogSettings,
		"sessionMaxInactiveInterval", settings
			.getSessionMaxInactiveInterval()));
	settings.setShowManagementButton(getBooleanValue(dialogSettings,
		"showManagementButton", settings.isShowManagementButton()));
	settings.setShowDialogButton(getBooleanValue(dialogSettings,
		"showDialogButton", settings.isShowDialogButton()));
	settings.setTimeZone(getStringValue(dialogSettings, "timeZone",
		settings.getTimeZone()));
	settings.setShowCountryFlags(getBooleanValue(dialogSettings,
		"showCountryFlags", settings.isShowCountryFlags()));
	settings.setShowPageHeader(getBooleanValue(dialogSettings,
		"showPageHeader", settings.isShowPageHeader()));
	settings.setShowPageFooter(getBooleanValue(dialogSettings,
		"showPageFooter", settings.isShowPageFooter()));
    }

    private void readExplanationSettings(Element root) {
	Element explSettings = root.getChild("Explanation");

	settings.setShowDiagExplanation(getBooleanValue(explSettings,
		"showDiagExplanation", settings.isShowDiagExplanation()));
	settings.setShowDiagReason(getBooleanValue(explSettings,
		"showDiagReason", settings.isShowDiagReason()));
	settings.setShowDiagConcreteDerivation(getBooleanValue(explSettings,
		"showDiagConcreteDerivation", settings
			.isShowDiagConcreteDerivation()));
	settings.setShowDiagDerivation(getBooleanValue(explSettings,
		"showDiagDerivation", settings.isShowDiagDerivation()));
    }

    private void readFrequentnessSettings(Element root) {
	Element freqSettings = root.getChild("Frequentness");

	settings.setShowFrequentness(getBooleanValue(freqSettings,
		"showFrequentness", settings.isShowFrequentness()));

    }

    private void readMMInfoSettings(Element root) {
	Element mmInfoSettings = root.getChild("MMInfo");

	settings.setShowMMInfo(getBooleanValue(mmInfoSettings, "showMMInfo",
		settings.isShowMMInfo()));
	settings.setMaxCharLengthInMMInfoPopup(getIntValue(mmInfoSettings,
		"maxCharLengthInMMInfoPopup", settings
			.getMaxCharLengthInMMInfoPopup()));
    }

    private void readProcessedQContainersSettings(Element root) {
	Element procSettings = root.getChild("ProcessedQContainers");

	settings.setShowProcessedQContainers(getBooleanValue(procSettings,
		"showProcessedQContainers", settings
			.isShowProcessedQContainers()));
	settings.setProcessedShowAll(getBooleanValue(procSettings, "showAll",
		settings.isProcessedShowAll()));
	settings.setProcessedShowUnknown(getBooleanValue(procSettings,
		"showUnknown", settings.isProcessedShowUnknown()));
	settings.setProcessedMaxInput(getIntValue(procSettings, "maxInput",
		settings.getProcessedMaxInput()));
	settings.setProcessedQTextMode(getStringValue(procSettings,
		"qTextMode", settings.getProcessedQTextMode()));
	settings.setProcessedShowQContainerNames(getBooleanValue(procSettings,
		"showQContainerNames", settings
			.isProcessedShowQContainerNames()));
	settings.setProcessedShowUnknownIcon(getBooleanValue(procSettings,
		"showUnknownIcon", settings.isProcessedShowUnknownIcon()));
	settings.setProcessedShowQContainerNamesIcon(getBooleanValue(
		procSettings, "showQContainerNamesIcon", settings
			.isProcessedShowQContainerNamesIcon()));

	settings.setShowAbstractQuestionsInResultPage(getBooleanValue(
		procSettings, "showAbstractQuestionsInResultPage", settings
			.isShowAbstractQuestionsInResultPage()));
    }


    private void readQuestionPageSettings(Element root) {
	Element qPageSettings = root.getChild("QuestionPage");

	settings.setAutoMoveToResultpage(getBooleanValue(qPageSettings,
		"autoMoveToResultpage", settings.isAutoMoveToResultpage()));
	settings.setShowQuestionPageAnswerButton(getBooleanValue(qPageSettings,
		"showQuestionPageAnswerButton", settings
			.isShowQuestionPageAnswerButton()));
	settings.setShowQuestionPageResultButton(getBooleanValue(qPageSettings,
		"showQuestionPageResultButton", settings
			.isShowQuestionPageResultButton()));
	settings.setShowQuestionPageUnknownButton(getBooleanValue(
		qPageSettings, "showQuestionPageUnknownButton", settings
			.isShowQuestionPageUnknownButton()));
	settings.setQuestionMinAnsWrap(getIntValue(qPageSettings,
		"questionMinAnsWrap", settings.getQuestionMinAnsWrap()));
	settings.setQuestionDateDefaultdateSection(getStringValue(
		qPageSettings, "questionDateDefaultDatesection", settings
			.getQuestionDateDefaultdateSection()));
	settings.setDialogMode(getStringValue(qPageSettings, "dialogMode",
		settings.getDialogMode()));
	settings.setMCConstraintsAutoGrayOut(getBooleanValue(qPageSettings,
		"mCConstraintsAutoGrayOut", settings
			.isMCConstraintsAutoGrayOut()));
	settings.setShowQASetTreeTab(getBooleanValue(qPageSettings,
		"showQASetTreeTab", settings.isShowQASetTreeTab()));
	settings.setShowDiagnosesTreeTab(getBooleanValue(qPageSettings,
		"showDiagnosesTreeTab", settings.isShowDiagnosesTreeTab()));
	settings.setShowAbstractQuestions(getBooleanValue(qPageSettings,
		"showAbstractQuestions", settings.isShowAbstractQuestions()));
    }

    private void readSCMSettings(Element root) {
	Element scmSettings = root.getChild("SCM");

	settings.setShowSCM(getBooleanValue(scmSettings, "showSCM", settings
		.isShowSCM()));
	settings.setScm_digitcount(getIntValue(scmSettings, "digitcount",
		settings.getScm_digitcount()));
	settings.setScm_display_min_percentage(getFloatValue(scmSettings,
		"displayMinPercentage", settings
			.getScm_display_min_percentage()));
    }
}

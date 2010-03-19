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

package de.d3web.dialog2.controller;

import org.apache.myfaces.custom.tabbedpane.HtmlPanelTabbedPane;

public class PageDisplayController {

	private static final String PAGEMODE_DIALOG = "dialogmode";
	private static final String PAGEMODE_MANAGEMENT = "managementmode";

	private static final String CENTERPAGE_QUESTIONS = "questions";
	private static final String CENTERPAGE_RESULT = "result";
	private static final String CENTERPAGE_SAVECASE = "saveCase";
	private static final String CENTERPAGE_KBUPLOAD = "kbupload";
	private static final String CENTERPAGE_CHANGELANGUAGE = "changelanguage";
	private static final String CENTERPAGE_LOADCASE = "loadcase";
	private static final String CENTERPAGE_EMPTY = "";

	private static final String LEFTPANEL_DIALOG = "left_dialog";
	private static final String LEFTPANEL_MANAGEMENT = "left_management";

	private static final String RIGHTPANEL_DIALOG = "right_dialog";
	private static final String RIGHTPANEL_EMPTY = "right_empty";

	private String pageMode;

	private String centerContent;
	private String leftContent;
	private String rightContent;

	private HtmlPanelTabbedPane managementTabPane = new HtmlPanelTabbedPane();

	public PageDisplayController() {
		// display management page at startup...
		moveToManagementPage();
	}

	public String getCenterContent() {
		return centerContent;
	}

	public String getLeftContent() {
		return leftContent;
	}

	public HtmlPanelTabbedPane getManagementTabPane() {
		return managementTabPane;
	}

	public String getPageMode() {
		return pageMode;
	}

	public String getRightContent() {
		return rightContent;
	}

	public String moveToChangeLanguagePage() {
		centerContent = CENTERPAGE_CHANGELANGUAGE;
		return "";
	}

	public String moveToDialogPage() {
		moveToQuestionPage();
		return "";
	}

	public String moveToKBUploadPage() {
		centerContent = CENTERPAGE_KBUPLOAD;
		return "";
	}

	public String moveToLoadCasePage() {
		centerContent = CENTERPAGE_LOADCASE;
		return "";
	}

	public String moveToManagementPage() {
		pageMode = PAGEMODE_MANAGEMENT;
		leftContent = LEFTPANEL_MANAGEMENT;
		centerContent = CENTERPAGE_EMPTY;
		rightContent = RIGHTPANEL_EMPTY;

		// TODO example how tabs can be changed...
		managementTabPane.setSelectedIndex(0);
		return "";
	}

	public String moveToQuestionPage() {
		pageMode = PAGEMODE_DIALOG;
		centerContent = CENTERPAGE_QUESTIONS;
		leftContent = LEFTPANEL_DIALOG;
		rightContent = RIGHTPANEL_DIALOG;
		return "";
	}

	public String moveToResultPage() {
		centerContent = CENTERPAGE_RESULT;
		return "";
	}

	public String moveToSaveCasePage() {
		centerContent = CENTERPAGE_SAVECASE;
		return "";
	}

	public void setManagementTabPane(HtmlPanelTabbedPane managementTabPane) {
		this.managementTabPane = managementTabPane;
	}
}

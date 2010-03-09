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

import java.util.LinkedList;
import java.util.List;

import org.apache.myfaces.component.html.ext.HtmlDataTable;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.IMetaData;
import de.d3web.core.knowledge.terminology.info.DCElement;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.dialog2.WebDialog;
import de.d3web.dialog2.basics.knowledge.CaseManager;
import de.d3web.dialog2.basics.knowledge.CaseObjectDescriptor;
import de.d3web.dialog2.util.CaseFinalizationNotifier;
import de.d3web.dialog2.util.DialogUtils;

public class LoadCaseController {

    private String sortColumn = null;

    private boolean sortAscending = true;

    private HtmlDataTable loadCaseDataTable;

    public List<CaseObjectDescriptor> getCases() {
	WebDialog dia = DialogUtils.getDialog();
	List<CaseObjectDescriptor> cases = new LinkedList<CaseObjectDescriptor>();
	String kbid = dia.getTheCase().getKnowledgeBase().getId();
	if (!CaseManager.getInstance().hasCasesForKb(kbid)) {
	    CaseManager.getInstance().loadCases(kbid);
	}
	cases = new LinkedList<CaseObjectDescriptor>(CaseManager.getInstance()
		.getCaseObjectDescriptorsForKb(kbid));
	return cases;
    }

    public HtmlDataTable getLoadCaseDataTable() {
	return loadCaseDataTable;
    }

    public String getSortColumn() {
	return sortColumn;
    }

    public boolean isHasData() {
	if (getCases().size() == 0) {
	    return false;
	}
	return true;
    }

    public boolean isSortAscending() {
	return sortAscending;
    }

    public String loadCase() {
	WebDialog dia = DialogUtils.getDialog();

	CaseObjectDescriptor caseObject = (CaseObjectDescriptor) loadCaseDataTable
		.getRowData();
	CaseObject co = CaseManager.getInstance().getCase(
		dia.getTheCase().getKnowledgeBase().getId(),
		caseObject.getCaseId());

	// a case will be loaded, so we have to execute the CaseFinalizer...
	if (dia.getTheCase() != null) {
	    CaseFinalizationNotifier.finalizeCase(dia.getTheCase());
	}

	dia.setTheCase(DialogUtils.createNewAnsweredCase(co, dia.getTheCase()
		.getKnowledgeBase()));

	dia.setCaseStartTimeToNow();
	dia.setCaseLoaded(true);
	dia.setCaseSaved(false);

	// set the loaded properties into savecasebean
	setLoadedCasePropertiesInSaveCaseBean(co);

	long processingTime = ((IMetaData) co.getProperties().getProperty(
		Property.CASE_METADATA)).getProcessingTime();
	if (processingTime > 0) {
	    dia.setCaseProcessingTime(processingTime * 1000); // because the
							      // time is saved
							      // as x/1000
	}

	// init the Trees so that the actual diagnoses and containers are
	// overtaken

	// re-init QuestionPageBean and move to result page...
	DialogUtils.getQuestionPageBean().init();
	DialogUtils.getPageDisplay().moveToQuestionPage();
	DialogUtils.getPageDisplay().moveToResultPage();

	return "";
    }

    public void setLoadCaseDataTable(HtmlDataTable loadCaseDataTable) {
	this.loadCaseDataTable = loadCaseDataTable;
    }

    private void setLoadedCasePropertiesInSaveCaseBean(CaseObject co) {
	SaveCaseController saveCaseBean = DialogUtils.getSaveCaseBean();
	saveCaseBean.setCaseTitle(co.getDCMarkup().getContent(DCElement.TITLE));
	saveCaseBean.setCaseComment(co.getDCMarkup().getContent(
		DCElement.DESCRIPTION));
	saveCaseBean.setCaseAuthor(co.getDCMarkup().getContent(
		DCElement.CREATOR));
	// TODO should the old date be set?
	saveCaseBean.setCaseDate(DCElement.string2date(co.getDCMarkup()
		.getContent(DCElement.DATE)));

    }

    public void setSortAscending(boolean sortAscending) {
	this.sortAscending = sortAscending;
    }

    public void setSortColumn(String sortColumn) {
	this.sortColumn = sortColumn;
    }
}

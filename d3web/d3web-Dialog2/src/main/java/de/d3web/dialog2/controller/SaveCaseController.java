/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.dialog2.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.caserepository.MetaDataImpl;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.DCElement;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;
import de.d3web.dialog2.WebDialog;
import de.d3web.dialog2.basics.knowledge.CaseManager;
import de.d3web.dialog2.basics.knowledge.CaseRepositoryDescriptor;
import de.d3web.dialog2.basics.settings.ResourceRepository;
import de.d3web.dialog2.basics.usermanaging.User;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.dialog2.util.SessionConverter;
import de.d3web.indication.inference.PSMethodUserSelected;

public class SaveCaseController {

	private String caseTitle;

	private String caseAuthor;

	private String userEmail;

	private String caseComment;

	private Date caseDate;

	private List<String> userSelectedDiags;

	private String lastSavedCaseID;

	public static Logger logger = Logger.getLogger(SaveCaseController.class);

	public SaveCaseController() {
		resetData();
	}

	public void autoSave(CaseObject co, WebDialog dia) {
		saveCaseObject(co, dia);
	}

	public void autoSaveCase() {
		logger.info("Starting autosave...");
		caseDate = new Date();
		// add "autosave" to case
		if (caseTitle != null && caseTitle.indexOf("autosave") == -1) {
			caseTitle += "autosave";
		}
		else {
			caseTitle = "case_autosave";
		}
		caseAuthor = getUserBean().getCompleteName();
		userEmail = getUserBean().getEmail();
		caseComment = "";
		saveCase();
	}

	public String dlCase() {
		WebDialog dia = DialogUtils.getDialog();
		CaseObjectImpl co = getCaseObject(dia.getSession(), caseTitle,
				getUserBean(), dia.getCaseProcessingTime());
		// also save case on server...
		saveCase();

		FacesContext ctx = FacesContext.getCurrentInstance();
		if (!ctx.getResponseComplete()) {
			// TODO case name??
			String fileName = "mycase.xml";

			HttpServletResponse response = (HttpServletResponse) ctx
					.getExternalContext().getResponse();
			response.setContentType("text/plain");
			response.setHeader("Content-Disposition", "attachment;filename=\""
					+ fileName + "\"");

			ServletOutputStream out;
			try {
				out = response.getOutputStream();
				out.write(co.getXMLCode().getBytes());
				out.flush();
			}
			catch (IOException e) {
				logger.error(e);
			}
			ctx.responseComplete();
		}
		return "";
	}

	public String getCaseAuthor() {
		if (caseAuthor == null) {
			caseAuthor = getUserBean().getCompleteName();
		}
		return caseAuthor;
	}

	public String getCaseComment() {
		return caseComment;
	}

	public Date getCaseDate() {
		return new Date();
	}

	public CaseObjectImpl getCaseObject(Session session, String caseName,
			User user, long processingtime) {
		CaseObjectImpl co = SessionConverter.getInstance().session2CaseObject(
				session, false, false);
		// DCMarkup
		String id = ""
				+ (CaseManager.getInstance().getMaxCaseIdForKb(
						session.getKnowledgeBase().getId()) + 1);
		co.getDCMarkup().setContent(DCElement.IDENTIFIER, id);
		lastSavedCaseID = id;

		co.getDCMarkup().setContent(DCElement.TITLE, caseName);
		if (caseAuthor == null || caseAuthor.trim().length() == 0) {
			caseAuthor = user.getCompleteName();
		}
		co.getDCMarkup().setContent(DCElement.CREATOR, caseAuthor);

		if (caseDate == null) {
			caseDate = new Date();
		}
		co.getDCMarkup().setContent(DCElement.DATE,
				DCElement.date2string(caseDate));

		co.getDCMarkup().setContent(DCElement.DESCRIPTION, caseComment);

		// Properties
		co.getProperties().setProperty(Property.CASE_KNOWLEDGEBASE_DESCRIPTOR,
				session.getKnowledgeBase().getDCMarkup());
		co.getProperties().setProperty(Property.CASE_SOURCE_SYSTEM,
				CaseObject.SourceSystem.DIALOG.getName());
		co.getProperties().setProperty(
				Property.CASE_CRITIQUE_TEXT,
				session.getProperties()
						.getProperty(Property.CASE_CRITIQUE_TEXT));

		// MetaData
		MetaDataImpl md = new MetaDataImpl();
		md.setAccount(user.getEmail());
		long time = Math.round(processingtime / 1000.0);
		md.setProcessingTime(time);
		co.getProperties().setProperty(Property.CASE_METADATA, md);
		// save user-selected diagnoses
		setUserSelectedDiagnoses(co, session);

		return co;
	}

	public String getCaseTitle() {
		return caseTitle;
	}

	public String getLastSavedCaseID() {
		return lastSavedCaseID;
	}

	private User getUserBean() {
		return DialogUtils.getUserBean().getUser();
	}

	public String getUserEmail() {
		if (userEmail == null) {
			userEmail = getUserBean().getEmail();
		}
		return userEmail;
	}

	public List<String> getUserSelectedDiags() {
		return userSelectedDiags;
	}

	public String overwriteCase() {
		if (lastSavedCaseID != null) {
			WebDialog dia = DialogUtils.getDialog();
			CaseManager.getInstance().removeCase(
					dia.getSession().getKnowledgeBase().getId(),
					lastSavedCaseID);
		}
		return saveCase();
	}

	public void resetData() {
		caseTitle = null;
		caseComment = null;
		caseAuthor = null;
		caseDate = new Date();
		userSelectedDiags = new ArrayList<String>();
	}

	public String saveCase() {
		WebDialog dia = DialogUtils.getDialog();
		saveCaseObject(getCaseObject(dia.getSession(), caseTitle,
				getUserBean(), dia.getCaseProcessingTime()), dia);

		// TODO show a msg that the case was saved ?
		return "";
	}

	private void saveCaseObject(CaseObject co, WebDialog dia) {
		// save case to repository
		CaseManager cman = CaseManager.getInstance();
		CaseRepositoryDescriptor crd = cman.getCRDforUser(dia.getSession()
				.getKnowledgeBase().getId(), getUserBean().getEmail(),
				ResourceRepository.getInstance().getBasicSettingValue(
						ResourceRepository.CR_LOCATIONTYPE));
		logger.info("Saving case...");
		if (!cman.addCase(co, crd)) {
			logger.error("Case could not be saved!");
		}
		cman.saveCases(crd);
		dia.setCaseSaved(true);
	}

	public void setCaseAuthor(String caseAuthor) {
		this.caseAuthor = caseAuthor;
	}

	public void setCaseComment(String caseComment) {
		this.caseComment = caseComment;
	}

	public void setCaseDate(Date caseDate) {
		this.caseDate = caseDate;
	}

	public void setCaseTitle(String caseTitle) {
		this.caseTitle = caseTitle;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	private void setUserSelectedDiagnoses(CaseObjectImpl co, Session session) {
		KnowledgeBase kb = session.getKnowledgeBase();
		for (Iterator<String> iter = userSelectedDiags.iterator(); iter
				.hasNext();) {
			String diagId = iter.next();
			Solution d = kb.searchSolution(diagId);
			if (d != null) {
				CaseObject.Solution sol = co.getSolution(d,
						PSMethodUserSelected.class);
				if (sol == null) {
					sol = new CaseObject.Solution();
					sol.setSolution(d);
					sol.setPSMethodClass(PSMethodUserSelected.class);
					sol.setState(new Rating(Rating.State.ESTABLISHED));
					co.addSolution(sol);
				}
				else {
					sol.setState(new Rating(Rating.State.ESTABLISHED));
				}
			}
		}
	}

	public void setUserSelectedDiags(List<String> userSelectedDiags) {
		this.userSelectedDiags = userSelectedDiags;
	}
}

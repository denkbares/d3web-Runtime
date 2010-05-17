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

package de.d3web.dialog2;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.d3web.core.session.Session;
import de.d3web.dialog2.basics.knowledge.CaseManager;
import de.d3web.dialog2.basics.knowledge.KBDescriptorLoader;
import de.d3web.dialog2.basics.knowledge.KnowledgeBaseRepository;
import de.d3web.dialog2.basics.settings.ResourceRepository;
import de.d3web.dialog2.basics.usermanaging.UserManager;
import de.d3web.dialog2.util.CaseFinalizationNotifier;
import de.d3web.dialog2.util.DialogUtils;

public class WebDialog {

	private Session theCase;

	private long caseStartTime = System.currentTimeMillis();

	private long caseProcessingTime;

	private boolean caseLoaded;

	private boolean caseSaved;

	public static Logger logger = Logger.getLogger(WebDialog.class);

	public static void resetWebApp() {
		ResourceRepository.getInstance().initialize();
		KBDescriptorLoader.getInstance().initialize();
		KnowledgeBaseRepository.getInstance().initialize();
		UserManager.getInstance().reset();
		CaseManager.getInstance().initialize();
	}

	public WebDialog() {
		// to set the right contextPath...
		DialogUtils.init();
		CaseManager.getInstance().initialize();
	}

	public long getCaseProcessingTime() {
		return System.currentTimeMillis() - caseStartTime + caseProcessingTime;
	}

	public long getCaseStartTime() {
		return caseStartTime;
	}

	public Session getTheCase() {
		return theCase;
	}

	public boolean isCaseLoaded() {
		return caseLoaded;
	}

	public boolean isCaseSaved() {
		return caseSaved;
	}

	public String killSession() {
		logger.info("Sessions gets invalidated...");
		resetWebApp();
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return "";
	}

	public void setCaseLoaded(boolean caseLoaded) {
		this.caseLoaded = caseLoaded;
	}

	public void setCaseProcessingTime(long caseProcessingTime) {
		this.caseProcessingTime = caseProcessingTime;
	}

	public void setCaseSaved(boolean caseSaved) {
		this.caseSaved = caseSaved;
	}

	public void setCaseStartTimeToNow() {
		caseStartTime = System.currentTimeMillis();
	}

	public void setTheCase(Session theCase) {
		this.theCase = theCase;
		DialogUtils.getKBLoadBean().setLoadedKb(theCase.getKnowledgeBase());
		DialogUtils.getKBLoadBean().setKbID(theCase.getKnowledgeBase().getId());
	}

	public String startNewCase() {
		// we will start a new case, so we have to execute the casefinalizer...
		if (theCase != null) {
			CaseFinalizationNotifier.finalizeCase(theCase);
		}

		setCaseStartTimeToNow();
		caseProcessingTime = 0;
		setTheCase(DialogUtils.createNewCase(DialogUtils.getKBLoadBean()
				.getLoadedKb()));
		caseLoaded = false;
		caseSaved = false;
		// reset savecase data...
		DialogUtils.getSaveCaseBean().resetData();

		DialogUtils.getQuestionPageBean().init();

		return DialogUtils.getPageDisplay().moveToQuestionPage();
	}

}

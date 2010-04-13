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

package de.d3web.dialog2.util;

import org.apache.log4j.Logger;

import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.core.session.ValuedObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionEventListener;
import de.d3web.dialog2.WebDialog;
import de.d3web.dialog2.basics.knowledge.CaseManager;
import de.d3web.dialog2.basics.usermanaging.User;
import de.d3web.dialog2.controller.SaveCaseController;

public class SaveCaseThread extends Thread implements SessionEventListener {

    private WebDialog dia;

    private SaveCaseController saveCaseBean;

    private Session caseToSave;

    private User user;

    private long maxIdleTime;

    private long timeCaseStarted;

    private long timeToSave;

    private boolean hasBeenNotified = false;

    public static Logger logger = Logger.getLogger(SaveCaseThread.class);

    public SaveCaseThread(WebDialog dia, SaveCaseController saveCaseBean,
	    Session caseToSave, User user, long maxIdleTime) {
	this.dia = dia;
	this.saveCaseBean = saveCaseBean;
	this.caseToSave = caseToSave;
	this.user = user;
	this.maxIdleTime = maxIdleTime;

	caseToSave.addListener(this);
	timeToSave = System.currentTimeMillis() + maxIdleTime;
	timeCaseStarted = System.currentTimeMillis();
    }

    public void notify(Session session, ValuedObject o, Object context) {
	timeToSave = System.currentTimeMillis() + maxIdleTime;
	hasBeenNotified = true;
    }

    @Override
    public void run() {
	logger.info("SaveCaseThread started. Saving case in " + maxIdleTime
		+ " ms.");
	long diff;
	while ((diff = (timeToSave - System.currentTimeMillis())) > 0) {
	    try {
		Thread.sleep(diff);
	    } catch (InterruptedException ex) {
		logger.warn(ex);
	    }
	}
	if (hasBeenNotified) {
	    saveCase();
	    caseToSave.removeListener(this);
	}
    }

    private void saveCase() {
	String caseName = ""
		+ (CaseManager.getInstance().getMaxCaseIdForKb(
			caseToSave.getKnowledgeBase().getId()) + 1);

	CaseObjectImpl co = saveCaseBean.getCaseObject(caseToSave, caseName,
		user, timeToSave - maxIdleTime - timeCaseStarted);
	logger.info("autosave case...");
	saveCaseBean.autoSave(co, dia);
	dia.setCaseSaved(true);
    }

}

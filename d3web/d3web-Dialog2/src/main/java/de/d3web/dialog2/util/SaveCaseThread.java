package de.d3web.dialog2.util;

import org.apache.log4j.Logger;

import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.dialog2.WebDialog;
import de.d3web.dialog2.basics.knowledge.CaseManager;
import de.d3web.dialog2.basics.usermanaging.User;
import de.d3web.dialog2.controller.SaveCaseController;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.ValuedObject;
import de.d3web.kernel.domainModel.XPSCaseEventListener;

public class SaveCaseThread extends Thread implements XPSCaseEventListener {

    private WebDialog dia;

    private SaveCaseController saveCaseBean;

    private XPSCase caseToSave;

    private User user;

    private long maxIdleTime;

    private long timeCaseStarted;

    private long timeToSave;

    private boolean hasBeenNotified = false;

    public static Logger logger = Logger.getLogger(SaveCaseThread.class);

    public SaveCaseThread(WebDialog dia, SaveCaseController saveCaseBean,
	    XPSCase caseToSave, User user, long maxIdleTime) {
	this.dia = dia;
	this.saveCaseBean = saveCaseBean;
	this.caseToSave = caseToSave;
	this.user = user;
	this.maxIdleTime = maxIdleTime;

	caseToSave.addListener(this);
	timeToSave = System.currentTimeMillis() + maxIdleTime;
	timeCaseStarted = System.currentTimeMillis();
    }

    public void notify(XPSCase xpsCase, ValuedObject o, Object context) {
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

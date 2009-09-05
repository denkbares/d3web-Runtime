package de.d3web.dialog2;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.d3web.dialog2.basics.knowledge.CaseManager;
import de.d3web.dialog2.basics.knowledge.KBDescriptorLoader;
import de.d3web.dialog2.basics.knowledge.KnowledgeBaseRepository;
import de.d3web.dialog2.basics.settings.ResourceRepository;
import de.d3web.dialog2.basics.usermanaging.UserManager;
import de.d3web.dialog2.util.CaseFinalizationNotifier;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.dialog2.util.SaveCaseThread;
import de.d3web.kernel.XPSCase;

public class WebDialog {

    private XPSCase theCase;

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

    public XPSCase getTheCase() {
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

    public void setTheCase(XPSCase theCase) {
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

	// init saveCaseThread
	long saveCaseThreadMaxIdleTime = DialogUtils.getDialogSettings()
		.getSaveCaseThreadMaxIdleTime();
	if (saveCaseThreadMaxIdleTime > 0) {
	    SaveCaseThread saveCaseThread = new SaveCaseThread(this,
		    DialogUtils.getSaveCaseBean(), theCase, DialogUtils
			    .getUserBean().getUser(), saveCaseThreadMaxIdleTime);
	    theCase.addListener(saveCaseThread);
	    saveCaseThread.start();
	}

	DialogUtils.getQuestionPageBean().init();

	return DialogUtils.getPageDisplay().moveToQuestionPage();
    }

}

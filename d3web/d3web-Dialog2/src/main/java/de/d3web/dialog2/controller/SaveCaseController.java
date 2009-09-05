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
import de.d3web.caserepository.utilities.CaseConverter;
import de.d3web.dialog2.WebDialog;
import de.d3web.dialog2.basics.knowledge.CaseManager;
import de.d3web.dialog2.basics.knowledge.CaseRepositoryDescriptor;
import de.d3web.dialog2.basics.settings.ResourceRepository;
import de.d3web.dialog2.basics.usermanaging.User;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.psMethods.userSelected.PSMethodUserSelected;
import de.d3web.kernel.supportknowledge.DCElement;
import de.d3web.kernel.supportknowledge.Property;

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
	} else {
	    caseTitle = "case_autosave";
	}
	caseAuthor = getUserBean().getCompleteName();
	userEmail = getUserBean().getEmail();
	caseComment = "";
	saveCase();
    }

    public String dlCase() {
	WebDialog dia = DialogUtils.getDialog();
	CaseObjectImpl co = getCaseObject(dia.getTheCase(), caseTitle,
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
	    } catch (IOException e) {
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

    public CaseObjectImpl getCaseObject(XPSCase theCase, String caseName,
	    User user, long processingtime) {
	CaseObjectImpl co = CaseConverter.getInstance().xpsCase2CaseObject(
		theCase, false, false);
	// DCMarkup
	String id = ""
		+ (CaseManager.getInstance().getMaxCaseIdForKb(
			theCase.getKnowledgeBase().getId()) + 1);
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
		theCase.getKnowledgeBase().getDCMarkup());
	co.getProperties().setProperty(Property.CASE_SOURCE_SYSTEM,
		CaseObject.SourceSystem.DIALOG.getName());
	co.getProperties().setProperty(
		Property.CASE_CRITIQUE_TEXT,
		theCase.getProperties()
			.getProperty(Property.CASE_CRITIQUE_TEXT));

	// MetaData
	MetaDataImpl md = new MetaDataImpl();
	md.setAccount(user.getEmail());
	long time = Math.round(processingtime / 1000.0);
	md.setProcessingTime(time);
	co.getProperties().setProperty(Property.CASE_METADATA, md);
	// save user-selected diagnoses
	setUserSelectedDiagnoses(co, theCase);

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
		    dia.getTheCase().getKnowledgeBase().getId(),
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
	saveCaseObject(getCaseObject(dia.getTheCase(), caseTitle,
		getUserBean(), dia.getCaseProcessingTime()), dia);

	// TODO show a msg that the case was saved ?
	return "";
    }

    private void saveCaseObject(CaseObject co, WebDialog dia) {
	// save case to repository
	CaseManager cman = CaseManager.getInstance();
	CaseRepositoryDescriptor crd = cman.getCRDforUser(dia.getTheCase()
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

    private void setUserSelectedDiagnoses(CaseObjectImpl co, XPSCase theCase) {
	KnowledgeBase kb = theCase.getKnowledgeBase();
	for (Iterator<String> iter = userSelectedDiags.iterator(); iter
		.hasNext();) {
	    String diagId = iter.next();
	    Diagnosis d = kb.searchDiagnosis(diagId);
	    if (d != null) {
		CaseObject.Solution sol = co.getSolution(d,
			PSMethodUserSelected.class);
		if (sol == null) {
		    sol = new CaseObject.Solution();
		    sol.setDiagnosis(d);
		    sol.setPSMethodClass(PSMethodUserSelected.class);
		    sol.setState(DiagnosisState.ESTABLISHED);
		    co.addSolution(sol);
		} else {
		    sol.setState(DiagnosisState.ESTABLISHED);
		}
	    }
	}
    }

    public void setUserSelectedDiags(List<String> userSelectedDiags) {
	this.userSelectedDiags = userSelectedDiags;
    }
}

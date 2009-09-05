package de.d3web.dialog2.basics.knowledge;

import java.util.Date;

/**
 * @author gbuscher
 */
public class CaseObjectDescriptor {

    private String title;
    private String caseId;
    private Date date;

    public CaseObjectDescriptor(String title, String caseId, Date date) {
	setTitle(title);
	setCaseId(caseId);
	setDate(date);
    }

    public String getCaseId() {
	return caseId;
    }

    public Date getDate() {
	return date;
    }

    public String getTitle() {
	return title;
    }

    public void setCaseId(String caseId) {
	this.caseId = caseId;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public void setTitle(String title) {
	this.title = title;
    }

}

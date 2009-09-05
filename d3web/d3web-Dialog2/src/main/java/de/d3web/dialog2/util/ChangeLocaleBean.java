package de.d3web.dialog2.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

public class ChangeLocaleBean {

    private List<SelectItem> languages;

    private String language;

    private Locale locale;

    public ChangeLocaleBean() {
	locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	language = locale.toString();
    }

    public void changeLanguage(ActionEvent event) {
	if (language.equals("de")) {
	    germanAction();
	} else if (language.equals("en")) {
	    englishAction();
	}
    }

    public String englishAction() {
	setLocale(Locale.ENGLISH);
	FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
	return null;
    }

    public String germanAction() {
	setLocale(Locale.GERMAN);
	FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
	return null;
    }

    public String getLanguage() {
	return language;
    }

    public List<SelectItem> getLanguages() {
	if (languages == null) {
	    languages = new ArrayList<SelectItem>();

	    SelectItem deutsch = new SelectItem();
	    deutsch.setLabel("deutsch");
	    deutsch.setValue("de");
	    languages.add(deutsch);

	    SelectItem english = new SelectItem();
	    english.setLabel("english");
	    english.setValue("en");
	    languages.add(english);
	}
	return languages;
    }

    public Locale getLocale() {
	return locale;
    }

    public void setLanguage(String language) {
	this.language = language;
    }

    public void setLocale(Locale locale) {
	this.locale = locale;
    }
}

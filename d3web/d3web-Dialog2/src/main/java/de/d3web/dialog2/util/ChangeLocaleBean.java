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

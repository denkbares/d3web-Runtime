/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.interview;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;

/**
 * Null object to represent an empty form, where no questions are presented in
 * the dialog.
 *
 * @author joba
 */
public final class EmptyForm implements Form {

	public static final String EMPTY_FORM_STRING = "EMPTY";
	private static Form instance;

	private EmptyForm() {
	}

	@NotNull
	@Override
	public String getName() {
		return EMPTY_FORM_STRING;
	}

	@NotNull
	@Override
	public String getPrompt(Locale lang) {
		return "";
	}

	public static Form getInstance() {
		if (instance == null) {
			instance = new EmptyForm();
		}
		return instance;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof EmptyForm);
	}

	@Override
	public int hashCode() {
		return getTitle().hashCode();
	}

	@Override
	public String toString() {
		return EMPTY_FORM_STRING;
	}

	@Override
	public List<Question> getActiveQuestions() {
		return Collections.emptyList();
	}

	@Override
	public QContainer getRoot() {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public InterviewObject getInterviewObject() {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getTitle() {
		return EMPTY_FORM_STRING;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isNotEmpty() {
		return false;
	}
}

/*
 * Copyright (C) 2013 denkbares GmbH
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

import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;

/**
 * A Form groups interview items, which should be presented at the same time to
 * the user.
 * <p>
 * Note: The reference to de.d3web.core.session.interviewmanager.Form will be
 * removed, when the class is removed from d3web-Kernel
 *
 * @author Markus Friedrich (denkbares GmbH)
 * @created 25.03.2013
 */
public interface Form extends de.d3web.core.session.interviewmanager.Form {

	/**
	 * Returns the Name of the Form. The name is a unique name that identifies
	 * the form's source, e.g. the name of the qcontainer or question, or a generic name.
	 *
	 * @return the name of this form
	 */
	@NotNull
	String getName();

	/**
	 * Returns the user-displayable title of the form for the specified language
	 * or a more generic language if the specified language is not available.
	 *
	 * @param lang the language to request the prompt for
	 * @return the prompt of this form
	 */
	@NotNull
	String getPrompt(Locale lang);

	/**
	 * Returns true if the form is empty. The form is empty if it has no active questions.
	 */
	boolean isEmpty();

	/**
	 * Returns all active questions of this form
	 *
	 * @return a List of active objects
	 * @created 25.03.2013
	 */
	List<Question> getActiveQuestions();

	/**
	 * Can be used to access the root QASet of the Form. If the form contains
	 * only a question, null is returned
	 *
	 * @return root QASet or null if this form contains only a Question
	 * @created 25.03.2013
	 */
	QContainer getRoot();

}

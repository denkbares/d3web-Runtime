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

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;

/**
 * A Form groups interview items, which should be presented at the same time to
 * the user.
 * 
 * Note: The reference to de.d3web.core.session.interviewmanager.Form will be
 * removed, when the class is removed from d3web-Kernel
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 25.03.2013
 */
@SuppressWarnings("deprecation")
public interface Form extends de.d3web.core.session.interviewmanager.Form {

	/**
	 * Returns the title of the Form
	 */
	@Override
	String getTitle();

	/**
	 * Returns true if the form is empty
	 */
	@Override
	boolean isNotEmpty();

	/**
	 * Returns all active questions of this form
	 * 
	 * @created 25.03.2013
	 * @return a List of active objects
	 */
	List<Question> getActiveQuestions();

	/**
	 * Can be used to access the root QASet of the Form. If the form contains
	 * only a question, null is returned
	 * 
	 * @created 25.03.2013
	 * @return root QASet or null if this form contains only a Question
	 */
	QContainer getRoot();

}

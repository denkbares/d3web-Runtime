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

package de.d3web.core.knowledge.terminology;

import de.d3web.core.manage.AnswerFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.utilities.Utils;

/**
 * This is a simple extension of the QuestionChoice with only has two possible
 * values, that are restricted to a YES and NO answer. <Br>
 * Creation date: (28.09.00 16:51:21)
 * 
 * @author Joachim Baumeister
 */
public class QuestionYN extends QuestionOC {

	protected final static String YES_STRING = "Yes";
    protected final static String NO_STRING = "No";

	public AnswerChoice yes;
	public AnswerChoice no;

	/**
	 * Creates a new Yes-No Question, which is a simple QuestionChoice with only
	 * instance-dependent two alternatives (YES, NO).
	 * 
	 * @see QuestionOC
	 * @see Question
	 * @see NamedObject
	 */
    
    public QuestionYN(String id) {
		super(id);
		yes = AnswerFactory.createAnswerYes(id + "YES", YES_STRING);
		no = AnswerFactory.createAnswerNo(id + "NO", NO_STRING);
		setAlternatives(Utils.createList(new AnswerChoice[] {
				yes, no }));
    }

    public QuestionYN(String id, String yesText, String noText) {
		super(id);
		yes = AnswerFactory.createAnswerYes(id + "YES", yesText);
		no = AnswerFactory.createAnswerNo(id + "NO", noText);
    	setAlternatives(Utils.createList(new AnswerChoice[] {
				yes, no }));
    }

	/**
	 * Sets this question to the NO answer.
	 * 
	 * @Deprecated Use the standard setters.
	 * @param theCase
	 *            XPSCase
	 */
	@Deprecated
    public void setValueNo(Session theCase) {
		super.setValue(theCase, new ChoiceValue(no));
    }

	/**
	 * Sets this question to the YES answer.
	 * 
	 * @Deprecated Use the standard setters.
	 * 
	 * @param theCase
	 *            de.d3web.kernel.domainModel.XPSCase
	 */
	@Deprecated
    public void setValueYes(Session theCase) {
		super.setValue(theCase, new ChoiceValue(yes));
    }
}
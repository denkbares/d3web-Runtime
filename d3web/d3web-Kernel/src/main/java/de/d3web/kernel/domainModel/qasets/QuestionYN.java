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

package de.d3web.kernel.domainModel.qasets;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerFactory;
import de.d3web.kernel.utilities.Utils;

/**
 * This is a simple extension of the QuestionChoice with only has two ansers,
 * which are sixed to a YES and NO answer. Creation date: (28.09.00 16:51:21)
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
     * instance-dependend two alternatives (YES, NO). Additionally you must set
     * some properties by the settter/getters. Important properties are:
     * <LI> knowledgeBase : belonging to the KBObject
     * <LI> id : an unique identifier for the KBObject
     * <LI> text : a name for the KBObject
     * 
     * @see QuestionOC
     * @see Question
     * @see NamedObject
     */
    public QuestionYN() {
	this(YES_STRING, NO_STRING);
    }

    public QuestionYN(String id) {
	super(id);

	yes = AnswerFactory.createAnswerYes(id + "YES", YES_STRING);
	no = AnswerFactory.createAnswerNo(id + "NO", NO_STRING);

	setAlternatives(Utils.createList(new Object[] { yes, no }));
    }

    public QuestionYN(String yesText, String noText) {
	super();

	yes = AnswerFactory.createAnswerYes("", yesText);
	no = AnswerFactory.createAnswerNo("", noText);

	setAlternatives(Utils.createList(new Object[] { yes, no }));

    }

    public void setId(String theID) {
	super.setId(theID);
	yes.setId(getId() + "YES");
	no.setId(getId() + "NO");
    }

    /**
     * Sets this question to the NO answer.
     * 
     * @param theCase
     *                de.d3web.kernel.domainModel.XPSCase
     */
    public void setValueNo(XPSCase theCase) {
	super.setValue(theCase, new Object[] { no });
    }

    /**
     * Sets this question to the YES answer.
     * 
     * @param theCase
     *                de.d3web.kernel.domainModel.XPSCase
     */
    public void setValueYes(XPSCase theCase) {
	super.setValue(theCase, new Object[] { yes });
    }
}
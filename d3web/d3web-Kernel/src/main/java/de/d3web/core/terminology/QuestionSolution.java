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

package de.d3web.core.terminology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.manage.AnswerFactory;
import de.d3web.core.session.IEventSource;
import de.d3web.core.session.KBOEventListener;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.terminology.info.Property;
import de.d3web.core.utilities.Utils;
import de.d3web.xcl.XCLModel;

public class QuestionSolution extends QuestionOC implements KBOEventListener {

    protected final static String HIGH_STRING = "High";
    protected final static String MEDIUM_STRING = "Medium";
    protected final static String UNCLEAR_STRING = "Unclear";
    protected final static String NOT_STRING = "Not";

    private boolean mapped = false;
    private Diagnosis d = null;

    public boolean isMapped() {
	return mapped;
    }

    public AnswerChoice getHigh() {
	return high;
    }

    public AnswerChoice getMedium() {
	return medium;
    }

    public AnswerChoice getUnclear() {
	return unclear;
    }

    public AnswerChoice getNot() {
	return not;
    }

    private AnswerChoice high;
    private AnswerChoice medium;
    private AnswerChoice unclear;
    private AnswerChoice not;

    private static final String[] highStrings = { "ja", "yes", "high", "hoch",
	    "true", "wahr", "trifft zu", "zutreffend", "is true",
	    "is accurate", "established", "etabliert" };
    private static final String[] mediumStrings = { "medium", "mittel",
	    "möglich", "possible", "teilweise", "suggested", "verdächtig",
	    "verdächtigt" };
    private static final String[] unclearStrings = { "unklar", "unclear",
	    "unsicher", "not clarified" };
    private static final String[] notStrings = { "nein", "no", "false",
	    "excluded", "ausgeschlossen", "nicht", "not", "falsch", "false" };

    public AnswerChoice selectAnswerForString(String answerText) {
	for (String s : highStrings) {
	    if (s.equalsIgnoreCase(answerText)) {
		return high;
	    }
	}

	for (String s : mediumStrings) {
	    if (s.equalsIgnoreCase(answerText)) {
		return medium;
	    }
	}

	for (String s : unclearStrings) {
	    if (s.equalsIgnoreCase(answerText)) {
		return unclear;
	    }
	}

	for (String s : notStrings) {
	    if (s.equalsIgnoreCase(answerText)) {
		return not;
	    }
	}

	return null;

    }

    public QuestionSolution(String highText, String mediumText,
	    String unclearString, String notString) {
	super();
	this.getProperties().setProperty(Property.ABSTRACTION_QUESTION,
		Boolean.TRUE);
	high = AnswerFactory.createAnswerYes("", highText);
	medium = AnswerFactory.createAnswerNo("", mediumText);
	unclear = AnswerFactory.createAnswerNo("", unclearString);
	not = AnswerFactory.createAnswerNo("", notString);

	setAlternatives(Utils.createList(new Object[] { high, medium, unclear,
		not }));
    }
    
    public QuestionSolution() {
	this(HIGH_STRING, MEDIUM_STRING, UNCLEAR_STRING, NOT_STRING);
    }
    
    public QuestionSolution(String id) {
	super(id);
	this.getProperties().setProperty(Property.ABSTRACTION_QUESTION,
		Boolean.TRUE);
	high = AnswerFactory.createAnswerYes(id + HIGH_STRING, HIGH_STRING);
	medium = AnswerFactory.createAnswerNo(id + MEDIUM_STRING, MEDIUM_STRING);
	unclear = AnswerFactory.createAnswerNo(id + UNCLEAR_STRING, UNCLEAR_STRING);
	not = AnswerFactory.createAnswerNo(id + NOT_STRING, NOT_STRING);

	setAlternatives(Utils.createList(new Object[] { high, medium, unclear,
		not }));
    }

    public void setId(String theID) {
	super.setId(theID);
	high.setId(getId() + HIGH_STRING);
	medium.setId(getId() + MEDIUM_STRING);
	unclear.setId(getId() + UNCLEAR_STRING);
	not.setId(getId() + NOT_STRING);

    }

    /**
     * @return a List of Answers which are currently the value of the question.
     */
    public List<Answer> getValue(XPSCase theCase) {

	ArrayList<Answer> v = new ArrayList<Answer>();

	Diagnosis d = null;
	List<Diagnosis> solutions = theCase.getKnowledgeBase().getDiagnoses();
	for (Diagnosis diagnosis : solutions) {
	    if (diagnosis.getText().equals(this.getText())) {
		d = diagnosis;
		break;
	    }
	}

	if (d != null) {
	    Collection<KnowledgeSlice> slices = theCase.getKnowledgeBase()
		    .getAllKnowledgeSlices();
	    for (KnowledgeSlice knowledgeSlice : slices) {
		if (knowledgeSlice instanceof XCLModel) {
		    XCLModel model = ((XCLModel) knowledgeSlice);
		    if (model.getSolution().equals(d)) {
			DiagnosisState state = model.getState(theCase);
			if (state.equals(DiagnosisState.ESTABLISHED)) {
			    this.setValueHigh(theCase);
			    v.add(high);
			}
			if (state.equals(DiagnosisState.SUGGESTED)) {
			    this.setValueMedium(theCase);
			    v.add(medium);
			}
			if (state.equals(DiagnosisState.UNCLEAR)) {
			    this.setValueUnclear(theCase);
			    v.add(unclear);
			}
			if (state.equals(DiagnosisState.EXCLUDED)) {
			    this.setValueNot(theCase);
			    v.add(not);
			}
			break;
		    }
		}
	    }
	}

	return v;
    }

    public void setValueHigh(XPSCase theCase) {
	super.setValue(theCase, new Object[] { high });
    }

    public void setValueMedium(XPSCase theCase) {
	super.setValue(theCase, new Object[] { medium });
    }

    public void setValueNot(XPSCase theCase) {
	super.setValue(theCase, new Object[] { not });
    }

    public void setValueUnclear(XPSCase theCase) {
	super.setValue(theCase, new Object[] { unclear });
    }

    public void notify(IEventSource source, XPSCase xpsCase) {
	if (source instanceof Diagnosis && this.d != null) {
	    if (((Diagnosis) source).equals(d)) {
		DiagnosisState state = d.getState(xpsCase, null);
		if (state.equals(DiagnosisState.ESTABLISHED)) {
		    this.setValueHigh(xpsCase);
		}
		if (state.equals(DiagnosisState.SUGGESTED)) {
		    this.setValueMedium(xpsCase);
		}
		if (state.equals(DiagnosisState.UNCLEAR)) {
		    this.setValueUnclear(xpsCase);
		}
		if (state.equals(DiagnosisState.EXCLUDED)) {
		    this.setValueNot(xpsCase);
		}
	    }
	}

    }

}

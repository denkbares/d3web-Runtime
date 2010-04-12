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

package de.d3web.core.inference;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.ChoiceValue;

/**
 * This is a 'marker' psmethod to represent all the initial values.
 * Especially used to add the initQASets to the QASetManager
 * Creation date: (21.02.2002 16:51:10)
 * @author Christian Betz
 */
public class PSMethodInit implements PSMethod {
	private static PSMethodInit instance = null;

	public static PSMethodInit getInstance() {
		if (instance == null) {
			instance = new PSMethodInit();
		}
		return instance;
	}

	public PSMethodInit() {
		super();
	}

	/**
	 * @return null
	 */
	public DiagnosisState getState(Session theCase, Solution theDiagnosis) {
		return null;
	}

	/**
	 * Some space for initial methods of a PSMethod.
	 * Does nothing.
	 * Creation date: (21.02.2002 16:51:10)
	 */
	public void init(Session theCase) {
		theCase.getPropagationContoller().openPropagation();
		try {
			//initialise all questions
			KnowledgeBase kb = theCase.getKnowledgeBase();
			for (Question q: kb.getQuestions()) {
				Object property = q.getProperties().getProperty(Property.INIT);
				if (property != null) {
					String s = (String) property;
					List<String> ids = new LinkedList<String>();
					int posstart = 0;
					int posend = s.indexOf(";");
					while (posend!=-1) {
						ids.add(s.substring(posstart, posend));
						posstart = posend+1;
						posend = s.indexOf(";", posstart);
					}
					ids.add(s.substring(posstart));
					if (q instanceof QuestionOC) {
						QuestionOC qc = (QuestionOC) q;
						AnswerChoice choice = (AnswerChoice) qc.getAnswer(theCase,
								ids.get(0));
						theCase.setValue(qc, new ChoiceValue(choice));
					} else {
						//TODO QuestionNum, QuestionDate
					}
				}
			}
		} finally {
			theCase.getPropagationContoller().commitPropagation();
		}
	}

	/**
	 * Indicates whether the problemsolver contributes to XPSCase.getDiagnoses(DiangosisState)
	 * Creation date: (21.02.2002 16:51:10)
	 * @return false
	 */
	public boolean isContributingToResult() {
		return false;
	}

	/**
	 * @see PSMethod
	 */
	public void propagate(Session theCase, Collection<PropagationEntry> changes) {
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		// because there should be only one init value for an object
		// we simply deliver the first fact as the result
		return facts[0];
	}
}
/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.core.inference;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;

/**
 * This is a 'marker' psmethod to represent all the initial values. Especially
 * used to add the initQASets to the QASetManager Creation date: (21.02.2002
 * 16:51:10)
 * 
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
	 * Some space for initial methods of a PSMethod. Creation date: (21.02.2002
	 * 16:51:10)
	 */
	@Override
	public void init(Session session) {
		session.getPropagationManager().openPropagation();
		try {
			// initialise all questions
			KnowledgeBase kb = session.getKnowledgeBase();
			for (Question q : kb.getQuestions()) {
				String property = q.getInfoStore().getValue(BasicProperties.INIT);
				if (property != null) {
					String s = property;
					List<String> ids = new LinkedList<String>();
					int posstart = 0;
					int posend = s.indexOf(';');
					while (posend != -1) {
						ids.add(s.substring(posstart, posend));
						posstart = posend + 1;
						posend = s.indexOf(';', posstart);
					}
					ids.add(s.substring(posstart));
					if (q instanceof QuestionOC) {
						QuestionOC qc = (QuestionOC) q;
						Choice choice = KnowledgeBaseManagement.createInstance(kb).findChoice(qc,
								ids.get(0));

						Fact fact = FactFactory.createFact(session, qc, new ChoiceValue(choice),
								new Object(), this);

						session.getBlackboard().addValueFact(fact);
					}
					else if (q instanceof QuestionNum) {
						QuestionNum qn = (QuestionNum) q;
						NumValue value;
						try {
							value = new NumValue(Double.parseDouble(property));
							Fact fact = FactFactory.createFact(session, qn,
									value, this, this);
							session.getBlackboard().addValueFact(fact);
						}
						catch (NumberFormatException e) {
							// TODO: parse formulas for QuestionNum
						}
					}
					// handle QuestionDate
				}
			}
		}
		finally {
			session.getPropagationManager().commitPropagation();
		}
	}

	/**
	 * @see PSMethod
	 */
	@Override
	public void propagate(Session session, Collection<PropagationEntry> changes) {
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		// because there should be only one init value for an object
		// we simply deliver the first fact as the result
		return facts[0];
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.problem;
	}
}
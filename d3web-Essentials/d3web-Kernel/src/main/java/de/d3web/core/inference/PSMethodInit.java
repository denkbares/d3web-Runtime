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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionZC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValueUtils;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.Unknown;
import com.denkbares.utils.Log;

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

			// init default indications
			List<? extends QASet> initQuestions = session.getKnowledgeBase().getInitQuestions();
			double index = -1.0;
			for (QASet object : initQuestions) {
				Fact fact = FactFactory.createIndicationFact(
						object, new Indication(Indication.State.INDICATED, index), this, this);
				session.getBlackboard().addInterviewFact(fact);
				index += Double.MIN_VALUE;
			}

			// initialise all questions
			KnowledgeBase kb = session.getKnowledgeBase();
			for (Question q : kb.getManager().getQuestions()) {
				String property = q.getInfoStore().getValue(BasicProperties.INIT);
				if (property != null) {
					try {
						Value value = getValue(q, property);
						Fact fact = FactFactory.createFact(q, value, new Object(),
								this);
						session.getBlackboard().addValueFact(fact);
					}
					catch (IllegalArgumentException e) {
						Log.warning(e.getMessage());
					}
				}
			}
		}
		finally {
			session.getPropagationManager().commitPropagation();
		}
	}

	/**
	 * Returns the value represented by the string for the given question.
	 * 
	 * @created 20.09.2011
	 * @param q {@link Question}
	 * @param property String of the init property
	 * @return Value representing the string
	 * @throws IllegalArgumentException it the property string is not correct
	 *         for the given question
	 */
	public static Value getValue(Question q, String property) throws IllegalArgumentException {
		if (property.equalsIgnoreCase("unknown")) {
			return Unknown.getInstance();
		}
		List<String> ids = new LinkedList<>();
		int posstart = 0;
		int posend = property.indexOf(';');
		while (posend != -1) {
			ids.add(property.substring(posstart, posend));
			posstart = posend + 1;
			posend = property.indexOf(';', posstart);
		}
		ids.add(property.substring(posstart));
		if (q instanceof QuestionZC) {
			throw new IllegalArgumentException("Cannot set initial value '" + property +
					"' for question '" + q.getName()
					+ "'. No Choice for this question type allowed.");
		}
		else if (q instanceof QuestionOC) {
			QuestionOC qc = (QuestionOC) q;
			String choiceID = ids.get(0);
			Choice choice =
					KnowledgeBaseUtils.findChoice(qc, choiceID);
			if (choice != null) {
				return new ChoiceValue(choice);
			}
			else {
				throw new IllegalArgumentException("Cannot set initial value '" + property +
						"' for question '" + q.getName()
						+ "'. Choice not found.");
			}
		}
		else if (q instanceof QuestionMC) {
			QuestionMC qmc = (QuestionMC) q;
			List<ChoiceID> choices = new LinkedList<>();
			List<String> badIds = new LinkedList<>();
			for (String id : ids) {
				Choice choice = KnowledgeBaseUtils.findChoice(qmc, id);
				if (choice != null) {
					choices.add(new ChoiceID(choice));
				}
				else {
					badIds.add(id);
				}
			}
			if (badIds.isEmpty()) {
				return new MultipleChoiceValue(choices.toArray(new ChoiceID[choices.size()]));
			}
			else {
				throw new IllegalArgumentException("Cannot set initial value '" + property +
						"' for question '" + q.getName()
						+ "'. The following choices could not be found: " + badIds);
			}
		}
		else if (q instanceof QuestionNum) {
			try {
				return new NumValue(Double.parseDouble(property));
			}
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("Cannot set initial value '" + property +
						"' for question '" + q.getName()
						+ "', because it is not valid number.");
			}
		}
		else if (q instanceof QuestionDate) {
			try {
				return ValueUtils.createDateValue((QuestionDate) q, property);
			}
			// throw more detailed error message
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Cannot set initial value '" + property +
						"' for question '" + q.getName()
						+ "', because it is not valid date format.");
			}
		}
		else if (q instanceof QuestionText) {
			return new TextValue(property);
		}
		throw new IllegalArgumentException("QuestionType not supported yet.");
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

	@Override
	public double getPriority() {
		// a very low priority because
		return 10;
	}

	@Override
	public Set<TerminologyObject> getPotentialDerivationSources(TerminologyObject derivedObject) {
		return Collections.emptySet();
	}

	@Override
	public Set<TerminologyObject> getActiveDerivationSources(TerminologyObject derivedObject, Session session) {
		return Collections.emptySet();
	}
}
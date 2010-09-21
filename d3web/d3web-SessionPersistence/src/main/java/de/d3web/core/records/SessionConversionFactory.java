/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.core.records;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethod.Type;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.protocol.Protocol;
import de.d3web.core.session.protocol.ProtocolEntry;
import de.d3web.core.session.values.UndefinedValue;

/**
 * Factory to create a Session out of a SessionRecord and vice versa.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class SessionConversionFactory {

	public static Session copyToSession(SessionRecord source) throws IOException {
		Session target = SessionFactory.createSession(source.getKb());
		target.setDCMarkup(source.getDCMarkup());
		Protocol protocol = source.getProtocol();
		if (protocol != null) {
			for (ProtocolEntry entry : protocol.getProtocolHistory()) {
				target.getProtocol().addEntry(entry.getFact());
			}
		}

		// Search psmethods of session (improves performance)
		Map<String, PSMethod> psMethods = new HashMap<String, PSMethod>();
		for (PSMethod psm : target.getPSMethods()) {
			psMethods.put(psm.getClass().toString(), psm);
		}
		target.getPropagationManager().openPropagation();
		try {
			for (FactRecord factRecord : source.getFacts()) {
				PSMethod psMethod = psMethods.get(factRecord.getPsm());
				if (psMethod != null) {
					if (psMethod.hasType(Type.source)) {
						target.getBlackboard().addValueFact(
								new DefaultFact(factRecord.getObject(), factRecord.getValue(),
										psMethod,
										psMethod));
					}
				}
				else {
					throw new IOException("Problemsolver " + factRecord.getPsm()
							+ " not found in Session.");
				}
			}
		}
		finally {
			target.getPropagationManager().commitPropagation();
		}
		return target;
	}

	public static SessionRecord copyToSessionRecord(Session source) {
		// TODO: Set correct dates
		SessionRecord target = new DefaultSessionRecord(source.getId(), source.getKnowledgeBase(),
				new Date(), new Date());
		target.setDCMarkup(source.getDCMarkup());
		target.setProtocol(source.getProtocol());
		Blackboard blackboard = source.getBlackboard();
		List<PSMethod> problemsolvingpsmethods = new LinkedList<PSMethod>();
		List<PSMethod> strategicpsmethods = new LinkedList<PSMethod>();
		for (PSMethod psm : source.getPSMethods()) {
			if (psm.hasType(Type.problem) || psm.hasType(Type.source)) {
				problemsolvingpsmethods.add(psm);
			}
			if (psm.hasType(Type.strategic) || psm.hasType(Type.source)) {
				strategicpsmethods.add(psm);
			}
		}
		for (Question q : blackboard.getValuedQuestions()) {
			int countpsm = 0;
			for (PSMethod psm : problemsolvingpsmethods) {
				Value value = blackboard.getValue(q, psm);
				if (UndefinedValue.isNotUndefinedValue(value)) {
					target.addFact(new FactRecord(q, psm.getClass().toString(), value));
					countpsm++;
				}
			}
			// if more than one psm has set a value, add the globally merged
			// fact
			if (countpsm > 1) {
				Fact valueFact = blackboard.getValueFact(q);
				PSMethod psm = valueFact.getPSMethod();
				target.addFact(new FactRecord(q, psm.getClass().toString(), valueFact.getValue()));
			}
		}
		for (Solution s : blackboard.getValuedSolutions()) {
			int countpsm = 0;
			for (PSMethod psm : problemsolvingpsmethods) {
				Rating rating = blackboard.getRating(s, psm);
				if (rating.hasState(State.UNCLEAR)) {
					target.addFact(new FactRecord(s, psm.getClass().toString(), rating));
					countpsm++;
				}
			}
			if (countpsm > 1) {
				Fact valueFact = blackboard.getValueFact(s);
				PSMethod psm = valueFact.getPSMethod();
				Value value = valueFact.getValue();
				target.addFact(new FactRecord(s, psm.getClass().toString(), value));
			}
		}
		for (TerminologyObject object : blackboard.getInterviewObjects()) {
			int countpsm = 0;
			for (PSMethod psm : strategicpsmethods) {
				Indication indication = blackboard.getIndication((InterviewObject) object, psm);
				if (indication.hasState(Indication.State.NEUTRAL)) {
					target.addFact(new FactRecord(object, psm.getClass().toString(), indication));
					countpsm++;
				}
			}
			if (countpsm > 1) {
				Fact interviewFact = blackboard.getInterviewFact(object);
				Value value = interviewFact.getValue();
				PSMethod psMethod = interviewFact.getPSMethod();
				target.addFact(new FactRecord(object, psMethod.getClass().toString(), value));
			}

		}
		return target;
	}

}

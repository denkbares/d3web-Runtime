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
		Session target = SessionFactory.createSession(source.getId(), source.getKnowledgeBase(),
				source.getCreationDate());
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
			List<Fact> valueFacts = new LinkedList<Fact>();
			List<Fact> interviewFacts = new LinkedList<Fact>();
			getFacts(source.getValueFacts(), psMethods, valueFacts);
			getFacts(source.getInterviewFacts(), psMethods, interviewFacts);
			for (Fact fact : valueFacts) {
				target.getBlackboard().addValueFact(fact);
			}
			for (Fact fact : interviewFacts) {
				target.getBlackboard().addInterviewFact(fact);
			}
		}
		finally {
			target.getPropagationManager().commitPropagation();
		}
		// this must be the last operation to overwrite all touches within
		// propagation
		target.touch(source.getLastChangeDate());
		return target;
	}

	private static void getFacts(List<FactRecord> factRecords, Map<String, PSMethod> psMethods, List<Fact> valueFacts) throws IOException {
		for (FactRecord factRecord : factRecords) {
			String psm = factRecord.getPsm();
			if (psm != null) {
				PSMethod psMethod = psMethods.get(psm);
				if (psMethod != null) {
					if (psMethod.hasType(Type.source)) {
						Value value = factRecord.getValue();
						valueFacts.add(new DefaultFact(factRecord.getObject(), value, psMethod,
								psMethod));
					}
				}
				else {
					throw new IOException("Problemsolver " + psm
							+ " not found in Session.");
				}
			}
		}
	}

	public static SessionRecord copyToSessionRecord(Session source) {
		DefaultSessionRecord target = new DefaultSessionRecord(source.getId(),
				source.getKnowledgeBase(),
				source.getCreationDate(), source.getLastChangeDate());
		target.setDCMarkup(source.getDCMarkup());
		target.setProtocol(source.getProtocol());
		Blackboard blackboard = source.getBlackboard();
		for (Question q : blackboard.getValuedQuestions()) {
			List<PSMethod> contributingPSMethods = blackboard.getContributingPSMethods(q);
			for (PSMethod psm : contributingPSMethods) {
				Value value = blackboard.getValue(q, psm);
				if (UndefinedValue.isNotUndefinedValue(value)) {
					target.addValueFact(new FactRecord(q, psm.getClass().toString(), value));
				}
			}
			// if more than one psm has set a value, add the globally merged
			// fact
			if (contributingPSMethods.size() > 1) {
				Fact valueFact = blackboard.getValueFact(q);
				target.addValueFact(new FactRecord(q, null, valueFact.getValue()));
			}
		}
		for (Solution s : blackboard.getValuedSolutions()) {
			List<PSMethod> contributingPSMethods = blackboard.getContributingPSMethods(s);
			for (PSMethod psm : contributingPSMethods) {
				Rating rating = blackboard.getRating(s, psm);
				if (!rating.hasState(State.UNCLEAR)) {
					target.addValueFact(new FactRecord(s, psm.getClass().toString(), rating));
				}
			}
			if (contributingPSMethods.size() > 1) {
				Fact valueFact = blackboard.getValueFact(s);
				Value value = valueFact.getValue();
				target.addValueFact(new FactRecord(s, null, value));
			}
		}
		for (TerminologyObject object : blackboard.getInterviewObjects()) {
			List<PSMethod> indicatingPSMethods = blackboard.getIndicatingPSMethods(object);
			for (PSMethod psm : indicatingPSMethods) {
				Indication indication = blackboard.getIndication((InterviewObject) object, psm);
				if (!indication.hasState(Indication.State.NEUTRAL)) {
					target.addInterviewFact(new FactRecord(object, psm.getClass().toString(),
							indication));
				}
			}
			if (indicatingPSMethods.size() > 1) {
				Fact interviewFact = blackboard.getInterviewFact(object);
				Value value = interviewFact.getValue();
				target.addInterviewFact(new FactRecord(object, null, value));
			}

		}
		return target;
	}

}
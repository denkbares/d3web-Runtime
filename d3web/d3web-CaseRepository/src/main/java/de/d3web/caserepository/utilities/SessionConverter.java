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

package de.d3web.caserepository.utilities;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.knowledge.terminology.info.PropertiesCloner;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.interviewmanager.DialogProxy;
import de.d3web.core.session.interviewmanager.ShadowMemory;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * Converter class for Case objects. It converts Session to CaseObject and
 * CaseObject (XMLImport) to Session
 * 
 * @author gbuscher, bruememr
 */
public class SessionConverter {

	private static SessionConverter instance = null;

	public static SessionConverter getInstance() {
		if (instance == null) {
			instance = new SessionConverter();
		}
		return instance;
	}

	private SessionConverter() {
		additionalCaseConverters = new LinkedList<AdditionalCaseConverter>();
	}

	private List<AdditionalCaseConverter> additionalCaseConverters = null;

	public void addAdditionalConverter(AdditionalCaseConverter converter) {
		additionalCaseConverters.add(converter);
	}

	/**
	 * Converts CaseObject to Session using the given dialog-controller-class.
	 * The registered (user-selected) containers are considered. DCMarkup and
	 * Properties of the caseObject will be cloned and added to the returned
	 * session.
	 * 
	 * @param dialogControllerClass the dialog-controller to use
	 * @return CaseObject
	 */
	public Session caseObject2Session(CaseObject cobj, KnowledgeBase kb,
			List<PSMethod> usedPSMethods) {
		return caseObject2Session(cobj, kb, usedPSMethods, true,
				true);
	}

	/**
	 * Converts CaseObject to Session using the given dialog-controller-class.
	 * The registered (user-selected) containers are considered.
	 * 
	 * @param dialogControllerClass the dialog-controller to use
	 * @param copyDCMarkup if true, DCMarkup of the caseObject will be cloned
	 *        and added to the session
	 * @param copyProperties if true, Properties of the caseObject will be
	 *        cloned and added to the session
	 * @return CaseObject
	 */
	public Session caseObject2Session(CaseObject cobj, KnowledgeBase kb,
			List<PSMethod> usedPSMethods, boolean copyDCMarkup,
			boolean copyProperties) {
		DialogProxy proxy = new DialogProxy();
		ShadowMemory shmem = new ShadowMemory();
		shmem.setPriority(1);
		proxy.addClient(shmem);

		Iterator<Question> qiter = cobj.getQuestions().iterator();
		while (qiter.hasNext()) {
			Question q = qiter.next();
			shmem.addAnswers(q.getId(), cobj.getValue(q));
		}

		List<QContainer> registeredContainers = new LinkedList<QContainer>();
		Iterator<?> contIter = cobj.getAppliedQSets().getAllApplied().iterator();
		while (contIter.hasNext()) {
			QContainer qcontainer = (QContainer) contIter.next();
			registeredContainers.add(qcontainer);
		}

		Session ret = SessionFactory.createAnsweredSession(kb,
				proxy,
				registeredContainers, usedPSMethods);

		// user-selected diagnoses
		Iterator<CaseObject.Solution> solIter = cobj.getSolutions(
				PSMethodUserSelected.class).iterator();
		while (solIter.hasNext()) {
			CaseObject.Solution sol = solIter.next();
			ret.getBlackboard().addValueFact(
					new DefaultFact(sol.getDiagnosis(), sol.getState(), new Object(),
					PSMethodUserSelected.getInstance()));
		}

		if (copyDCMarkup) {
			ret.setDCMarkup((DCMarkup) cobj.getDCMarkup().clone());
		}
		if (copyProperties) {
			ret.setProperties(PropertiesCloner.getInstance().cloneProperties(
					cobj.getProperties()));
		}

		Iterator<AdditionalCaseConverter> iter = additionalCaseConverters.iterator();
		while (iter.hasNext()) {
			AdditionalCaseConverter conv = iter.next();
			conv.caseObject2Session(cobj, ret);
		}

		return ret;
	}

	/**
	 * Converts Session to CaseObject. The returned CaseObject has empty
	 * IMetaData. The established system's diagnoses are stored within the
	 * CaseObject. DCMarkup and Properties of the session will be cloned and
	 * added to the returned caseObject.
	 * 
	 * @return CaseObjectImpl
	 */
	public CaseObjectImpl session2CaseObject(Session theCase) {
		return session2CaseObject(theCase, true, true);
	}

	/**
	 * Converts Session to CaseObject. The returned CaseObject has empty
	 * IMetaData. The established system's diagnoses are stored within the
	 * CaseObject.
	 * 
	 * @param copyDCMarkup if true, DCMarkup of the session will be cloned and
	 *        added to the caseObject
	 * @param copyProperties if true, Properties of the session will be cloned
	 *        and added to the caseObject
	 * @return CaseObjectImpl
	 */
	public CaseObjectImpl session2CaseObject(Session theCase, boolean copyDCMarkup,
			boolean copyProperties) {
		CaseObjectImpl ret = new CaseObjectImpl(theCase.getKnowledgeBase());

		// Questions

		Iterator<Question> qiter = theCase.getKnowledgeBase().getQuestions().iterator();
		while (qiter.hasNext()) {
			Question q = qiter.next();
			Value value = theCase.getBlackboard().getValue(q);
			ret.addQuestionAndAnswers(q, value);
		}

		// processed QContainers

		// first, empty the whole collection
		ret.getAppliedQSets().clearAllApplied();

		// // then add the containers from which the qaSetManager has taken
		// // protocol of
		// Iterator<?> citer =
		// theCase.getQASetManager().getProcessedContainers().iterator();
		// while (citer.hasNext()) {
		// ret.getAppliedQSets().setApplied((QContainer) citer.next());
		// }

		// Diagnoses
		addDiagnosesToSolutions(ret, theCase, State.ESTABLISHED);
		addDiagnosesToSolutions(ret, theCase, State.SUGGESTED);
		addDiagnosesToSolutions(ret, theCase, State.EXCLUDED);

		if (copyDCMarkup) {
			ret.setDCMarkup((DCMarkup) theCase.getDCMarkup().clone());
		}
		if (copyProperties) {
			ret.setProperties(PropertiesCloner.getInstance().cloneProperties(
					theCase.getProperties()));
		}
		Iterator<AdditionalCaseConverter> iter = additionalCaseConverters.iterator();
		while (iter.hasNext()) {
			AdditionalCaseConverter conv = iter.next();
			conv.session2CaseObject(theCase, ret);
		}
		return ret;
	}

	/**
	 * Adds all solutions of the given Session with the given DiagnosisState to
	 * "co".
	 */
	private void addDiagnosesToSolutions(CaseObjectImpl co, Session theCase, Rating.State state) {
		List<PSMethod> usedPsm = new LinkedList<PSMethod>();
		Iterator<? extends PSMethod> usedPsmIter = theCase.getPSMethods().iterator();
		while (usedPsmIter.hasNext()) {
			PSMethod psm = usedPsmIter.next();
			if (psm.isContributingToResult()) {
				usedPsm.add(psm);
			}
		}

		List<de.d3web.core.knowledge.terminology.Solution> diags = theCase.getBlackboard().getSolutions(
				state);
		if (diags != null) {
			Iterator<de.d3web.core.knowledge.terminology.Solution> diter = diags.iterator();
			while (diter.hasNext()) {
				de.d3web.core.knowledge.terminology.Solution d = diter.next();

				Iterator<PSMethod> psMethodIter = usedPsm.iterator();
				while (psMethodIter.hasNext()) {
					PSMethod psm = psMethodIter.next();
					Rating ds = theCase.getBlackboard().getRating(d, psm);
					if (ds.hasState(state)) {
						CaseObject.Solution s = new CaseObject.Solution();
						s.setDiagnosis(d);
						s.setState(new Rating(state));
						s.setPSMethodClass(psm.getClass());
						co.addSolution(s);
					}
				}
			}
		}
	}

}
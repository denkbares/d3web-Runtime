package de.d3web.caserepository.utilities;

import java.util.*;

import de.d3web.caserepository.*;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.dialogControl.proxy.*;
import de.d3web.kernel.domainModel.*;
import de.d3web.kernel.domainModel.qasets.*;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.userSelected.PSMethodUserSelected;
import de.d3web.kernel.supportknowledge.*;

/**
 * Converter class for Case objects. It converts XPSCase to CaseObject and
 * CaseObject (XMLImport) to XPSCase
 * 
 * @author gbuscher, bruememr
 */
public class CaseConverter {

	private static CaseConverter instance = null;
	public static CaseConverter getInstance() {
		if (instance == null) {
			instance = new CaseConverter();
		}
		return instance;
	}
	private CaseConverter() {
		additionalCaseConverters = new LinkedList();
	}

	private List additionalCaseConverters = null;

	public void addAdditionalConverter(AdditionalCaseConverter converter) {
		additionalCaseConverters.add(converter);
	}

	/**
	 * Converts CaseObject to XPSCase using the given dialog-controller-class.
	 * The registered (user-selected) containers are considered. DCMarkup and
	 * Properties of the caseObject will be cloned and added to the returned
	 * xpsCase.
	 * 
	 * @param dialogControllerClass
	 *            the dialog-controller to use
	 * @return CaseObject
	 */
	public XPSCase caseObject2XPSCase(CaseObject cobj, KnowledgeBase kb,
			Class dialogControllerClass, List usedPSMethods) {
		return caseObject2XPSCase(cobj, kb, dialogControllerClass, usedPSMethods, true, true);
	}

	/**
	 * Converts CaseObject to XPSCase using the given dialog-controller-class.
	 * The registered (user-selected) containers are considered.
	 * 
	 * @param dialogControllerClass
	 *            the dialog-controller to use
	 * @param copyDCMarkup
	 *            if true, DCMarkup of the caseObject will be cloned and added
	 *            to the xpsCase
	 * @param copyProperties
	 *            if true, Properties of the caseObject will be cloned and added
	 *            to the xpsCase
	 * @return CaseObject
	 */
	public XPSCase caseObject2XPSCase(CaseObject cobj, KnowledgeBase kb,
			Class dialogControllerClass, List usedPSMethods, boolean copyDCMarkup,
			boolean copyProperties) {
		DialogProxy proxy = new DialogProxy();
		ShadowMemory shmem = new ShadowMemory();
		shmem.setPriority(1);
		proxy.addClient(shmem);

		Iterator qiter = cobj.getQuestions().iterator();
		while (qiter.hasNext()) {
			Question q = (Question) qiter.next();
			shmem.addAnswers(q.getId(), cobj.getAnswers(q));
		}

		List registeredContainers = new LinkedList();
		Iterator contIter = cobj.getAppliedQSets().getAllApplied().iterator();
		while (contIter.hasNext()) {
			QContainer qcontainer = (QContainer) contIter.next();
			registeredContainers.add(qcontainer);
		}

		XPSCase ret = CaseFactory.createAnsweredXPSCase(kb, dialogControllerClass, proxy,
				registeredContainers, usedPSMethods);

		// user-selected diagnoses
		Iterator solIter = cobj.getSolutions(PSMethodUserSelected.class).iterator();
		while (solIter.hasNext()) {
			CaseObject.Solution sol = (CaseObject.Solution) solIter.next();
			sol.getDiagnosis().setValue(ret, new Object[]{sol.getState()},
					PSMethodUserSelected.class);
		}

		if (copyDCMarkup) {
			ret.setDCDMarkup((DCMarkup) cobj.getDCMarkup().clone());
		}
		if (copyProperties) {
			ret.setProperties(PropertiesCloner.getInstance().cloneProperties(cobj.getProperties()));
		}

		Iterator iter = additionalCaseConverters.iterator();
		while (iter.hasNext()) {
			AdditionalCaseConverter conv = (AdditionalCaseConverter) iter.next();
			conv.caseObject2XPSCase(cobj, ret);
		}

		return ret;
	}

	/**
	 * Converts XPSCase to CaseObject. The returned CaseObject has empty
	 * IMetaData. The established system's diagnoses are stored within the
	 * CaseObject. DCMarkup and Properties of the xpsCase will be cloned and
	 * added to the returned caseObject.
	 * 
	 * @return CaseObjectImpl
	 */
	public CaseObjectImpl xpsCase2CaseObject(XPSCase theCase) {
		return xpsCase2CaseObject(theCase, true, true);
	}

	/**
	 * Converts XPSCase to CaseObject. The returned CaseObject has empty
	 * IMetaData. The established system's diagnoses are stored within the
	 * CaseObject.
	 * 
	 * @param copyDCMarkup
	 *            if true, DCMarkup of the xpsCase will be cloned and added to
	 *            the caseObject
	 * @param copyProperties
	 *            if true, Properties of the xpsCase will be cloned and added to
	 *            the caseObject
	 * @return CaseObjectImpl
	 */
	public CaseObjectImpl xpsCase2CaseObject(XPSCase theCase, boolean copyDCMarkup,
			boolean copyProperties) {
		CaseObjectImpl ret = new CaseObjectImpl(theCase.getKnowledgeBase());

		// Questions

		Iterator qiter = theCase.getQuestions().iterator();
		while (qiter.hasNext()) {
			Question q = (Question) qiter.next();
			List value = q.getValue(theCase);
			if ((value != null) && (!value.isEmpty())) {
				ret.addQuestionAndAnswers(q, value);
			}
		}

		// processed QContainers

		// first, empty the whole collection
		ret.getAppliedQSets().clearAllApplied();

		// then add the containers from which the qaSetManager has taken
		// protocol of
		Iterator citer = theCase.getQASetManager().getProcessedContainers().iterator();
		while (citer.hasNext()) {
			ret.getAppliedQSets().setApplied((QContainer) citer.next());
		}

		// Diagnoses
		addDiagnosesToSolutions(ret, theCase, DiagnosisState.ESTABLISHED);
		addDiagnosesToSolutions(ret, theCase, DiagnosisState.SUGGESTED);
		addDiagnosesToSolutions(ret, theCase, DiagnosisState.EXCLUDED);

		if (copyDCMarkup) {
			ret.setDCDMarkup((DCMarkup) theCase.getDCMarkup().clone());
		}
		if (copyProperties) {
			ret.setProperties(PropertiesCloner.getInstance().cloneProperties(
					theCase.getProperties()));
		}
		Iterator iter = additionalCaseConverters.iterator();
		while (iter.hasNext()) {
			AdditionalCaseConverter conv = (AdditionalCaseConverter) iter.next();
			conv.xpsCase2CaseObject(theCase, ret);
		}
		return ret;
	}

	/**
	 * Adds all solutions of the given XPSCase with the given DiagnosisState to
	 * "co".
	 */
	private void addDiagnosesToSolutions(CaseObjectImpl co, XPSCase theCase, DiagnosisState state) {
		List usedPsm = new LinkedList();
		Iterator usedPsmIter = theCase.getUsedPSMethods().iterator();
		while (usedPsmIter.hasNext()) {
			PSMethod psm = (PSMethod) usedPsmIter.next();
			if (psm.isContributingToResult()) {
				usedPsm.add(psm);
			}
		}

		List diags = theCase.getDiagnoses(state);
		if (diags != null) {
			Iterator diter = diags.iterator();
			while (diter.hasNext()) {
				Diagnosis d = (Diagnosis) diter.next();

				Iterator psMethodIter = usedPsm.iterator();
				while (psMethodIter.hasNext()) {
					PSMethod psm = (PSMethod) psMethodIter.next();
					if (d.getState(theCase, psm.getClass()).equals(state)) {
						CaseObject.Solution s = new CaseObject.Solution();
						s.setDiagnosis(d);
						s.setState(state);
						s.setPSMethodClass(psm.getClass());
						co.addSolution(s);
					}
				}
			}
		}
	}

}
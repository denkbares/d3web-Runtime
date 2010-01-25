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

package de.d3web.kernel.domainModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.dialogControl.QASetManager;
import de.d3web.kernel.dialogControl.QASetManagerFactory;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.dynamicObjects.XPSCaseObject;
import de.d3web.kernel.psMethods.DefaultPropagationController;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.PSMethodInit;
import de.d3web.kernel.psMethods.PropagationContoller;
import de.d3web.kernel.psMethods.contraIndication.PSMethodContraIndication;
import de.d3web.kernel.psMethods.diaFlux.FluxSolver;
import de.d3web.kernel.psMethods.dialogControlling.PSMethodDialogControlling;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;
import de.d3web.kernel.psMethods.nextQASet.PSMethodNextQASet;
import de.d3web.kernel.psMethods.parentQASet.PSMethodParentQASet;
import de.d3web.kernel.psMethods.questionSetter.PSMethodQuestionSetter;
import de.d3web.kernel.psMethods.suppressAnswer.PSMethodSuppressAnswer;
import de.d3web.kernel.psMethods.userSelected.PSMethodUserSelected;
import de.d3web.kernel.psMethods.xclPattern.PSMethodXCL;
import de.d3web.kernel.supportknowledge.DCMarkup;
import de.d3web.kernel.supportknowledge.Properties;

/**
 * Central class <BR>
 * The XPSCase is the facade for all activities handled by the dialog and
 * associated problem-solvers. <BR>
 * All actions have to be stated through the XPSCase (especially the setValue
 * operations for knowledge base objects!) <BR>
 * The XPSCase always knows the state of the <BR>
 * <LI>problem-solvers (which are used) <LI>questions (answerd, to be answerd),
 * <LI>diagnoses (state with respect to the problem-solvers) <br>
 * It's important to set values through this facade and not directly via the
 * knowledge base objects, because XPSCase is responsible for the propagation
 * mechanism of the connected problem-solvers.
 * 
 * @author Christian Betz, joba
 * @see XPSCaseObject
 */
public class D3WebCase implements XPSCase {

	private final KnowledgeBase kb;
	private final DefaultPropagationController propagationController;
	private Map<CaseObjectSource, XPSCaseObject> dynamicStore;

	private List<Diagnosis> establishedDiagnoses = new LinkedList<Diagnosis>();
	private List<Question> answeredQuestions = new LinkedList<Question>();

	private List<PSMethod> usedPSMethods;

	private Set<Class<? extends KnowledgeSlice>> finishReasons;
	// private boolean finished = false;

	private QASetManager qaSetManager = null;
	private QASetManagerFactory qamFactory = null;

	private List<QContainer> indicatedQContainers = new LinkedList<QContainer>();

	public static boolean TRACE = false;
	private Collection<PSMethod> dialogControllingPSMethods;

	private DCMarkup dcMarkup;
	private Properties properties;

	private static LinkedList<PSMethod> commonPSMethods = new LinkedList<PSMethod>(
			Arrays.asList(
			PSMethodUserSelected.getInstance(),
			new PSMethodDialogControlling(),
			PSMethodContraIndication.getInstance(),
			PSMethodNextQASet.getInstance(),
			PSMethodQuestionSetter.getInstance(),
			PSMethodSuppressAnswer.getInstance(),
			PSMethodHeuristic.getInstance(),
			PSMethodInit.getInstance(),
			PSMethodParentQASet.getInstance(),
			PSMethodXCL.getInstance(),
			FluxSolver.getInstance()));

	/**
	 * Adds a {@link PSMethod} instance to the list of default PSMethod when
	 * creating a new case.
	 * 
	 * @param method
	 *            the PSMethod to be added
	 */
	public static void addCommonPSMethod(PSMethod method) {
		commonPSMethods.add(method);
	}

	/**
	 * Adds a {@link PSMethod} instance to the list of default PSMethod when
	 * creating a new case. The index is the position of the property
	 * commonPSMethods where to add the PSMethod, and therefore the priority.
	 * 
	 * @param method
	 *            the PSMethod to be added
	 */
	public static void addCommonPSMethod(PSMethod method, int index) {
		commonPSMethods.add(index, method);
	}
	
	/**
	 * Returns the current common PSMethods. These PSMethods will be added 
	 * to a newly created case as default PSMethods.
	 * 
	 * @return the current common (default) PSMethods
	 */
	public PSMethod[] getCommonPSMethods() {
		return commonPSMethods.toArray(new PSMethod[commonPSMethods.size()]);
	}

	/**
	 * Creates a new user case with the specified knowledge base. <br>
	 * The default problem-solvers for each case are listed in static array
	 * <code>commonPSMethods</code>. See class comment for further details.
	 */
	D3WebCase(KnowledgeBase kb, QASetManagerFactory theQamFactory) {
		this.kb = kb;
		this.propagationController = new DefaultPropagationController(this);

		setQASetManagerFactory(theQamFactory);

		properties = new Properties();
		dcMarkup = new DCMarkup();
		finishReasons = new HashSet<Class<? extends KnowledgeSlice>>();

		dynamicStore = new HashMap<CaseObjectSource, XPSCaseObject>();

		// add problem-solving methods used for this case
		usedPSMethods = new LinkedList<PSMethod>();
		dialogControllingPSMethods = new LinkedList<PSMethod>();

		PSMethodUserSelected psmUser = PSMethodUserSelected.getInstance();

		dialogControllingPSMethods.add(psmUser);
		dialogControllingPSMethods.add(PSMethodHeuristic.getInstance());

		// register some common problem solving methods
		// first add the methods
		for (PSMethod method : commonPSMethods) {
			addUsedPSMethod(method);
		}

		// activate InitQASets
		for (QASet qaSet : getKnowledgeBase().getInitQuestions()) {
			// //
			// // das ist irgendwie doppelt gemoppelt. Nachschauen, ob das
			// wirklich so gebraucht wird!!
			// //
			// addQASet(nextQASet, null, PSMethodInit.class);
			qaSet.activate(this, null, PSMethodInit.class);
		}
		trace("\n------------------------------------------------\nNeuer Fall\n");
	}

	private String id = null;

	/*
	 * @see de.d3web.kernel.domainModel.IDReference#getId()
	 */
	public String getId() {
		if (id == null) {
			id = createNewCaseId();
		}
		return id;
	}

	private String createNewCaseId() {
		return UUID.randomUUID().toString();
	}

	private synchronized void addAnsweredQuestions(Question frage) {
		if (!getAnsweredQuestions().contains(frage)) {
			getAnsweredQuestions().add(frage);
		}
	}

	/**
	 * adds an established Diagnosis to this case
	 */
	public void addEstablishedDiagnoses(Diagnosis diag) {
		if (!establishedDiagnoses.contains(diag)) {
			establishedDiagnoses.add(diag);
		}
	}

	/**
	 * adds a QASet to this case
	 */
	public void addQASet(QASet qaset, Rule rule, Class<? extends PSMethod> psm) {
		getQASetManager().propagate(qaset, rule, getPSMethodInstance(psm));
	}

	/**
	 * Adds a new PSMethod to the used PSMethods of this case. Creation date:
	 * (28.08.00 17:33:43)
	 * 
	 * @param newUsedPSMethods
	 *            java.util.List
	 */
	public void addUsedPSMethod(PSMethod psmethod) {
		if (getUsedPSMethods().contains(psmethod)) return;

		this.usedPSMethods.add(psmethod);
		psmethod.init(this);

		PropagationContoller propagationContoller = getPropagationContoller();
		propagationContoller.openPropagation();
		try {
			for (Question question : this.getAnsweredQuestions()) {
				Object[] oldValue = new Object[0];
				Object[] newValue = getValue(question, null);
				propagationContoller.propagate(question, oldValue, newValue, psmethod);
			}
			// TODO: das ist so viel zu aufwendig, wenn viele Lösungen sind. Man
			// bräuchte eine Liste der bewerteten Lösungen im Fall (analog
			// beantwortete Fragen)
			/*
			for (Diagnosis diagnosis : this.getDiagnoses()) {
				if (DiagnosisState.UNCLEAR.equals(diagnosis.getState(this))) continue;
				Object[] oldValue = new Object[] { DiagnosisState.UNCLEAR };
				Object[] newValue = getValue(diagnosis, null);
				propagationContoller.propagate(diagnosis, oldValue, newValue, psmethod);
			}
			*/
		}
		finally {
			propagationContoller.commitPropagation();
		}
	}

	/**
	 * @return a list of answered questions in this case
	 */
	public List<Question> getAnsweredQuestions() {
		return answeredQuestions;
	}

	public List<QContainer> getIndicatedQContainers() {
		return indicatedQContainers;
	}

	/**
	 * @return the unique and dynamic user case object for the specifed (static)
	 *         knowledge base object.
	 */
	public XPSCaseObject getCaseObject(CaseObjectSource cos) {
		XPSCaseObject co = dynamicStore.get(cos);
		if (co == null) {
			co = cos.createCaseObject(this);
			dynamicStore.put(cos, co);
		}
		return co;
	}

	/**
	 * Returns a flat list of diagnoses, contained in the knowledge base, not
	 * hierarchically ordered.
	 * 
	 * @return a list of Diagnosis instances contained in the knowledge base
	 * @see XPSCase#getDiagnoses()
	 */
	public List<Diagnosis> getDiagnoses() {
		return getKnowledgeBase().getDiagnoses();
	}

	/**
	 * @return a list of Diagnosis instances, which have the specified
	 *         DiagnosisState. All used PSMethods of this case are considered.
	 * @see XPSCase#getDiagnoses(DiagnosisState state)
	 */
	public List<Diagnosis> getDiagnoses(DiagnosisState state) {
		return getDiagnoses(state, getUsedPSMethods());
	}

	/**
	 * @return a list of Diagnosis instances, which have the specified
	 *         DiagnosisState. Only these diagnoses are considered, whose states
	 *         have been set by one of the given PSMethods.
	 */
	public List<Diagnosis> getDiagnoses(DiagnosisState state, List<? extends PSMethod> psMethods) {
		List<Diagnosis> result = new LinkedList<Diagnosis>();
		for (Diagnosis diag : getDiagnoses()) {
			for (PSMethod psm : psMethods) {
				if (psm.isContributingToResult()
						&& diag.getState(this, psm.getClass()).equals(state)) {
					result.add(diag);
					// do not need to look at the remaining psms
					break;
				}
			}
		}
		return result;
	}

	private QASetManagerFactory getQASetManagerFactory() {
		return qamFactory;
	}

	/**
	 * Returns all the Methods used for dialog controlling. Each has to be in
	 * the usedPSMethods-List
	 * 
	 * @return a Collection containing all dialogcontrolling PSMethods
	 */
	public Collection<PSMethod> getDialogControllingPSMethods() {
		return dialogControllingPSMethods;
	}

	/**
	 * @return a list of all established diagnoses
	 */
	public List<Diagnosis> getEstablishedDiagnoses() {
		return establishedDiagnoses;
	}

	/**
	 * @return list of all knowledgeslices for psMethod which fired in theCase
	 * @param diagnosis
	 *            de.d3web.kernel.domainModel.Diagnosis
	 * @param context
	 *            java.lang.Class
	 */
	public List<KnowledgeSlice> getExplanation(Diagnosis diagnosis, PSMethod psMethod) {

		List<KnowledgeSlice> result = new LinkedList<KnowledgeSlice>();

		if (!getDiagnoses().contains(diagnosis) || !getUsedPSMethods().contains(psMethod))
			return result;

		List<? extends KnowledgeSlice> knowledge = diagnosis.getKnowledge(
				psMethod.getClass(), MethodKind.BACKWARD);

		if (knowledge == null) {
			return result;
		}

		for (KnowledgeSlice slice : knowledge) {
			if (slice.isUsed(this))
				result.add(slice);
		}
		return result;
	}

	/**
	 * @return knowledge base used in the case.
	 */
	public KnowledgeBase getKnowledgeBase() {
		return kb;
	}

	/**
	 * @return the instance of a specified PSMethod class definition, which is
	 *         used in this XPSCase; null if the PSMethod-class is not used in
	 *         this case.
	 * @param psmethodClass
	 *            java.lang.Class
	 */
	public PSMethod getPSMethodInstance(Class<? extends PSMethod> context) {
		for (PSMethod psm : getUsedPSMethods()) {
			if (psm.getClass().equals(context)) {
				return (PSMethod) psm;
			}

		}
		trace("<<<ERROR>>> XPSCase.getPSMethodInstance(): Instance for " + context
				+ " not found.");
		return null;
	}

	/**
	 * @return the QASetManager (e.g. DialogController) defined for this XPSCase
	 */
	public QASetManager getQASetManager() {
		if (qaSetManager == null) {
			try {
				qaSetManager = getQASetManagerFactory().createQASetManager(this);
			}
			catch (Exception e) {
				Logger.getLogger(this.getClass().getName()).throwing(
						this.getClass().getName(),
						"initializing dialog controller", e);
				qaSetManager = null;
			}
		}
		return qaSetManager;
	}

	/**
	 * @see XPSCase#getQuestions()
	 */
	public List<? extends Question> getQuestions() {
		return getKnowledgeBase().getQuestions();
	}

	/**
	 * @return List of all problem solving mehods used in this case
	 */
	public List<PSMethod> getUsedPSMethods() {
		return usedPSMethods;
	}

	/**
	 * Checks if there are reasons for finishing the case.
	 * 
	 * @see XPSCase#isFinished()
	 */
	public boolean isFinished() {
		return !finishReasons.isEmpty();
	}

	/**
	 * Propagate new value of a specified ValuedObject to all
	 * Problem-Solving-Methods connected to this ValuedObject (e.g. Question,
	 * Diagnosis)
	 * 
	 * @author joba
	 */
	private void propagateValue(ValuedObject valuedObject, Object[] oldValue, Object[] newValue) {
		// notify the dialog control if questions have been changed
		// ugly implementation, use strategy pattern later
		if (valuedObject instanceof Question) {
			// removeQuestion((Question) valuedObject);
			addAnsweredQuestions((Question) valuedObject);
		}

		// only propagate to ValuedObjects which are
		// NamedObjects (and so have KnowledgeMaps
		if (valuedObject instanceof NamedObject) {
			this.propagationController.propagate((NamedObject) valuedObject, oldValue,
					newValue);
		}
	}

	/**
	 * removes a Diagnosis from the list of established diagnosis
	 * 
	 * @param diag
	 *            Diagnosis to remove
	 */
	public void removeEstablishedDiagnoses(Diagnosis diagnosis) {
		establishedDiagnoses.remove(diagnosis);
	}

	/**
	 * Removes an existing PSMethod from the used PSMethods of this case.
	 * 
	 * @param usedMethod
	 *            PSMethod to remove
	 */
	public void removeUsedPSMethod(PSMethod usedMethod) {
		if (getUsedPSMethods().contains(usedMethod))
			getUsedPSMethods().remove(usedMethod);
	}

	private void setQASetManagerFactory(QASetManagerFactory factory) {
		qamFactory = factory;
	}

	/**
	 * Sets all the Methods used for dialog controlling. Each has to be in the
	 * usedPSMethods-List Creation date: (04.01.2002 13:03:17)
	 * 
	 * @param newDialogControllingPSMethods
	 *            java.util.Collection
	 */
	public void setDialogControllingPSMethods(Collection<? extends PSMethod> newDialogControllingPSMethods) {
		dialogControllingPSMethods = new LinkedList<PSMethod>(
				newDialogControllingPSMethods);
	}

	/**
	 * Adds a new reason for quiting the current case.
	 * 
	 * @see XPSCase#setFinished(boolean f)
	 */
	public void finish(Class<? extends KnowledgeSlice> reasonForFinishCase) {
		finishReasons.add(reasonForFinishCase);
	}

	public Set<Class<? extends KnowledgeSlice>> getFinishReasons() {
		return finishReasons;
	}

	/**
	 * Removes a specified reason from the set of reasons for quiting the case.
	 * 
	 * @param reasonForContinueCase
	 */
	public void continueCase(Class<? extends KnowledgeSlice> reasonForContinueCase) {
		if (finishReasons.contains(reasonForContinueCase))
			finishReasons.remove(reasonForContinueCase);
	}

	public void setQASetManager(QASetManager newQASetManager) {
		qaSetManager = newQASetManager;
	}

	/**
	 * @see XPSCase#setUsedPSMethods(List l)
	 */
	public void setUsedPSMethods(List<? extends PSMethod> usedPSMethods) {
		this.usedPSMethods = new LinkedList<PSMethod>(usedPSMethods);
	}

	/**
	 * Sets the values for a specified question and propagates it to connected
	 * problem-solving-methods. There is no information (context) from where
	 * this setValue was called. Creation date: (28.08.00 17:16:13)
	 * 
	 * @param valuedObject
	 * @param answers
	 */
	public void setValue(ValuedObject valuedObject, Object[] values) {
		// do not know the real context, so send PSMethod.class as context
		setValue(valuedObject, values, PSMethod.class);
	}

	/**
	 * Sets the values for a specified question and propagates it to connected
	 * problem-solving-methods. There is some information (context) given from
	 * where this setValue was called. In this case a rule has set the new value
	 * of a question. Creation date: (28.08.00 17:16:13)
	 * 
	 * @param valuedObject
	 *            ValuedObject
	 * @param answers
	 *            Object[]
	 * @param ruleContext
	 *            rule, which sets the value
	 */
	public void setValue(ValuedObject valuedObject, Object[] values, Rule ruleContext) {
		Object[] oldValue = getValue(valuedObject, null);
		if (valuedObject instanceof Question) {
			((Question) valuedObject).setValue(this, ruleContext, values);
		}
		else {
			valuedObject.setValue(this, values);
		}
		Object[] newValue = getValue(valuedObject, ruleContext.getProblemsolverContext());
		notifyListeners(valuedObject, ruleContext);
		propagateValue(valuedObject, oldValue, newValue);
	}

	/**
	 * Sets the values for a specified question and propagates it to connected
	 * problem-solving-methods. There is some information (context) given from
	 * where this setValue was called. Typically Problemsolvers use this to
	 * state a context for scores of diagnoses (with a context we all know where
	 * to write a diagnosis value). Creation date: (28.08.00 17:16:13)
	 * 
	 * @param namedObject
	 *            ValuedObject
	 * @param answers
	 *            Object[]
	 * @param context
	 *            problem-solver context
	 */
	public void setValue(ValuedObject valuedObject, Object[] values, Class<? extends PSMethod> context) {
		Object[] oldValue = getValue(valuedObject, context);
		if (valuedObject instanceof Diagnosis) {
			((Diagnosis) valuedObject).setValue(this, values, context);
		}
		else {
			valuedObject.setValue(this, values);
		}
		Object[] newValue = getValue(valuedObject, context);
		notifyListeners(valuedObject, context);
		propagateValue(valuedObject, oldValue, newValue);
	}

	private Object[] getValue(ValuedObject valuedObject, Class<? extends PSMethod> context) {
		if (valuedObject instanceof Diagnosis) {
			return new DiagnosisState[] { ((Diagnosis) valuedObject).getState(this) };
		}
		else if (valuedObject instanceof Question) {
			return ((Question) valuedObject).getValue(this).toArray();
		}
		else {
			throw new IllegalStateException("unexpected ValuedObject");
		}
	}

	/**
	 * Gives the specified string as a trace output to the System.out stream.
	 */
	public static void strace(String s) {
		if (TRACE)
			System.out.println("TRACE: " + s);
	}

	/**
	 * Gives the specified string as a trace output to the System.out stream.
	 */
	public void trace(String s) {
		if (TRACE)
			System.out.println("TRACE: " + s);
	}

	protected void finalize() throws Throwable {
		super.finalize();
		trace("D3Webcase finalized!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.supportknowledge.DCMarkedUp#getDCMarkup()
	 */
	public DCMarkup getDCMarkup() {
		return dcMarkup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.kernel.supportknowledge.DCMarkedUp#setDCDMarkup(de.d3web.kernel
	 * .supportknowledge.DCMarkup)
	 */
	public void setDCMarkup(DCMarkup dcMarkup) {
		this.dcMarkup = dcMarkup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.misc.PropertiesAdapter#getProperties()
	 */
	public Properties getProperties() {
		return properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.kernel.misc.PropertiesAdapter#setProperties(de.d3web.kernel.
	 * misc.Properties)
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	// ******************** event notification *********************

	Collection<XPSCaseEventListener> listeners = new LinkedList<XPSCaseEventListener>();

	/**
	 * this listener will be notified, if some value has been set in this case
	 */
	public void addListener(XPSCaseEventListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeListener(XPSCaseEventListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	private void notifyListeners(ValuedObject o, Object context) {
		for (XPSCaseEventListener listener : listeners) {
			listener.notify(this, o, context);
		}
	}

	public Collection<XPSCaseEventListener> getListeners() {
		return listeners;
	}

	public PropagationContoller getPropagationContoller() {
		return propagationController;
	}

	// ******************** /event notification ********************

}